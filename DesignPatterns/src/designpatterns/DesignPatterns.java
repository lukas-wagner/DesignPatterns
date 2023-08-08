package designpatterns;


import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; 

import ilog.concert.*;
import ilog.cplex.*;

public class DesignPatterns {

	static double[] solvingTimes = new double [4]; 
	static List<ResourceParameters> resourceParameters = new ArrayList<>();

	public static void main(String[] args) {
		double timeInterval = 0.25; // 0.05 = 3Minuten, 0.125 = 7.5 Minuten
		int timeIntervalInMinutes = (int) (timeInterval*60); 

		int arrayLength = 96; // select price length
		double[] electricityPrice = getElectricityPrice(arrayLength);

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		resource1.setMinPowerInput(0);
		double maxPowerEl = 50;
		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(maxPowerEl);
		resource1.setEfficiency(.36);
		double maxRampPerMinute = 0.05;
//		resource1.setMaxRamp(maxRampPerMinute*timeInterval*60*maxPowerEl);
//		resource1.createPlaList(21.997,-26.36,0,10.6091);
//		resource1.createPlaList(20.754,-13.173,10.6091,13.31882);
//		resource1.createPlaList(19.782,-0.23309,13.31882,16.59741);
//		resource1.createPlaList(18.787,16.289,16.59741,20.78419);
//		resource1.createPlaList(17.864,35.465,20.78419,25.39345);
//		resource1.createPlaList(17.132,54.051,25.39345,29.70148);
//		resource1.createPlaList(16.609,69.603,29.70148,33.85188);
//		resource1.createPlaList(16.058,88.24,33.85188,39.48562);
//		resource1.createPlaList(15.622,105.45,39.48562,42.91256);
//		resource1.createPlaList(15.308,118.93,42.91256,46.914);
//		resource1.createPlaList(14.946,135.92,46.914,100);
//		resource1.setNumberOfLinearSegments(resource1.getPla().size());
		// add object to Array List
		resourceParameters.add(resource1);		

		ResourceParameters resource2 = new  ResourceParameters();
		resource2.setName("Electrolyzer2");
		resource2.setMinPowerInput(0);
		resource2.setMaxPowerInput(maxPowerEl);
		resource2.setMaxPowerOutput(maxPowerEl);
		resource2.createPlaList(21.997,-26.36,0,10.6091);
		resource2.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource2.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource2.createPlaList(18.787,16.289,16.59741,20.78419);
		resource2.createPlaList(17.864,35.465,20.78419,25.39345);
		resource2.createPlaList(17.132,54.051,25.39345,29.70148);
		resource2.createPlaList(16.609,69.603,29.70148,33.85188);
		resource2.createPlaList(16.058,88.24,33.85188,39.48562);
		resource2.createPlaList(15.622,105.45,39.48562,42.91256);
		resource2.createPlaList(15.308,118.93,42.91256,46.914);
		resource2.createPlaList(14.946,135.92,46.914,100);

		resource2.setNumberOfLinearSegments(resource2.getPla().size());
//		System.out.println("size pla 2 " + resource2.getPla().size());
		resourceParameters.add(resource2);


		ResourceParameters resource3 = new  ResourceParameters();
		resource3.setName("Storage");
		resource3.setMinPowerInput(0);
		resource3.setMaxPowerInput(maxPowerEl);
		resource3.setMaxPowerOutput(maxPowerEl);
		resource3.setResourceAsStorage(true);
		resource3.setEfficiencyInputStorage(1);
		resource3.setEfficiencyOutputStorage(1);
		resource3.setInitalCapacity(500);
		resource3.setMaximumStorageCapacity(2000);
		resource3.setStaticPowerLoss(1);
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
			double maxPowerSystem = 1000; 
			double constantHydrogenDemand = 80*timeInterval;
			int bigM = 1000; 

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			// RESOURCE 1 - Electrolyzer 1
			IloNumVar[] powerInput = cplex.numVarArray(arrayLength,  0 ,  maxPowerSystem);
			IloNumVar[] powerInputElectrolyzer1 = cplex.numVarArray(arrayLength, resourceParameters.get(0).getMinPowerInput(), resourceParameters.get(0).getMaxPowerInput());
			IloNumVar[] powerOutputElectrolyzer1 = cplex.numVarArray(arrayLength, resourceParameters.get(0).getMinPowerOutput(), resourceParameters.get(0).getMaxPowerOutput());
			IloIntVar[][] binariesPlaElectrolyzer1 = new IloIntVar[resourceParameters.get(0).getNumberOfLinearSegments()][arrayLength]; 
			IloIntVar[][] powerInputElectrolyzer1LinearSegments = new IloIntVar[resourceParameters.get(0).getNumberOfLinearSegments()][arrayLength]; 

			for (int i = 0; i < resourceParameters.get(0).getNumberOfLinearSegments(); i++) {
				binariesPlaElectrolyzer1[i] = cplex.intVarArray(arrayLength, 0 ,1);
				powerInputElectrolyzer1LinearSegments[i] = cplex.intVarArray(arrayLength, 0 ,1);
			}

			// RESOURCE 2 - Electrolyzer 2
			IloNumVar[] powerInputElectrolyzer2 = cplex.numVarArray(arrayLength, resourceParameters.get(1).getMinPowerInput(), resourceParameters.get(1).getMaxPowerInput());
			IloNumVar[] powerOutputElectrolyzer2 = cplex.numVarArray(arrayLength, resourceParameters.get(1).getMinPowerOutput(), resourceParameters.get(1).getMaxPowerOutput());
			IloIntVar[][] binariesPlaElectrolyzer2 = new IloIntVar[resourceParameters.get(1).getNumberOfLinearSegments()][arrayLength]; 
			IloIntVar[][] powerInputElectrolyzer2LinearSegments = new IloIntVar[resourceParameters.get(1).getNumberOfLinearSegments()][arrayLength]; 

			for (int i = 0; i < resourceParameters.get(1).getNumberOfLinearSegments(); i++) {
				binariesPlaElectrolyzer2[i] = cplex.intVarArray(arrayLength, 0 ,1);
				powerInputElectrolyzer2LinearSegments[i] = cplex.intVarArray(arrayLength, 0 ,1);
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
				cplex.addEq(powerInput[i], cplex.sum(powerInputElectrolyzer1[i], powerInputElectrolyzer2[i]));

				// hydrogen output = sum (power output electrolyzer r)
				// dependency correlative
				cplex.addEq(combinedHydrogenOutput[i], cplex.sum(powerOutputElectrolyzer1[i], powerOutputElectrolyzer2[i]));

				// dependency correlative
				// constant hydrogen demand must be met by either storage output and/or production
				cplex.addEq(constantHydrogenDemand, cplex.sum(powerOutputStorage[i], combinedHydrogenOutput[i]));
			}

			// Pattern input output
			if (resourceParameters.get(0).getNumberOfLinearSegments()==0
					&& resourceParameters.get(0).getEfficiency()!=0
					&& resourceParameters.get(0).getSlope()==0
					&& resourceParameters.get(0).getIntercept()==0
					) {
				// Case 1: efficiency
				for (int i = 0; i < arrayLength; i++) {
					cplex.addEq(powerOutputElectrolyzer1[i], 
							cplex.prod(powerInputElectrolyzer1[i], resourceParameters.get(0).getEfficiency())
							);
				}
			} /**else if (resourceParameters.get(0).getNumberOfLinearSegments()==0
					&& (resourceParameters.get(0).getSlope()!=0
					|| resourceParameters.get(0).getIntercept()!=0)) {
				// Case 2: linear relationship y= ax+b
				for (int i = 0; i < arrayLength; i++) {
					cplex.addEq(powerOutputElectrolyzer1[i], 
							cplex.sum(
									resourceParameters.get(0).getIntercept(), 
									cplex.prod(powerInputElectrolyzer1[i], resourceParameters.get(0).getSlope()
											)
									)
							);
				} 
				/**
			} else if (resourceParameters.get(0).getNumberOfLinearSegments()>0) {
				// Case 3: pla
				for (int i = 0; i < arrayLength; i++) {
					// sum binaries[i] = 1 part 1
					IloLinearNumExpr binarySumElectrolyzer1 = cplex.linearNumExpr();
					
					// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 1
					IloLinearNumExpr powerInputSumElectrolyzer1 = cplex.linearNumExpr();
					
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
						binarySumElectrolyzer1.addTerm(binariesPlaElectrolyzer1[j][i], 1);
						
						// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 2
						powerInputSumElectrolyzer1.addTerm(powerInputElectrolyzer1LinearSegments[j][i], 1);
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
			**/

//			// --------------------- ramp limits ----------------------------------
//			for (int i = 1; i < arrayLength; i++) {
//				//				TODO was ist für das erste Intervall? 
//				cplex.addGe(cplex.diff(powerInputElectrolyzer1[i], powerInputElectrolyzer1[i-1]), resourceParameters.get(0).getMinRamp());
//				cplex.addLe(cplex.diff(powerInputElectrolyzer1[i], powerInputElectrolyzer1[i-1]), resourceParameters.get(0).getMaxRamp());
//			}


			// --------------------- storage and loss ----------------------------------
			for (int i = 0; i < arrayLength+1; i++) {
				if(i==0) {
					cplex.addEq(stateOfCharge[i], resourceParameters.get(2).getInitalCapacity());
				} else {
					cplex.addEq(stateOfCharge[i], 
							cplex.diff(
									cplex.prod(powerInputStorage[i-1], resourceParameters.get(2).getEfficiencyInputStorage()*timeInterval),
									cplex.sum(
											cplex.prod(powerOutputStorage[i-1], resourceParameters.get(2).getEfficiencyOutputReciprocal()*timeInterval),
											resourceParameters.get(2).getStaticPowerLoss()*timeInterval
											)
									)
							);
				}
			}

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
				
				for (int i = 0; i < arrayLength; i++) {
					System.out.println(cplex.getValue(stateOfCharge[i]));
				}

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
				85.87,37.21,32.62,32.27,51.03,63.76,44.84,39.2,58.28,43.46,42.47,47.48, 58.96,42.99,43.27,50.5,
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



}
