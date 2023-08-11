package designpatterns;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

public class DesignPatterns {

	static double[] solvingTimes = new double [4]; 
	static List<ResourceParameters> resourceParameters = new ArrayList<>();

	public static void main(String[] args) {
		double timeInterval = 0.25; // 0.05 = 3Minuten, 0.125 = 7.5 Minuten
		int timeIntervalInMinutes = (int) (timeInterval*60); 

		int arrayLength = 20; // select price length
		double[] electricityPrice = getElectricityPrice(arrayLength);

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		resource1.setMinPowerInput(0);
		resource1.setMinPowerOutput(0);
		double maxPowerEl = 50; // MW
		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(1000);
		double maxRampPerMinute = 0.05;
		resource1.setMaxRamp(maxRampPerMinute*timeInterval*60*maxPowerEl);

		resource1.createPlaList(21.997,-26.36,0.01,10.6091); // kg/h
		resource1.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource1.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource1.createPlaList(18.787,16.289,16.59741,20.78419);
		resource1.createPlaList(17.864,35.465,20.78419,25.39345);
		resource1.createPlaList(17.132,54.051,25.39345,29.70148);
		resource1.createPlaList(16.609,69.603,29.70148,33.85188);
		resource1.createPlaList(16.058,88.24,33.85188,39.48562);
		resource1.createPlaList(15.622,105.45,39.48562,42.91256);
		resource1.createPlaList(15.308,118.93,42.91256,46.914);
		resource1.createPlaList(14.946,135.92,46.914,50);

		resource1.setNumberOfLinearSegments(resource1.getPla().size());

//		resource1.setNumberOfLinearSegments(0);
		resource1.setEfficiency(18.8);
		resource1.setSlope(17.5);
		resource1.setIntercept(29);

		resource1.addSystemStateWithMaxPowerOutput(0, "off", 0, 9999, new int[] {1}, 0, 0, 0);
		resource1.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7, 0);
		resource1.addSystemState(2, "operation", 2, 9999, new int[] {0}, 7, maxPowerEl);
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		// add object to Array List
		resourceParameters.add(resource1);		

		ResourceParameters resource2 = resource1; 
		resource2.setName("Electrolyzer2");
		resourceParameters.add(resource2);


		ResourceParameters resource3 = new  ResourceParameters();
		resource3.setName("Storage");
		resource3.setMinPowerInput(0);
		resource3.setMaxPowerInput(2000);
		resource3.setMaxPowerOutput(2000);
		resource3.setResourceAsStorage(true);
		resource3.setEfficiencyInputStorage(1);
		resource3.setEfficiencyOutputStorage(1);
		resource3.setInitalCapacity(500);
		resource3.setMaximumStorageCapacity(2000);
		resource3.setStaticPowerLoss(0);
		resourceParameters.add(resource3);

		System.out.println("size resourceParameters "+ resourceParameters.size());

		// convert price array to price array with appropriate resolution
		arrayLength = electricityPrice.length;

		double optimalityGap = 0.001;  // default 10e-4
		optimizationModel(timeInterval, arrayLength, electricityPrice, optimalityGap);
	}



	public static void optimizationModel(double timeInterval, int arrayLength, double[] electricityPrice, 	double optimalityGap) {
		try {
			@SuppressWarnings("resource")
			IloCplex cplex = new IloCplex();

			//			additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 500*timeInterval;

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			// RESOURCE 1 - Electrolyzer 1
			IloNumVar[] powerInput = cplex.numVarArray(arrayLength,  0 ,  maxPowerSystem);
			IloNumVar[] powerInputElectrolyzer1 = cplex.numVarArray(arrayLength, resourceParameters.get(0).getMinPowerInput(), resourceParameters.get(0).getMaxPowerInput());
			IloNumVar[] powerOutputElectrolyzer1 = cplex.numVarArray(arrayLength, resourceParameters.get(0).getMinPowerOutput(), resourceParameters.get(0).getMaxPowerOutput());
			IloIntVar[][] binariesPlaElectrolyzer1 = new IloIntVar[resourceParameters.get(0).getNumberOfLinearSegments()][arrayLength]; 
			IloNumVar[][] powerInputElectrolyzer1LinearSegments = new IloNumVar[resourceParameters.get(0).getNumberOfLinearSegments()][arrayLength]; 

			IloIntVar[][] statesIntArrayElectrolyzer1 = new IloIntVar[arrayLength][resourceParameters.get(0).getNumberOfSystemStates()];

			for (int i = 0; i < arrayLength; i++) {
				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					statesIntArrayElectrolyzer1[i][j] = cplex.intVar(0, 1);
				}
				//				statesIntArrayElectrolyzer1[i] = cplex.intVarArray(resourceParameters.get(0).getNumberOfSystemStates(), 0 ,1);
			}

			for (int i = 0; i < resourceParameters.get(0).getNumberOfLinearSegments(); i++) {
				binariesPlaElectrolyzer1[i] = cplex.intVarArray(arrayLength, 0 ,1);
				powerInputElectrolyzer1LinearSegments[i] = cplex.numVarArray(arrayLength,resourceParameters.get(0).getMinPowerInput(), resourceParameters.get(0).getMaxPowerInput());
			}

			IloNumVar[] combinedHydrogenOutput = cplex.numVarArray(arrayLength,  0 ,  Double.MAX_VALUE);

			// RESOURCE 3 - Storage
			IloNumVar[] powerInputStorage = cplex.numVarArray(arrayLength, resourceParameters.get(2).getMinPowerOutput(), resourceParameters.get(2).getMaxPowerOutput());
			IloNumVar[] stateOfCharge = cplex.numVarArray(arrayLength+1, resourceParameters.get(2).getMinimumStorageCapacity(), resourceParameters.get(2).getMaximumStorageCapacity());
			IloNumVar[] powerOutputStorage = cplex.numVarArray(arrayLength, resourceParameters.get(2).getMinPowerOutput(), resourceParameters.get(2).getMaxPowerOutput());



			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------

			// Design Patterns
			// ---------------------------------- power input system ----------------------------------
			for (int i = 0; i < arrayLength; i++) {
				// power input system = sum (power input electrolyzer r)
				cplex.addEq(powerInput[i], powerInputElectrolyzer1[i]);

				// hydrogen output = sum (power output electrolyzer r)
				// dependency correlative
				cplex.addEq(powerInputStorage[i], powerOutputElectrolyzer1[i]);

				// dependency correlative
				// constant hydrogen demand must be met by either storage output and/or production
				cplex.addEq(powerOutputStorage[i], constantHydrogenDemand);
			}

			// Pattern input output
			if (resourceParameters.get(0).getNumberOfLinearSegments()==0
					&& resourceParameters.get(0).getEfficiency()!=0
					) {
				// Case 1: efficiency
				System.out.println("Case 1: efficiency");
				for (int i = 0; i < arrayLength; i++) {
					cplex.addEq(powerOutputElectrolyzer1[i], 
							cplex.prod(powerInputElectrolyzer1[i], resourceParameters.get(0).getEfficiency())
							);
				} 
			} else if (resourceParameters.get(0).getNumberOfLinearSegments()==0
					&& resourceParameters.get(0).getEfficiency()==0
					&& resourceParameters.get(0).getSlope()!=0) {
				for (int i = 0; i < arrayLength; i++) {
					cplex.addEq(powerOutputElectrolyzer1[i], 
							cplex.sum(
									resourceParameters.get(0).getIntercept(),
									cplex.prod(powerInputElectrolyzer1[i], resourceParameters.get(0).getSlope())
									)
							);

				}
			} else if (resourceParameters.get(0).getNumberOfLinearSegments()>0) {
				// Case 3: pla
				System.out.println("Case 3: pla");
				for (int i = 0; i < arrayLength; i++) {
					// sum binaries[i] = 1 part 1
					IloNumExpr binarySumElectrolyzer1 = cplex.numExpr();

					// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 1
					IloNumExpr powerInputSumElectrolyzer1 = cplex.numExpr();


					for (int j = 0; j < resourceParameters.get(0).getNumberOfLinearSegments(); j++) {

						// lowerBound * power <= binary
						cplex.addLe(
								cplex.prod(
										resourceParameters.get(0).getPla().get(j).getLowerBound(), 
										binariesPlaElectrolyzer1[j][i]
										), 
								powerInputElectrolyzer1LinearSegments[j][i]
								);

						//power <= upperBound * binary
						cplex.addLe(
								powerInputElectrolyzer1LinearSegments[j][i], 
								cplex.prod(
										resourceParameters.get(0).getPla().get(j).getUpperBound(), 
										binariesPlaElectrolyzer1[j][i]
										)
								);

						// sum binaries[i] = 1 part 2
						//						binarySumElectrolyzer1.addTerm(binariesPlaElectrolyzer1[j][i], 1);
						binarySumElectrolyzer1 = cplex.sum(binarySumElectrolyzer1,binariesPlaElectrolyzer1[j][i]);
						// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 2
						//						powerInputSumElectrolyzer1.addTerm(powerInputElectrolyzer1LinearSegments[j][i], 1);
						powerInputSumElectrolyzer1 = cplex.sum(powerInputSumElectrolyzer1, powerInputElectrolyzer1LinearSegments[j][i]);
					}

					// sum binaries[i] = 1 part 3           
					cplex.addEq(binarySumElectrolyzer1, 1);

					// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 3
					cplex.addEq(powerInputSumElectrolyzer1, powerInputElectrolyzer1[i]);

					for (int j = 0; j < resourceParameters.get(0).getNumberOfLinearSegments(); j++) {
						cplex.add(
								cplex.ifThen(
										cplex.and(cplex.le(binariesPlaElectrolyzer1[j][i], 1),
												cplex.ge(binariesPlaElectrolyzer1[j][i], 0.9)
												),
										cplex.eq(powerOutputElectrolyzer1[i],
												cplex.sum(
														resourceParameters.get(0).getPla().get(j).getIntercept(),
														cplex.prod(
																powerInputElectrolyzer1LinearSegments[j][i],
																resourceParameters.get(0).getPla().get(j).getSlope()
																)
														)
												)
										)
								);
					}
				}
			}





			//			// --------------------- ramp limits ----------------------------------
			//			for (int i = 1; i < arrayLength; i++) {
			//				//				TODO was ist für das erste Intervall? 
			//				cplex.addGe(cplex.diff(powerInputElectrolyzer1[i], powerInputElectrolyzer1[i-1]), resourceParameters.get(0).getMinRamp());
			//				cplex.addLe(cplex.diff(powerInputElectrolyzer1[i], powerInputElectrolyzer1[i-1]), resourceParameters.get(0).getMaxRamp());
			//			}


			// --------------------- System state ----------------------------------

			for (int i = 0; i < arrayLength; i++) {

				cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[i]), 1);

				IloNumExpr powerMinSum = cplex.numExpr();
				IloNumExpr powerMaxSum = cplex.numExpr();
				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					powerMinSum = cplex.sum(powerMinSum, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMinPower()));
					powerMaxSum = cplex.sum(powerMaxSum, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMaxPower()));
				}
				cplex.addGe(powerInputElectrolyzer1[i], powerMinSum);
				cplex.addLe(powerInputElectrolyzer1[i], powerMaxSum);

				/**
				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					cplex.addLe(powerInputElectrolyzer1[i], cplex.prod(statesIntArrayElectrolyzer1[i][j],  resourceParameters.get(0).getSystemStates().get(j).getMaxPower()));
					cplex.addGe(powerInputElectrolyzer1[i], cplex.prod(statesIntArrayElectrolyzer1[i][j],  resourceParameters.get(0).getSystemStates().get(j).getMinPower()));
					System.out.println(j + " min " + resourceParameters.get(0).getSystemStates().get(j).getMinPower() + " max " + resourceParameters.get(0).getSystemStates().get(j).getMaxPower());
				}
				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					cplex.add(cplex.ifThen(
							cplex.eq(statesIntArrayElectrolyzer1[i][j],1), 
							cplex.and(
									cplex.and(
											cplex.ge(powerInputElectrolyzer1[i], 
													resourceParameters.get(0).getSystemStates().get(j).getMinPower()
													), 
											cplex.le(powerInputElectrolyzer1[i], 
													resourceParameters.get(0).getSystemStates().get(j).getMaxPower()
													)
											),
									cplex.le(powerOutputElectrolyzer1[i], 
											resourceParameters.get(0).getSystemStates().get(j).getMaxPowerOutput()
											) 							
									)
							)
							);
				 */

				//					// operating state = power output > 0
				//					// other states = power output == 0
				//					cplex.add(cplex.ifThen(
				//							cplex.eq(statesIntArrayElectrolyzer1[i][1],0), 
				//							)
				//							);
			}


			//			cplex.addEq(powerInput[10], 0);


			/**
			// --------------------- State sequences and holding duration ----------------------------------

			// initial state	
			//Constraints nur für Zeitpunkt 0: Zustand 0 = 1, alle anderen 0, start in state = off 

			cplex.addEq(statesIntArrayElectrolyzer1[0][0], 1);
			cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[0]), 1);

			for (int i = 1; i < arrayLength; i++) {

				// only one active state per time step
				cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[i]), 1);

				for (int s = 0; s < resourceParameters.get(0).getNumberOfSystemStates(); s++) {
					// min duration
					if (resourceParameters.get(0).getSystemStates().get(s).getMinStateDuration() == 99999.0) {

					}else{
						IloNumExpr constraintLeftSide = cplex.diff(statesIntArrayElectrolyzer1[i][s],statesIntArrayElectrolyzer1[i-1][s]);
						IloNumExpr constraintRightSide = statesIntArrayElectrolyzer1[i][s];

						int counter = 1;
						for (int r = 1; r < resourceParameters.get(0).getSystemStates().get(s).getMinStateDuration(); r++) {

							try {
								constraintRightSide = cplex.sum(constraintRightSide,statesIntArrayElectrolyzer1[i+r][s]);
								counter++; 

							} catch (Exception e) {
								// TODO: handle exception
							} 
						}
						cplex.addLe(cplex.prod(constraintLeftSide, Math.min(counter, resourceParameters.get(0).getSystemStates().get(s).getMinStateDuration())), constraintRightSide);
					}
					//max Duration
					if (resourceParameters.get(0).getSystemStates().get(s).getMaxStateDuration() == 99999) {

					}else{
						IloNumExpr constraintLeftSide = statesIntArrayElectrolyzer1[i][s];

						int counter = 1;
						for (int r = 1; r < resourceParameters.get(0).getSystemStates().get(s).getMaxStateDuration()+1; r++) {

							try {
								constraintLeftSide = cplex.sum(constraintLeftSide,statesIntArrayElectrolyzer1[i+r][s]);
								counter++; 

							} catch (Exception e) {
								// TODO: handle exception
							} 
						}
						cplex.addLe(constraintLeftSide, Math.min(counter,resourceParameters.get(0).getSystemStates().get(s).getMaxStateDuration()));
					}
					//State sequences
					IloNumExpr constraintLeftSide = cplex.diff(statesIntArrayElectrolyzer1[i-1][s],statesIntArrayElectrolyzer1[i][s]);
					IloNumExpr constraintRightSide = statesIntArrayElectrolyzer1[i][resourceParameters.get(0).getSystemStates().get(s).getFollowerStates()[0]];

					for (int r = 1; r < resourceParameters.get(0).getSystemStates().get(s).getFollowerStates().length; r++) {
						constraintRightSide = cplex.sum(constraintRightSide, statesIntArrayElectrolyzer1[i][resourceParameters.get(0).getSystemStates().get(s).getFollowerStates()[r]]);
					}
					cplex.addLe(constraintLeftSide, constraintRightSide);
				}
			}
			 */

			// --------------------- storage and loss ----------------------------------
			for (int i = 0; i < arrayLength+1; i++) {
				if(i==0) {
					cplex.addEq(stateOfCharge[i], resourceParameters.get(2).getInitalCapacity());
				} else {
					cplex.addEq(stateOfCharge[i], 
							cplex.sum(
									stateOfCharge[i-1],
									cplex.diff(
											cplex.prod(powerInputStorage[i-1], resourceParameters.get(2).getEfficiencyInputStorage()*timeInterval),
											cplex.sum(
													cplex.prod(powerOutputStorage[i-1], resourceParameters.get(2).getEfficiencyOutputReciprocal()*timeInterval),
													resourceParameters.get(2).getStaticPowerLoss()*timeInterval
													)
											)
									)
							);
				}
			}
			cplex.addLe(stateOfCharge[arrayLength], 200);
			cplex.addGe(stateOfCharge[arrayLength], 100);
			//			----------------------------------



			//	System.out.println(cplex);
			cplex.exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < arrayLength; i++) {
				objective.addTerm(timeInterval*electricityPrice[i], powerInput[i]);
			}
			cplex.addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, optimalityGap);
			long start = System.currentTimeMillis();

			if (cplex.solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+cplex.getObjValue());
				System.out.println(cplex.getCplexStatus());
				double [][] optimizationResults = new double [arrayLength+1][100];

				for (int i = 0; i < arrayLength+1; i++) {
					optimizationResults[i][0] = cplex.getValue(stateOfCharge[i]);
				}
				for (int i = 1; i < arrayLength+1; i++) {
					optimizationResults[i][1] = cplex.getValue(powerInputElectrolyzer1[i-1]);
					for (int j = 0; j < resourceParameters.get(0).getNumberOfLinearSegments(); j++) {
						optimizationResults[i][2+j] = cplex.getValue(binariesPlaElectrolyzer1[j][i-1]);
					}
					optimizationResults[i][12] = cplex.getValue(powerOutputElectrolyzer1[i-1]);
					optimizationResults[i][13] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][0]);
					optimizationResults[i][14] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][1]);
					optimizationResults[i][15] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][3]);
					optimizationResults[i][16] = electricityPrice[i-1];
				}

				writeResultsToFile(optimizationResults, "designpatterns", "stateOfCharge, powerInput, binary1, binary2, binary3, binary4, binary5, binary6, binary7, binary8, binary9, binary10, powerOutput, systemState1, systemState2, electricityPrice");

			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
	}


	private static double[] getElectricityPrice(int i) {
		double[] electricityPrice = new double[] {
				8540.87,37.21,320.62,32.27,51044.03,63.76,44.84,39.2,58.244448,4344444.46,42.47,47.48, 58.96,42.99,43.27,50.5,
				80.21,30.64,21.47,35.41,15.67,24.15,43.98,55.36,23.58,42.56,60.2,87.49,22.46,38.95,	31.68,60.19,
				17.49,37.23,40.51,57.98,46.62,46.74,61.23,71.33,57.2,55.6,59.39,81.35,48.79,58.3,58.01,57.73,71.85,
				64.5,57.07,57.84,44.25,38.6,65.02,67.76,42.31,62.06,83.36,100.31,57.25,66.51,84.52,95.44,73.57,84.5,
				85.41,98.72,75.05,98.16,101.62,96.88,127.03,92.24,102.1,79.11,140.98,93.1,91.67,66.07,125.87,89.96,
				76.41,48.12,100.5,74.96,74.03,64.77,100.75,75.26,87.21,38.04,85.35,60.38,56.9,35.95
		};
		double[] electricityPriceNew = new double[i];


		if (i>electricityPrice.length) {
			for (int j = 0; j < electricityPrice.length; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			for (int k = electricityPrice.length; k<i; k++) {
				electricityPriceNew[k] = electricityPrice[(int) (Math.random()*electricityPrice.length)];
			}
			return electricityPriceNew;
		} else {
			for (int j = 0; j < i; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			return electricityPriceNew; 
		}
	}

	public static void writeResultsToFile (double[][] contentToWrite, String fileName, String header) {

		try {
			//	double currentTime = System.currentTimeMillis(); 
			FileWriter myWriter = new FileWriter("C:/Users/Wagner/OneDrive - Helmut-Schmidt-Universität/Papers/Oncon2023/"+fileName+".csv");
			//			write objective value in first row
			//			myWriter.write(Double.toString(objValue));
			//			myWriter.write("\n");
			myWriter.write("id,"+header);
			myWriter.write("\n");
			for (int i = 0; i < contentToWrite.length; i++) {
				myWriter.write(Double.toString(i));
				for(int j = 0; j < contentToWrite[0].length; j++) {
					myWriter.write(",");
					myWriter.write(Double.toString(contentToWrite[i][j]));
				}
				myWriter.write("\n");
			}
			myWriter.close();
			System.out.println("Successfully wrote data to the file "+ fileName+".csv.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
