package designpatterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class ElectrolyzerOptimization2 {
	/** The nolimit. */
	static final double NOLIMIT = 9999;

	/** The Constant INPUT. */
	static final String INPUT = "Input";

	/** The Constant OUTPUT. */
	static final String OUTPUT = "Output";

	/** The Constant SOC. */
	static final String SOC = "SOC";

	/** The Constant POWER. */
	static final String POWER = "Power";

	/** The Constant BINARY. */
	static final String BINARY = "Binary";

	/** The Constant SEGMENT. */
	static final String SEGMENT = "Segment";

	/** The Constant STATE. */
	static final String STATE = "State";
	
	static final int ONLYONE = -1; 
	
	static double startupCost = 10; 
	static double constHydrDemand = 900; 

	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
//		electrolyzerBaseModel();
		electrolyzerModelI();
		electrolyzerModelII();
		}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {

		designpatterns.DesignPatterns.setOptimalityGap(0.01); // default 10e-4 = 0.001
		designpatterns.DesignPatterns.setTimeInterval(0.25); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		designpatterns.DesignPatterns.setArrayLength(10); // set arrayLength in # of time steps

		double maxPowerEl = 52.25; // MW

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		//		resource1.setEnergyCarrier(POWER);
		resource1.setEnergyCarrierInputs(Arrays.asList(POWER, POWER));
		//		resource1.setMinPowerInput(0);
		resource1.setMinPowerInputs(Arrays.asList(0.0, 0.0));
		resource1.setMaxPowerInputs(Arrays.asList(maxPowerEl, maxPowerEl));
		resource1.setMinPowerOutput(0);
		//		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(1000);

		List<PiecewiseLinearApproximation> input1 = new ArrayList<PiecewiseLinearApproximation>();
		input1.add(DesignPatterns.setPla(0,0,0,7.84));
		input1.add(DesignPatterns.setPla(-20.587,21.361, 7.84, 14.86643));
		input1.add(DesignPatterns.setPla(24.303,18.342, 14.86643, 25.89245));
		input1.add(DesignPatterns.setPla(70.154, 16.571,25.89245, 38.15582));
		input1.add(DesignPatterns.setPla(118.41,15.306, 38.15582, maxPowerEl));

		resource1.getPlaList().add(input1);
		
		double minRamp = 0;
		double maxRamp = 0.8*maxPowerEl/DesignPatterns.getTimeInterval(); 
		resource1.setNumberOfInputs(resource1.getPlaList().size());
//		resource1.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
//		resource1.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 7.85, 7.85, 0);
//		resource1.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7.85, maxPowerEl);
//		resource1.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 4, new int[] {2,4}, 0.52,0.52, 0);
//		resource1.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 7.85, 7.85, 0);
		
		resource1.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource1.addSystemStateWithMaxPowerOutputAndRampInput(1, "start-up", 2, 2, new int[] {2}, 7.84, 7.84, 0, minRamp,  maxRamp);
		resource1.addSystemStateWithRamp(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7.85, maxPowerEl, minRamp,  maxRamp);
		resource1.addSystemStateWithMaxPowerOutputAndRampInput(3, "stand-by", 0, 4, new int[] {2,4}, 0.52,0.52, 0, minRamp,  maxRamp);
		resource1.addSystemStateWithMaxPowerOutputAndRampInput(4, "shut down", 2, 2, new int[] {0}, 7.84, 7.84, 0, minRamp,  maxRamp);
		
		
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		resource1.setInitialSystemState(2);

		// add object to Array List
		DesignPatterns.getResourceParameters().add(resource1);

		ResourceParameters resource2 = new  ResourceParameters();
		resource2.setName("Electrolyzer2");
		//		resource2.setEnergyCarrier(POWER);
		resource2.setEnergyCarrierInputs(Arrays.asList(POWER));
		//		resource2.setMinPowerInput(0);
		resource2.setMinPowerInputs(Arrays.asList(0.0));
		resource2.setMaxPowerInputs(Arrays.asList(maxPowerEl));
		resource2.setMinPowerOutput(0);
		//		resource2.setMaxPowerInput(maxPowerEl);
		resource2.setMaxPowerOutput(1000);

		resource2.getPlaList().add(input1);

		resource2.setNumberOfInputs(resource2.getPlaList().size());
		
//		resource2.setSecondaryResource(true);
//		resource2.setPrimaryResource("Electrolyzer1");
		
//		resource2.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
//		resource2.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 7.85, 7.85, 0);
//		resource2.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7.85, maxPowerEl);
//		resource2.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 4, new int[] {2,4}, 0.52,0.52, 0);
//		resource2.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 7.85, 7.85, 0);
		
		resource2.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource2.addSystemStateWithMaxPowerOutputAndRampInput(1, "start-up", 2, 2, new int[] {2}, 7.84, 7.84, 0, minRamp,  maxRamp);
		resource2.addSystemStateWithRamp(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7.85, maxPowerEl, minRamp,  maxRamp);
		resource2.addSystemStateWithMaxPowerOutputAndRampInput(3, "stand-by", 0, 4, new int[] {2,4}, 0.52,0.52, 0, minRamp,  maxRamp);
		resource2.addSystemStateWithMaxPowerOutputAndRampInput(4, "shut down", 2, 2, new int[] {0}, 7.84, 7.84, 0, minRamp,  maxRamp);
		
		resource2.setNumberOfSystemStates(resource2.getSystemStates().size());
		resource2.setInitialSystemState(2);
		DesignPatterns.getResourceParameters().add(resource2);

		ResourceParameters resource3 = new  ResourceParameters();
		resource3.setName("Storage");
		resource3.setEnergyCarrier(POWER);
		resource3.setMinPowerInput(0);
		resource3.setMaxPowerInput(2000);
		resource3.setMaxPowerOutput(100000);
		resource3.setResourceAsStorage(true);
		resource3.setEfficiencyInputStorage(1);
		resource3.setEfficiencyOutputStorage(1);
		resource3.setMaximumStorageCapacity(4000);
		resource3.getCapacitySetPoints().put(0, (double) 2000);
		resource3.getCapacitySetPoints().put(DesignPatterns.getArrayLength(), (double) 2000);
		resource3.setStaticEnergyLoss(10);
		//		resource3.setDynamicEnergyLoss(0);
		//		resource3.setReferenceDynamicEnergyLoss(resource3.maximumStorageCapacity);
		DesignPatterns.getResourceParameters().add(resource3);
	}

	/**
	 * Electrolyzer base model.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void electrolyzerBaseModel () throws IloException {
		String nameOfModel = "baseModel";
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = constHydrDemand;//*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Create Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, ONLYONE, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, 0, POWER), 
//						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, 1, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0, POWER), 
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, ONLYONE,  POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE, POWER), 
					}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, ONLYONE, POWER)
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE, POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, ONLYONE, POWER)}
					);

			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer1");
			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer2");

			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("Storage");

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int timestep = 0; timestep < designpatterns.DesignPatterns.getArrayLength(); timestep++) {
				objective.addTerm(
						designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[timestep], 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE,  POWER)[timestep]
						);
			}
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				int sizeOfResultsMatrix = designpatterns.DesignPatterns.getDecisionVariablesMatrix().size()*40+designpatterns.DesignPatterns.getDecisionVariablesVector().size(); 

				double [][] optimizationResults = new double [designpatterns.DesignPatterns.getArrayLength()+1][sizeOfResultsMatrix];

				for (int i = 1; i < designpatterns.DesignPatterns.getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT,0, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT,ONLYONE, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT,ONLYONE, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", SOC, ONLYONE,POWER)[i]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0, SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0, BINARY)[j][i-1]);
						counter++; 
					}

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0, SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0, BINARY)[j][i-1]);
						counter++; 
					}


				}

				String headerOptimizationResults = null; 
				headerOptimizationResults ="System-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-SOC";
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-Binary-"+Integer.toString(j);
				}
				String filePath = "C:/Users/Wagner/OneDrive - Helmut-Schmidt-Universität/Papers/Oncon2023/results/";
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "dp-"+nameOfModel, headerOptimizationResults, filePath);
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
	}

	/**
	 * Electrolyzer  model i.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void electrolyzerModelI () throws IloException {
		String nameOfModel = "model1";
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = constHydrDemand;//2*1100*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(
						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, -1, POWER)[i], 
						constantHydrogenDemand
						);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, 0, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0, POWER),
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, ONLYONE, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE,POWER),
					}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT,ONLYONE, POWER)
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE,POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, ONLYONE,POWER)}
					);

			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer1");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("Electrolyzer1");
			designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration("Electrolyzer1");
			designpatterns.DesignPatterns.generateRampLimits("Electrolyzer1", INPUT);


			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer2");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("Electrolyzer2");
			designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration("Electrolyzer2");
			designpatterns.DesignPatterns.generateRampLimits("Electrolyzer2", INPUT);

			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("Storage");

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(
						designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)[i]
								);
				objective.addTerm(startupCost, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1", POWER,ONLYONE, STATE)[i][1]);
				objective.addTerm(startupCost, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2", POWER,ONLYONE, STATE)[i][1]);
			}
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				int sizeOfResultsMatrix = designpatterns.DesignPatterns.getDecisionVariablesMatrix().size()*40+designpatterns.DesignPatterns.getDecisionVariablesVector().size(); 

				double [][] optimizationResults = new double [designpatterns.DesignPatterns.getArrayLength()+1][sizeOfResultsMatrix];

				for (int i = 1; i < designpatterns.DesignPatterns.getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT,0, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, ONLYONE, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", SOC, ONLYONE,POWER)[i]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0, SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0, BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,ONLYONE,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,ONLYONE,STATE)[i][j]);
						counter++; 
					}

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0, SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0, BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE, STATE)[i][j]);
						counter++; 
					}


				}

				String headerOptimizationResults = null; 
				headerOptimizationResults ="System-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-SOC";

				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-State-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-BINARY-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-STATE-"+Integer.toString(j);
				}
				String filePath = "C:/Users/Wagner/OneDrive - Helmut-Schmidt-Universität/Papers/Oncon2023/results/";
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "dp-"+nameOfModel, headerOptimizationResults, filePath);
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
	}


	/**
	 * Electrolyzer  model ii.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void electrolyzerModelII () throws IloException {
		String nameOfModel = "model2";
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = constHydrDemand;//2*1100*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT,ONLYONE, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE,POWER)}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, 0,POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0,POWER),
					}
					);

			IloIntVar[][][] restrictiveDepElectrolyzersStorage =  designpatterns.DesignPatterns.generateRestrictiveDependency(
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, ONLYONE, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE,POWER),
					}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, ONLYONE,POWER)
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE,POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, ONLYONE,POWER)}
					);

			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer1");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("Electrolyzer1");
			designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration("Electrolyzer1");
			designpatterns.DesignPatterns.generateRampLimits("Electrolyzer1", INPUT);


			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer2");
			designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits("Electrolyzer2");
			designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration("Electrolyzer2");
			designpatterns.DesignPatterns.generateRampLimits("Electrolyzer2", INPUT);

			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("Storage");

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(
						designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE,POWER)[i]
								);
				objective.addTerm(startupCost, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1", POWER, ONLYONE,STATE)[i][1]);
				objective.addTerm(startupCost, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2", POWER, ONLYONE, STATE)[i][1]);
			}
			designpatterns.DesignPatterns.getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			designpatterns.DesignPatterns.getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, designpatterns.DesignPatterns.getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (designpatterns.DesignPatterns.getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+designpatterns.DesignPatterns.getCplex().getObjValue());
				System.out.println(designpatterns.DesignPatterns.getCplex().getCplexStatus());

				int sizeOfResultsMatrix = designpatterns.DesignPatterns.getDecisionVariablesMatrix().size()*40+designpatterns.DesignPatterns.getDecisionVariablesVector().size(); 

				double [][] optimizationResults = new double [designpatterns.DesignPatterns.getArrayLength()+1][sizeOfResultsMatrix];

				for (int i = 1; i < designpatterns.DesignPatterns.getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, ONLYONE, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, 0, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, 0,POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, ONLYONE,POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, ONLYONE,POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", SOC, ONLYONE,POWER)[i]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,ONLYONE,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,ONLYONE,STATE)[i][j]);
						counter++; 
					}

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[i][j]);
						counter++; 
					}

//					for (int j = 0; j < restrictiveDepElectrolyzersStorage.length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(restrictiveDepElectrolyzersStorage[0][i-1][0]);
						counter++; 
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(restrictiveDepElectrolyzersStorage[0][i-1][1]);
						counter++; 
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(restrictiveDepElectrolyzersStorage[1][i-1][0]);
						counter++; 
//					}


				}

				String headerOptimizationResults = null; 
				headerOptimizationResults ="System-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +";"+"Storage-SOC";

				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-State-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,0,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-BINARY-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,ONLYONE,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-STATE-"+Integer.toString(j);
				}
				headerOptimizationResults = headerOptimizationResults + ";binaryOutput1;binaryOutput2;binaryInput";
				String filePath = "C:/Users/Wagner/OneDrive - Helmut-Schmidt-Universität/Papers/Oncon2023/results/";
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "dp-"+nameOfModel, headerOptimizationResults, filePath);
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
		finally {
			if (designpatterns.DesignPatterns.getCplex()!=null)  {
				designpatterns.DesignPatterns.getCplex().close();
				designpatterns.DesignPatterns.globalCplex=null;
			}
		}
	}


}
