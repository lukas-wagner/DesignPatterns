package designpatterns;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class ElectrolyzerOptimization {
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

	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
		electrolyzerBaseModel();
		electrolyzerModelI();
	}

	/**
	 * Sets the optimization parameters, primarily in ArrayList<ResourceParameters> resourceParameters.
	 */
	public static void setOptimizationParameters () {

		designpatterns.DesignPatterns.setOptimalityGap(0.001); // default 10e-4
		designpatterns.DesignPatterns.setTimeInterval(0.25); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		designpatterns.DesignPatterns.setArrayLength(96); // set arrayLength in # of time steps

		double maxPowerEl = 52.25; // MW

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		resource1.setEnergyCarrier(POWER);
		resource1.setMinPowerInput(0);
		resource1.setMinPowerOutput(0);
		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(1000);

		resource1.createPlaList(0,0,0,7.84); // kg/h
		resource1.createPlaList(21.997,-26.36,7.84,10.6091); // kg/h
		resource1.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource1.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource1.createPlaList(18.787,16.289,16.59741,20.78419);
		resource1.createPlaList(17.864,35.465,20.78419,25.39345);
		resource1.createPlaList(17.132,54.051,25.39345,29.70148);
		resource1.createPlaList(16.609,69.603,29.70148,33.85188);
		resource1.createPlaList(16.058,88.24,33.85188,39.48562);
		resource1.createPlaList(15.622,105.45,39.48562,42.91256);
		resource1.createPlaList(15.308,118.93,42.91256,46.914);
		resource1.createPlaList(14.946,135.92,46.914,maxPowerEl);
		resource1.setNumberOfLinearSegments(resource1.getPla().size());

		resource1.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource1.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7.84, 0);
		resource1.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource1.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 0.52,0.52, 0);
		resource1.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		// add object to Array List
		designpatterns.DesignPatterns.getResourceParameters().add(resource1);

		ResourceParameters resource2 = new ResourceParameters();
		resource2.setName("Electrolyzer2");
		resource2.setEnergyCarrier(POWER);
		resource2.setMinPowerInput(0);
		resource2.setMinPowerOutput(0);
		resource2.setMaxPowerInput(maxPowerEl);
		resource2.setMaxPowerOutput(1000);

		resource2.createPlaList(0,0,0,7.84); // kg/h
		resource2.createPlaList(21.997,-26.36,7.84,10.6091); // kg/h
		resource2.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource2.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource2.createPlaList(18.787,16.289,16.59741,20.78419);
		resource2.createPlaList(17.864,35.465,20.78419,25.39345);
		resource2.createPlaList(17.132,54.051,25.39345,29.70148);
		resource2.createPlaList(16.609,69.603,29.70148,33.85188);
		resource2.createPlaList(16.058,88.24,33.85188,39.48562);
		resource2.createPlaList(15.622,105.45,39.48562,42.91256);
		resource2.createPlaList(15.308,118.93,42.91256,46.914);
		resource2.createPlaList(14.946,135.92,46.914,50);
		resource2.setNumberOfLinearSegments(resource2.getPla().size());

		resource2.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource2.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7.84, 0);
		resource2.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource2.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 0.52, 0.52, 0);
		resource2.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource2.setNumberOfSystemStates(resource2.getSystemStates().size());
		designpatterns.DesignPatterns.getResourceParameters().add(resource2);

		ResourceParameters resource3 = new  ResourceParameters();
		resource3.setName("Storage");
		resource3.setEnergyCarrier(POWER);
		resource3.setMinPowerInput(0);
		resource3.setMaxPowerInput(2000);
		resource3.setMaxPowerOutput(912.13);
		resource3.setResourceAsStorage(true);
		resource3.setEfficiencyInputStorage(1);
		resource3.setEfficiencyOutputStorage(1);
		resource3.setInitalCapacity(0);
		resource3.setMaximumStorageCapacity(5000);
		designpatterns.DesignPatterns.getResourceParameters().add(resource3);
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
			double constantHydrogenDemand = 2*1000*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER),
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER),
					}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, POWER)
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)}
					);

			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer1");

			designpatterns.DesignPatterns.generateInputOutputRelationship("Electrolyzer2");

			designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem("Storage");

			designpatterns.DesignPatterns.getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = designpatterns.DesignPatterns.getCplex().linearNumExpr();
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				objective.addTerm(designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i]);
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
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", SOC, POWER)[i-1]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY)[j][i-1]);
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
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-Binary-"+Integer.toString(j);
				}
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "designpatterns_from-method"+nameOfModel, headerOptimizationResults);
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
		String nameOfModel = "modelI";
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 2*900*designpatterns.DesignPatterns.getTimeInterval();

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			designpatterns.DesignPatterns.creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			for (int i = 0; i < designpatterns.DesignPatterns.getArrayLength(); i++) {
				designpatterns.DesignPatterns.getCplex().addEq(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER),
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER), 
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER),
					}, 
					new IloNumVar[][] {
						designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, POWER)
					}
					);

			designpatterns.DesignPatterns.generateCorrelativeDependency(
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, POWER)}, 
					new IloNumVar[][] {designpatterns.DesignPatterns.getDecisionVariableFromVector("System", OUTPUT, POWER)}
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
				objective.addTerm(designpatterns.DesignPatterns.getTimeInterval()*designpatterns.DesignPatterns.getElectricityPrice()[i], designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i]);
				objective.addTerm(2000, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1", POWER, STATE)[i][1]);
				objective.addTerm(2000, designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2", POWER, STATE)[i][1]);
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
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("System", INPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER)[i-1]);
					counter++; 

					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromVector("Storage", SOC, POWER)[i-1]);
					counter++; 

					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY)[j][i-1]);
						counter++; 
					}


					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,STATE)[i-1][j]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = designpatterns.DesignPatterns.getCplex().getValue(designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[i-1][j]);
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

				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer1-POWER-State-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-BINARY-"+Integer.toString(j);
				}
				for (int j = 0; j < designpatterns.DesignPatterns.getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
					headerOptimizationResults = headerOptimizationResults +";"+"Electrolyzer2-POWER-STATE-"+Integer.toString(j);
				}
				designpatterns.DesignPatterns.writeResultsToFile(optimizationResults, "designpatterns_from-method"+nameOfModel, headerOptimizationResults);
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
