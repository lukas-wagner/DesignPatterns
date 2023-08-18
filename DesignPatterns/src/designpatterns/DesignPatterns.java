package designpatterns;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ilog.concert.*;
import ilog.cplex.*;

/**
 * The Class DesignPatterns.
 *
 * @author Lukas Wagner, lukas.wagner@hsu-hh.de
 * Code for Design Patterns for Energy Optimization.
 * Conference paper "Design Patterns for Optimization Models of Flexible Energy Resources", ONCON 2023
 */
public class DesignPatterns {

	/** The global cplex. */
	private static	IloCplex globalCplex = null; 

	/** The solving times. */
	static double[] solvingTimes = new double [4]; 

	/** The resource parameters. */
	static List<ResourceParameters> resourceParameters = new ArrayList<>();

	/** The array length. */
	static int arrayLength; 

	/** The time interval = length of time step. */
	static double timeInterval; 

	/** The optimality gap. */
	static double optimalityGap = 0.001; // default 10e-4

	/** The nolimit. */
	private static final double NOLIMIT = 9999;

	/** The Constant INPUT. */
	private static final String INPUT = "Input";

	/** The Constant OUTPUT. */
	private static final String OUTPUT = "Output";

	/** The Constant SOC. */
	private static final String SOC = "SOC";

	/** The Constant POWER. */
	private static final String POWER = "Power";

	/** The Constant BINARY. */
	private static final String BINARY = "Binary";

	/** The Constant SEGMENT. */
	private static final String SEGMENT = "Segment";

	/** The Constant STATE. */
	private static final String STATE = "State";

	/** The hashmap for decision variables vector. */
	static HashMap<String, IloNumVar[]> decisionVariablesVector = new HashMap<>(); 

	/** The hashmap decision variables matrix. */
	static HashMap<String, IloNumVar[][]> decisionVariablesMatrix = new HashMap<>(); 
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IloException the ilo exception
	 */
	public static void main(String[] args) throws IloException {
		setOptimizationParameters();
		//		optimizationModel(timeInterval, arrayLength);
		optimizationModelWithMethods();
	}

	/**
	 * Sets the optimization parameters, primarily in ResourceParameters resourceParameters.
	 */
	public static void setOptimizationParameters () {
		setOptimalityGap(0.01); // default 10e-4
		setTimeInterval(0.25); // 0.05 = 3Minutes, 0.125 = 7.5 Minutes
		setArrayLength(20); // set arrayLength in # of time steps

		// parameters
		// new object for resource to set parameters
		ResourceParameters resource1 = new  ResourceParameters();
		resource1.setName("Electrolyzer1");
		resource1.setMinPowerInput(0);
		resource1.setMinPowerOutput(0);
		double maxPowerEl = 50; // MW
		resource1.setMaxPowerInput(maxPowerEl);
		resource1.setMaxPowerOutput(1000);

		resource1.createPlaList(0,0,0,7); // kg/h
		resource1.createPlaList(21.997,-26.36,7,10.6091); // kg/h
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

		//		resource1.setNumberOfLinearSegments(0);
		resource1.setEfficiency(18.8);
		resource1.setSlope(17.5);
		resource1.setIntercept(29);

		resource1.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource1.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7, 0);
		resource1.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource1.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 7,7, 0);
		resource1.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource1.setNumberOfSystemStates(resource1.getSystemStates().size());
		// add object to Array List
		resourceParameters.add(resource1);		

		ResourceParameters resource2 = new ResourceParameters();
		resource2.setName("Electrolyzer2");
		resource2.setMinPowerInput(0);
		resource2.setMinPowerOutput(0);
		resource2.setMaxPowerInput(maxPowerEl);
		resource2.setMaxPowerOutput(1000);

		resource2.createPlaList(0,0,0,7); // kg/h
		resource2.createPlaList(21.997,-26.36,7,10.6091); // kg/h
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
		resource2.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7, 0);
		resource2.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource2.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 7,7, 0);
		resource2.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource2.setNumberOfSystemStates(resource2.getSystemStates().size());
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

		/**
		ResourceParameters resource4 = new ResourceParameters();
		resource4.setName("Electrolyzer3");
		resource4.setMinPowerInput(0);
		resource4.setMinPowerOutput(0);
		resource4.setMaxPowerInput(maxPowerEl);
		resource4.setMaxPowerOutput(1000);

		resource4.createPlaList(0,0,0,7); // kg/h
		resource4.createPlaList(21.997,-26.36,7,10.6091); // kg/h
		resource4.createPlaList(20.754,-13.173,10.6091,13.31882);
		resource4.createPlaList(19.782,-0.23309,13.31882,16.59741);
		resource4.createPlaList(18.787,16.289,16.59741,20.78419);
		resource4.createPlaList(17.864,35.465,20.78419,25.39345);
		resource4.createPlaList(17.132,54.051,25.39345,29.70148);
		resource4.createPlaList(16.609,69.603,29.70148,33.85188);
		resource4.createPlaList(16.058,88.24,33.85188,39.48562);
		resource4.createPlaList(15.622,105.45,39.48562,42.91256);
		resource4.createPlaList(15.308,118.93,42.91256,46.914);
		resource4.createPlaList(14.946,135.92,46.914,50);

		resource4.setNumberOfLinearSegments(resource4.getPla().size());

		resource4.addSystemStateWithMaxPowerOutput(0, "off", 4, NOLIMIT, new int[] {1}, 0, 0, 0);
		resource4.addSystemStateWithMaxPowerOutput(1, "start-up", 2, 2, new int[] {2}, 0, 7, 0);
		resource4.addSystemState(2, "operation", 4, NOLIMIT, new int[] {3, 4}, 7, maxPowerEl);
		resource4.addSystemStateWithMaxPowerOutput(3, "stand-by", 0, 10, new int[] {2,4}, 7,7, 0);
		resource4.addSystemStateWithMaxPowerOutput(4, "shut down", 2, 2, new int[] {0}, 0, 7, 0);
		resource4.setNumberOfSystemStates(resource4.getSystemStates().size());
		resourceParameters.add(resource4);
		 */
		System.out.println("size resourceParameters "+ resourceParameters.size());
	}



	/**
	 * Creation of decision variables.
	 * saves created decision variables to 2 hashmaps, depending on #2 of colums (1 -> vector, 2-> matrix)
	 *
	 * @param maxPowerSystem the max power system
	 * @throws IloException the ilo exception
	 */
	public static void creationOfDecisionVariables (double maxPowerSystem) throws IloException {
		IloNumVar[] powerInput = getCplex().numVarArray(getArrayLength(),  0 ,  maxPowerSystem);
		getDecisionVariablesVector().put("System"+"-"+INPUT+"-"+POWER, powerInput);


		// TODO get List of names (converters) from resourceParameters
		String[] converters = new String [] {"Electrolyzer1", "Electrolyzer2"};//, "Electrolyzer3"};
		String[] storageSystems = new String [] {"Storage"};

		for (String nameOfResource : converters) {
			int indexOfResource = -1;
			indexOfResource = findIndexByName(nameOfResource);
			if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

			IloNumVar[] powerInputResource = getCplex().numVarArray(
					getArrayLength(), 
					resourceParameters.get(indexOfResource).getMinPowerInput(), 
					resourceParameters.get(indexOfResource).getMaxPowerInput()
					);
			getDecisionVariablesVector().put(nameOfResource+"-"+INPUT+"-"+POWER, powerInputResource);

			IloNumVar[] powerOutputResource = getCplex().numVarArray(
					getArrayLength(), 
					resourceParameters.get(indexOfResource).getMinPowerOutput(), 
					resourceParameters.get(indexOfResource).getMaxPowerOutput()
					);
			getDecisionVariablesVector().put(nameOfResource+"-"+OUTPUT+"-"+POWER, powerOutputResource);

			if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()!=0) {
				// only create decision variable, if necessary
				IloIntVar[][] binariesPlaResource = new IloIntVar[resourceParameters.get(indexOfResource).getNumberOfLinearSegments()][getArrayLength()]; 
				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {
					binariesPlaResource[plaSegment] = getCplex().intVarArray(getArrayLength(), 0 , 1);
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+BINARY, binariesPlaResource);

				IloNumVar[][] powerInputLinearSegmentsResource = new IloNumVar[resourceParameters.get(indexOfResource).getNumberOfLinearSegments()][getArrayLength()]; 
				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {
					powerInputLinearSegmentsResource[plaSegment] = getCplex().numVarArray(
							getArrayLength(),
							resourceParameters.get(indexOfResource).getMinPowerInput(), 
							resourceParameters.get(indexOfResource).getMaxPowerInput()
							);
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+SEGMENT, powerInputLinearSegmentsResource);

			}

			if (resourceParameters.get(indexOfResource).getNumberOfSystemStates()!=0) {
				// only create decision variable, if necessary
				IloIntVar[][] statesIntArrayResource = new IloIntVar[getArrayLength()][resourceParameters.get(indexOfResource).getNumberOfSystemStates()];
				for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
					for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
						statesIntArrayResource[timeStep][state] = getCplex().intVar(0, 1);
					}
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+STATE, statesIntArrayResource);
			}

		}

		// ---------------------RESOURCE  - Storage---------------------
		for (String nameOfStorage : storageSystems) {
			int indexOfStorage = -1;
			indexOfStorage = findIndexByName(nameOfStorage);
			if (indexOfStorage==-1) System.err.println("Storage System not found in list of resourceParameters!");

			// check if resource is actually storage!
			if (resourceParameters.get(indexOfStorage).isStorage()==true) {
				IloNumVar[] powerInputStorage = getCplex().numVarArray(
						getArrayLength(), 
						resourceParameters.get(indexOfStorage).getMinPowerOutput(), 
						resourceParameters.get(indexOfStorage).getMaxPowerOutput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+INPUT+"-"+POWER, powerInputStorage);

				IloNumVar[] stateOfCharge = getCplex().numVarArray(
						getArrayLength()+1, 
						resourceParameters.get(indexOfStorage).getMinimumStorageCapacity(), 
						resourceParameters.get(indexOfStorage).getMaximumStorageCapacity()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+SOC+"-"+POWER, stateOfCharge);

				IloNumVar[] powerOutputStorage = getCplex().numVarArray(
						getArrayLength(), 
						resourceParameters.get(indexOfStorage).getMinPowerOutput(), 
						resourceParameters.get(indexOfStorage).getMaxPowerOutput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+OUTPUT+"-"+POWER, powerOutputStorage);
			}
		}

		IloNumVar[] combinedHydrogenOutput = getCplex().numVarArray(getArrayLength(),  0, Double.MAX_VALUE);
	}

	/**
	 * Optimization model with methods.
	 *
	 * @throws IloException the ilo exception
	 */
	public static void optimizationModelWithMethods() throws IloException {
		try {
			//additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 2*900*getTimeInterval();

			//-------------------------------------------------------------------- Decision Variables --------------------------------------------------------------------
			creationOfDecisionVariables(maxPowerSystem);

			// ------------------------------------------------------------------------ CONSTRAINTS--------------------------------------------------------------------
			IloNumVar[] powerOutputSystem = getCplex().numVarArray(getArrayLength(), 0, Double.MAX_VALUE);
			for (int i = 0; i < getArrayLength(); i++) {
				getCplex().addEq(powerOutputSystem[i], constantHydrogenDemand);
			}

			// ------------------------------------------------------------------------ Use of Design Patterns--------------------------------------------------------------------
			generateCorrelativeDependency(
					new IloNumVar[][] {getDecisionVariableFromVector("System", INPUT, POWER)}, 
					new IloNumVar[][] {
						getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER), 
						getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER),
						//getDecisionVariableFromVector("Electrolyzer3", INPUT, POWER)
					}
					);

			generateCorrelativeDependency(
					new IloNumVar[][] {
						getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER), 
						getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER),
						//getDecisionVariableFromVector("Electrolyzer3", OUTPUT, POWER)
					}, 
					new IloNumVar[][] {
						getDecisionVariableFromVector("Storage", INPUT, POWER)
					}
					);

			generateCorrelativeDependency(
					new IloNumVar[][] {getDecisionVariableFromVector("Storage", OUTPUT, POWER)}, 
					new IloNumVar[][] {powerOutputSystem}
					);

			generateCorrelativeDependency(new IloNumVar[][] {}, new IloNumVar[][] {});
			generateRestrictiveDependency(new IloNumVar[][] {}, new IloNumVar[][] {});

			generateInputOutputRelationship("Electrolyzer1");
			generateSystemStateSelectionByPowerLimits("Electrolyzer1");
			generateStateSequencesAndHoldingDuration("Electrolyzer1");
			generateRampLimits("Electrolyzer1", INPUT);

			generateInputOutputRelationship("Electrolyzer2");
			generateSystemStateSelectionByPowerLimits("Electrolyzer2");
			generateStateSequencesAndHoldingDuration("Electrolyzer2");
			generateRampLimits("Electrolyzer2", INPUT);

			//			generateInputOutputRelationship("Electrolyzer3");
			//			generateSystemStateSelectionByPowerLimits("Electrolyzer3");
			//			generateStateSequencesAndHoldingDuration("Electrolyzer3");
			//			generateRampLimits("Electrolyzer3", INPUT);

			generateEnergyBalanceForStorageSystem("Storage");

			//	System.out.println(cplex);
			getCplex().exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = getCplex().linearNumExpr();
			for (int i = 0; i < getArrayLength(); i++) {
				objective.addTerm(timeInterval*getElectricityPrice()[i], getDecisionVariableFromVector("System", INPUT, POWER)[i]);
			}
			getCplex().addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			getCplex().setParam(IloCplex.Param.MIP.Tolerances.MIPGap, getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
			if (getCplex().solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+getCplex().getObjValue());
				System.out.println(getCplex().getCplexStatus());

				int sizeOfResultsMatrix = getDecisionVariablesMatrix().size()*40+getDecisionVariablesVector().size(); 

				double [][] optimizationResults = new double [getArrayLength()+1][sizeOfResultsMatrix];

				for (int i = 1; i < getArrayLength()+1; i++) {
					int counter = 0; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("System", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer1", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer1", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer2", INPUT, POWER)[i-1]);
					counter++;
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Electrolyzer2", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", INPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", OUTPUT, POWER)[i-1]);
					counter++; 
					optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromVector("Storage", SOC, POWER)[i-1]);
					counter++; 
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer1",POWER,STATE)[i-1][j]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY)[j][i-1]);
						counter++; 
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						optimizationResults[i][counter] = getCplex().getValue(getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[i-1][j]);
						counter++; 
					}
				}
				
				String headerOptimizationResults = null; 
				headerOptimizationResults ="System-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer1-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer1-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer2-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer2-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Storage-INPUT-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Storage-Output-POWER";
				headerOptimizationResults = headerOptimizationResults +","+"Storage-SOC";
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,SEGMENT).length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer1-POWER-SEGMENT-"+Integer.toString(j);
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer1",POWER,BINARY).length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer1-POWER-Binary-"+Integer.toString(j);
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer1-POWER-State-"+Integer.toString(j);
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,SEGMENT).length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer2-POWER-SEGMENT-"+Integer.toString(j);
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,BINARY).length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer2-POWER-BINARY-"+Integer.toString(j);
					}
					for (int j = 0; j < getDecisionVariableFromMatrix("Electrolyzer2",POWER,STATE)[0].length; j++) {
						headerOptimizationResults = headerOptimizationResults +","+"Electrolyzer2-POWER-STATE-"+Integer.toString(j);
					}
			
				writeResultsToFile(optimizationResults, "designpatterns_from-method", headerOptimizationResults);
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
	}


	/**
	 * Find index by name in list resourceParameters.
	 *
	 * @param nameToFind the name to find
	 * @return the index
	 */
	public static int findIndexByName(String nameToFind) {
		for (int i = 0; i < resourceParameters.size(); i++) {
			ResourceParameters resource = resourceParameters.get(i);
			if (resource.getName().equals(nameToFind)) {
				return i;
			}
		}
		return -1; // Return -1 if name not found
	}

	public static IloNumVar[] getDecisionVariableFromVector(String name, String port, String medium) {
		return getDecisionVariablesVector().get(name+"-"+port+"-"+medium);
	}

	public static IloNumVar[][] getDecisionVariableFromMatrix(String name, String medium, String type) {
		return getDecisionVariablesMatrix().get(name+"-"+medium+"-"+type);
	}
	/**
	 * Generate input output relationship.
	 *
	 * @param nameOfResource the name of resource
	 * @param powerInputResource decision variable the power input resource
	 * @param powerOutputResource decision variable  the power output resource
	 * @param binariesPlaResource decision variable  the binaries pla resource
	 * @param powerInputResourceLinearSegments decision variable  the power input resource linear segments
	 * @throws IloException the ilo exception
	 */
	private static void generateInputOutputRelationship (String nameOfResource) throws IloException {//, IloNumVar[] powerInputResource, IloNumVar[] powerOutputResource, IloIntVar[][] binariesPlaResource, IloNumVar[][] powerInputResourceLinearSegments) throws IloException {
		// Pattern input output
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");


		// get decision variables from vector/matrix
		IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, POWER);
		IloNumVar[] powerOutputResource = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);
		IloIntVar[][] binariesPlaResource  = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, BINARY);
		IloNumVar[][] powerInputResourceLinearSegments = getDecisionVariableFromMatrix(nameOfResource, POWER, SEGMENT);

		if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()!=0
				) {
			// Case 1: efficiency
			System.out.println("Case 1: efficiency");
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				getCplex().addEq(powerOutputResource[timestep], 
						getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getEfficiency())
						);
			} 
		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()==0
				&& resourceParameters.get(indexOfResource).getSlope()!=0) {
			// y = ax+b
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				getCplex().addEq(powerOutputResource[timestep], 
						getCplex().sum(
								resourceParameters.get(indexOfResource).getIntercept(),
								getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getSlope())
								)
						);
			}
		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()>0) {
			// Case 3: pla
			System.out.println("Case 3: pla");
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				// sum binaries[i] = 1 part 1
				IloNumExpr binarySum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();

				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {

					// lowerBound * power <= binary
					getCplex().addLe(
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getLowerBound(), 
									binariesPlaResource[plaSegment][timestep]
									), 
							powerInputResourceLinearSegments[plaSegment][timestep]
							);

					//power <= upperBound * binary
					getCplex().addLe(
							powerInputResourceLinearSegments[plaSegment][timestep], 
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getUpperBound(), 
									binariesPlaResource[plaSegment][timestep]
									)
							);

					// sum binaries[i] = 1 part 2
					binarySum = getCplex().sum(binarySum,binariesPlaResource[plaSegment][timestep]);
					powerInputSum = getCplex().sum(powerInputSum, powerInputResourceLinearSegments[plaSegment][timestep]);
				}

				// sum binaries[i] = 1 part 3           
				getCplex().addEq(binarySum, 1);

				// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 3
				getCplex().addEq(powerInputSum, powerInputResource[timestep]);

				for (int plasegment = 0; plasegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plasegment++) {
					getCplex().add(
							getCplex().ifThen(
									getCplex().and(
											getCplex().le(binariesPlaResource[plasegment][timestep], 1),
											getCplex().ge(binariesPlaResource[plasegment][timestep], 0.9)
											),
									getCplex().eq(powerOutputResource[timestep],
											getCplex().sum(
													resourceParameters.get(indexOfResource).getPla().get(plasegment).getIntercept(),
													getCplex().prod(
															powerInputResourceLinearSegments[plasegment][timestep],
															resourceParameters.get(indexOfResource).getPla().get(plasegment).getSlope()
															)
													)
											)
									)
							);
				}
			}
		}
		System.out.println("Input-output successfully created for resource " + nameOfResource);
	}

	/**
	 * Generate input output relationship with latency.
	 *
	 * @param nameOfResource the name of resource
	 * @param powerInputResource the power input resource
	 * @param powerOutputResource the power output resource
	 * @param binariesPlaResource the binaries pla resource
	 * @param powerInputResourceLinearSegments the power input resource linear segments
	 * @param latency the latency
	 * @throws IloException the ilo exception
	 */
	private static void generateInputOutputRelationshipWithLatency (String nameOfResource, IloNumVar[] powerInputResource, IloNumVar[] powerOutputResource, IloIntVar[][] binariesPlaResource, IloNumVar[][] powerInputResourceLinearSegments, int latency) throws IloException {
		// Pattern input output
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

		IloNumVar[] helperVariable = getCplex().numVarArray(getArrayLength(), resourceParameters.get(indexOfResource).getMinPowerOutput(), resourceParameters.get(indexOfResource).getMaxPowerOutput());
		if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()!=0
				) {
			// Case 1: efficiency
			System.out.println("Case 1: efficiency with latency");

			if (latency>getArrayLength()) {
				System.err.println("Latency > ArrayLength: "+ latency+ ">" + getArrayLength());
			}
			else {
				for (int timestep = 0; timestep < getArrayLength(); timestep++) {
					getCplex().addEq(helperVariable[timestep], 
							getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getEfficiency())
							);
				} 
				for (int timestep = latency; timestep < getArrayLength()-latency; timestep++) {
					// TODO was ist mit dem Ende, wenn PIn = 0 und Pout >0?
					getCplex().addEq(powerOutputResource[timestep+latency], helperVariable[timestep]);
				}				
			}

		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()==0
				&& resourceParameters.get(indexOfResource).getEfficiency()==0
				&& resourceParameters.get(indexOfResource).getSlope()!=0) {
			// TODO include latency in y = ax+b 
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				getCplex().addEq(powerOutputResource[timestep], 
						getCplex().sum(
								resourceParameters.get(indexOfResource).getIntercept(),
								getCplex().prod(powerInputResource[timestep], resourceParameters.get(indexOfResource).getSlope())
								)
						);
			}
		} else if (resourceParameters.get(indexOfResource).getNumberOfLinearSegments()>0) {
			// Case 3: pla
			// TODO include latency in pla 
			System.out.println("Case 3: pla");
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				// sum binaries[i] = 1 part 1
				IloNumExpr binarySum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();

				for (int plaSegment = 0; plaSegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plaSegment++) {

					// lowerBound * power <= binary
					getCplex().addLe(
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getLowerBound(), 
									binariesPlaResource[plaSegment][timestep]
									), 
							powerInputResourceLinearSegments[plaSegment][timestep]
							);

					//power <= upperBound * binary
					getCplex().addLe(
							powerInputResourceLinearSegments[plaSegment][timestep], 
							getCplex().prod(
									resourceParameters.get(indexOfResource).getPla().get(plaSegment).getUpperBound(), 
									binariesPlaResource[plaSegment][timestep]
									)
							);

					// sum binaries[i] = 1 part 2
					binarySum = getCplex().sum(binarySum,binariesPlaResource[plaSegment][timestep]);
					powerInputSum = getCplex().sum(powerInputSum, powerInputResourceLinearSegments[plaSegment][timestep]);
				}

				// sum binaries[i] = 1 part 3           
				getCplex().addEq(binarySum, 1);

				// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 3
				getCplex().addEq(powerInputSum, powerInputResource[timestep]);

				for (int plasegment = 0; plasegment < resourceParameters.get(indexOfResource).getNumberOfLinearSegments(); plasegment++) {
					getCplex().add(
							getCplex().ifThen(
									getCplex().and(
											getCplex().le(binariesPlaResource[plasegment][timestep], 1),
											getCplex().ge(binariesPlaResource[plasegment][timestep], 0.9)
											),
									getCplex().eq(powerOutputResource[timestep],
											getCplex().sum(
													resourceParameters.get(indexOfResource).getPla().get(plasegment).getIntercept(),
													getCplex().prod(
															powerInputResourceLinearSegments[plasegment][timestep],
															resourceParameters.get(indexOfResource).getPla().get(plasegment).getSlope()
															)
													)
											)
									)
							);
				}
			}
		}
		System.out.println("Input-output successfully created for resource " + nameOfResource);
	}

	/**
	 * Generate system state selection by power limits.
	 *
	 * @param nameOfResource the name of resource
	 * @param powerInputResource the power input resource
	 * @param powerOutputResource the power output resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	private static void generateSystemStateSelectionByPowerLimits (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

		IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, POWER);
		IloNumVar[] powerOutputResource  = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

		for (int timestep = 0; timestep < getArrayLength(); timestep++) {
			getCplex().addEq(getCplex().sum(statesIntArrayResource[timestep]), 1);

			IloNumExpr powerMinSum = getCplex().numExpr();
			IloNumExpr powerMaxSum = getCplex().numExpr();
			IloNumExpr powerOutputMaxSum = getCplex().numExpr();
			for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
				powerMinSum = getCplex().sum(powerMinSum, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinPower()));
				powerMaxSum = getCplex().sum(powerMaxSum, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPower()));
				powerOutputMaxSum = getCplex().sum(powerOutputMaxSum, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxPowerOutput()));
			}
			getCplex().addGe(powerInputResource[timestep], powerMinSum);
			getCplex().addLe(powerInputResource[timestep], powerMaxSum);
			getCplex().addLe(powerOutputResource[timestep], powerOutputMaxSum);
		}
		System.out.println("System states created for "+ nameOfResource);
	}


	/**
	 * Generate ramp limits.
	 *
	 * @param nameOfResource the name of resource
	 * @param port the side (INPUT XOR OUTPUT)
	 * @param relevantPowerFlowResource the power input resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	private static void generateRampLimits (String nameOfResource, String port) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");


		IloNumVar[] relevantPowerFlowResource = getDecisionVariableFromVector(nameOfResource, port, POWER); 
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

		if (port == INPUT) {
			// --------------------- ramp limits ----------------------------------
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr sumMinRamp = getCplex().numExpr();
				IloNumExpr sumMaxRamp = getCplex().numExpr();
				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();

				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					sumMinRamp = getCplex().sum(sumMinRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinRampInput()));
					sumMaxRamp = getCplex().sum(sumMaxRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxRampInput()));
				}

				getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
				getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
			}
		} else if (port == OUTPUT) {
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr sumMinRamp = getCplex().numExpr();
				IloNumExpr sumMaxRamp = getCplex().numExpr();
				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();

				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
					sumMinRamp = getCplex().sum(sumMinRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinRampOutput()));
					sumMaxRamp = getCplex().sum(sumMaxRamp, getCplex().prod(statesIntArrayResource[timestep][state], resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxRampOutput()));
				}

				getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
				getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
			}
		}
		System.out.println("Ramp limits created for "+ nameOfResource);
	}

	/**
	 * Generate state sequences and holding duration.
	 *
	 * @param nameOfResource the name of resource
	 * @param statesIntArrayResource the states int array resource
	 * @throws IloException the ilo exception
	 */
	private static void generateStateSequencesAndHoldingDuration (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, STATE);

		// --------------------- State sequences and holding duration ----------------------------------

		// initial state	
		//Constraints nur für Zeitpunkt 0: Zustand 0 = 1, alle anderen 0, start in state = off 
		//
		//			cplex.addEq(statesIntArrayElectrolyzer1[0][0], 1);
		//			cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[0]), 1);

		for (int timestep = 1; timestep < getArrayLength(); timestep++) {

			// only one active state per time step
			//				cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[i]), 1);

			for (int state = 0; state < resourceParameters.get(indexOfResource).getNumberOfSystemStates(); state++) {
				// min duration
				if (resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration() == NOLIMIT) {

				} else {
					IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep][state],statesIntArrayResource[timestep-1][state]);
					IloNumExpr constraintRightSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration(); timeInStateCounter++) {

						try {
							constraintRightSide = getCplex().sum(constraintRightSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 

						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(getCplex().prod(constraintLeftSide, Math.min(counter, resourceParameters.get(indexOfResource).getSystemStates().get(state).getMinStateDuration())), constraintRightSide);
				}
				//max Duration
				if (resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration() == NOLIMIT) {

				}else{
					IloNumExpr constraintLeftSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()+1; timeInStateCounter++) {

						try {
							constraintLeftSide = getCplex().sum(constraintLeftSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 

						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(constraintLeftSide, Math.min(counter,resourceParameters.get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()));
				}
				//State sequences
				IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep-1][state],statesIntArrayResource[timestep][state]);
				IloNumExpr constraintRightSide = statesIntArrayResource[timestep][resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates()[0]];

				for (int followerStatesCounter = 1; followerStatesCounter < resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates().length; followerStatesCounter++) {
					constraintRightSide = getCplex().sum(constraintRightSide, statesIntArrayResource[timestep][resourceParameters.get(indexOfResource).getSystemStates().get(state).getFollowerStates()[followerStatesCounter]]);
				}
				getCplex().addLe(constraintLeftSide, constraintRightSide);
			}
		}
		System.out.println("State sequences and holding duration created for " + nameOfResource);
	}



	/**
	 * Generate energy balance for storage system.
	 *
	 * @param nameOfResource the name of resource
	 * @param stateOfCharge the state of charge
	 * @param powerInputStorage the power input storage
	 * @param powerOutputStorage the power output storage
	 * @throws IloException the ilo exception
	 */
	private static void generateEnergyBalanceForStorageSystem (String nameOfResource) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");
		if (resourceParameters.get(indexOfResource).isStorage()==false) System.err.println("System "+ nameOfResource + " is not a storage system!");

		IloNumVar[] stateOfCharge = getDecisionVariableFromVector(nameOfResource, SOC, POWER);
		IloNumVar[] powerInputStorage = getDecisionVariableFromVector(nameOfResource, INPUT, POWER); 
		IloNumVar[] powerOutputStorage = getDecisionVariableFromVector(nameOfResource, OUTPUT, POWER);

		// --------------------- storage and loss ----------------------------------
		for (int timestep = 0; timestep < getArrayLength()+1; timestep++) {
			if(timestep==0) {
				getCplex().addEq(stateOfCharge[timestep], resourceParameters.get(indexOfResource).getInitalCapacity());
			} else {
				getCplex().addEq(stateOfCharge[timestep], 
						getCplex().sum(
								stateOfCharge[timestep-1],
								getCplex().diff(
										getCplex().prod(powerInputStorage[timestep-1], resourceParameters.get(indexOfResource).getEfficiencyInputStorage()*getTimeInterval()),
										getCplex().sum(
												getCplex().prod(powerOutputStorage[timestep-1], resourceParameters.get(indexOfResource).getEfficiencyOutputReciprocal()*getTimeInterval()),
												resourceParameters.get(indexOfResource).getStaticPowerLoss()*timeInterval
												)
										)
								)
						);
			}
		}
		System.out.println("Energy balance created for "+ nameOfResource);
	}


	/**
	 * Generate correlative dependency.
	 * sum powerOutput[i] = sum PowerInput[i]
	 *
	 * @param powerOutputs array of all relevant power output arrays (new IloNumVar [][] {})
	 * @param powerInputs array of all relevant power input arrays (new IloNumVar [][] {})
	 * @throws IloException the ilo exception
	 */
	private static void generateCorrelativeDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// sum powerOutput[i] = sum PowerInput[i]
		if (powerOutputs.length >0 && powerInputs.length >0) {

			for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();
				for (int outputI = 0; outputI < powerOutputs.length; outputI++) {
					powerOutputSum = getCplex().sum(powerOutputSum, powerOutputs[outputI][timeStep]);				
				}
				for (int inputI = 0; inputI < powerInputs.length; inputI++) {
					powerInputSum = getCplex().sum(powerInputSum, powerInputs[inputI][timeStep]);				
				}
				getCplex().addEq(powerOutputSum, powerInputSum);
			}
		}
	}


	/**
	 * Generate restrictive dependency.
	 * only one active member per side, all others == 0
	 *
	 * @param powerOutputs array of all relevant power output arrays (new IloNumVar [][] {})
	 * @param powerInputs array of all relevant power input arrays (new IloNumVar [][] {})
	 * @throws IloException the ilo exception
	 */
	private static void generateRestrictiveDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// binary variables for dependency
		if (powerOutputs.length >0 && powerInputs.length >0) {

			IloIntVar[][] binaryVariablesOutputSide = new IloIntVar[getArrayLength()][powerOutputs.length];
			IloIntVar[][] binaryVariablesInputSide = new IloIntVar[getArrayLength()][powerInputs.length];

			for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
				IloNumExpr binarySumOutput = getCplex().numExpr();
				IloNumExpr binarySumInput = getCplex().numExpr();

				IloNumVar powerOutputRestrDep = getCplex().numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
				IloNumVar powerInputRestrDep = getCplex().numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
				// any output connected to any other input
				getCplex().addEq(powerOutputRestrDep, powerInputRestrDep);


				//---- OUTPUT ----
				for (int outputI = 0; outputI < powerOutputs.length; outputI++) {
					binaryVariablesOutputSide[timeStep][outputI] = getCplex().intVar(0, 1);
					binarySumOutput = getCplex().sum(binarySumOutput, binaryVariablesOutputSide[timeStep][outputI]);

					// if binary variable =1, then output active
					getCplex().add(
							getCplex().ifThen(
									getCplex().eq(binaryVariablesOutputSide[timeStep][outputI], 1),
									getCplex().eq(powerOutputRestrDep, powerOutputs[outputI][timeStep])
									)
							);
					System.out.println("Output "+outputI + " added to restrictive dependency");
				}

				//---- INPUT ----
				for (int inputI = 0; inputI < powerInputs.length; inputI++) {
					binaryVariablesInputSide[timeStep][inputI] = getCplex().intVar(0, 1);
					binarySumInput = getCplex().sum(binarySumInput, binaryVariablesInputSide[timeStep][inputI]);

					// if binary variable =1, then input active
					getCplex().add(
							getCplex().ifThen(
									getCplex().eq(binaryVariablesInputSide[timeStep][inputI], 1),
									getCplex().eq(powerInputRestrDep, powerInputs[inputI][timeStep])
									)
							);
				}

				// sum time step = 1
				getCplex().addEq(binarySumInput, 1);
				getCplex().addEq(binarySumOutput, 1);
			}
		}
	}


	/**
	 * Gets the electricity price.
	 *
	 * @return the electricity price
	 */
	private static double[] getElectricityPrice() {
		double[] electricityPrice = new double[] {
				8540.87,37.21,320.62,32.27,51044.03,63.76,44.84,39.2,58.244448,4344444.46,42.47,47.48, 58.96,42.99,43.27,50.5,
				80.21,30.64,21.47,35.41,15.67,24.15,43.98,55.36,23.58,42.56,60.2,87.49,22.46,38.95,	31.68,60.19,
				17.49,37.23,40.51,57.98,46.62,46.74,61.23,71.33,57.2,55.6,59.39,81.35,48.79,58.3,58.01,57.73,71.85,
				64.5,57.07,57.84,44.25,38.6,65.02,67.76,42.31,62.06,83.36,100.31,57.25,66.51,84.52,95.44,73.57,84.5,
				85.41,98.72,75.05,98.16,101.62,96.88,127.03,92.24,102.1,79.11,140.98,93.1,91.67,66.07,125.87,89.96,
				76.41,48.12,100.5,74.96,74.03,64.77,100.75,75.26,87.21,38.04,85.35,60.38,56.9,35.95
		};
		double[] electricityPriceNew = new double[getArrayLength()];


		if (getArrayLength()>electricityPrice.length) {
			for (int j = 0; j < electricityPrice.length; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			for (int k = electricityPrice.length; k<getArrayLength(); k++) {
				electricityPriceNew[k] = electricityPrice[(int) (Math.random()*electricityPrice.length)];
			}
			return electricityPriceNew;
		} else {
			for (int j = 0; j < getArrayLength(); j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			return electricityPriceNew; 
		}
	}

	/**
	 * Write results to file.
	 *
	 * @param contentToWrite the content to write
	 * @param fileName the file name
	 * @param header the header
	 */
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

	/**
	 * Gets the cplex.
	 *
	 * @return the cplex
	 */

	public static IloCplex getCplex() {
		if(globalCplex==null) {
			try {
				globalCplex = new IloCplex();
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
		return globalCplex;
	}



	/**
	 * Gets the array length.
	 *
	 * @return the arrayLength
	 */
	public static int getArrayLength() {
		return arrayLength;
	}



	/**
	 * Sets the array length.
	 *
	 * @param arrayLength the arrayLength to set
	 */
	public static void setArrayLength(int arrayLength) {
		DesignPatterns.arrayLength = arrayLength;
	}



	/**
	 * Gets the time interval.
	 *
	 * @return the timeInterval
	 */
	public static double getTimeInterval() {
		return timeInterval;
	}



	/**
	 * Sets the time interval.
	 *
	 * @param timeInterval the timeInterval to set
	 */
	public static void setTimeInterval(double timeInterval) {
		DesignPatterns.timeInterval = timeInterval;
	}

	/**
	 * Gets the optimality gap.
	 *
	 * @return the optimalityGap
	 */
	public static double getOptimalityGap() {
		return optimalityGap;
	}

	/**
	 * Sets the optimality gap.
	 *
	 * @param optimalityGap the optimalityGap to set
	 */
	public static void setOptimalityGap(double optimalityGap) {
		DesignPatterns.optimalityGap = optimalityGap;
	}

	/**
	 * @return the decisionVariables
	 */
	public static HashMap<String, IloNumVar[]> getDecisionVariablesVector() {
		return decisionVariablesVector;
	}

	/**
	 * @param decisionVariables the decisionVariables to set
	 */
	public static void setDecisionVariablesVector(HashMap<String, IloNumVar[]> decisionVariables) {
		DesignPatterns.decisionVariablesVector = decisionVariables;
	}

	/**
	 * @return the decisionVariablesMatrix
	 */
	public static HashMap<String, IloNumVar[][]> getDecisionVariablesMatrix() {
		return decisionVariablesMatrix;
	}

	/**
	 * @param decisionVariablesMatrix the decisionVariablesMatrix to set
	 */
	public static void setDecisionVariablesMatrix(HashMap<String, IloNumVar[][]> decisionVariablesMatrix) {
		DesignPatterns.decisionVariablesMatrix = decisionVariablesMatrix;
	}

	public static void optimizationModel(double timeInterval, int arrayLength) throws IloException {
		try {
			@SuppressWarnings("resource")
			IloCplex cplex = new IloCplex();

			//			additional parameters for system
			double maxPowerSystem = 100; 
			double constantHydrogenDemand = 900*timeInterval;

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

			/**
			//	DEPENDENCY 1
			// Liste möglicher Entscheidungsvariablen je Seite, 
			List<IloNumVar[]> listOfResourcesD1SA = new ArrayList<IloNumVar[]>();
			listOfResourcesD1SA.add(powerOutputElectrolyzer1);
			List<IloNumVar[]> listOfResourcesD1SB = new ArrayList<IloNumVar[]>();
			listOfResourcesD1SB.add(powerInputStorage);
			int numberOfEnergyResourcesDep1SideA = listOfResourcesD1SA.size(); // Länge Liste zählen
			int numberOfEnergyResourcesDep1SideB = listOfResourcesD1SB.size(); 
			IloIntVar[][] dependency1SideA = new IloIntVar[arrayLength][numberOfEnergyResourcesDep1SideA];
			IloIntVar[][] dependency1SideB = new IloIntVar[arrayLength][numberOfEnergyResourcesDep1SideB];
			IloNumVar[] powerFlowSideDep1SideA = cplex.numVarArray(arrayLength, 0, Double.MAX_VALUE);
			IloNumVar[] powerFlowSideDep1SideB = cplex.numVarArray(arrayLength, 0, Double.MAX_VALUE);


			for (int i = 0; i < arrayLength; i++) {
				for (int j = 0; j < numberOfEnergyResourcesDep1SideA; j++) {
					dependency1SideA[i][j] = cplex.intVar(0, 1);
				}
				for (int j = 0; j < numberOfEnergyResourcesDep1SideB; j++) {
					dependency1SideB[i][j] = cplex.intVar(0, 1);
				}
			}

			for (int i = 0; i < arrayLength; i++) {
				IloNumExpr binarySumdependency1SideA = cplex.numExpr();
				IloNumExpr binarySumdependency1SideB = cplex.numExpr();

				for (int j = 0; j < numberOfEnergyResourcesDep1SideA; j++) {
					binarySumdependency1SideA = cplex.sum(binarySumdependency1SideA,dependency1SideA[i][j]);
					cplex.add(
							cplex.ifThen(
									cplex.eq(dependency1SideA[i][j], 1),
									cplex.eq(powerFlowSideDep1SideA[i], listOfResourcesD1SA.get(j)[i])
									)
							);
				}
				for (int j = 0; j < numberOfEnergyResourcesDep1SideB; j++) {
					binarySumdependency1SideB = cplex.sum(binarySumdependency1SideB,dependency1SideB[i][j]);

					cplex.add(
							cplex.ifThen(
									cplex.eq(dependency1SideB[i][j], 1),
									cplex.eq(powerFlowSideDep1SideB[i], listOfResourcesD1SB.get(j)[i])
									)
							);
				}

				// sum of binary decision variables per side = 1 forall t
				cplex.addEq(binarySumdependency1SideA, 1);
				cplex.addEq(binarySumdependency1SideB, 1);

			}
			 */
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


			// --------------------- ramp limits ----------------------------------
			for (int i = 1; i < arrayLength; i++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr sumMinRamp = cplex.numExpr();
				IloNumExpr sumMaxRamp = cplex.numExpr();
				IloNumExpr powerDifferenceEl1 = cplex.numExpr();

				powerDifferenceEl1 = cplex.diff(powerInputElectrolyzer1[i], powerInputElectrolyzer1[i-1]);

				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					sumMinRamp = cplex.sum(sumMinRamp, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMinRampInput()));
					sumMaxRamp = cplex.sum(sumMaxRamp, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMaxRampInput()));
				}


				cplex.addGe(cplex.abs(powerDifferenceEl1), cplex.prod(timeInterval, sumMinRamp));
				cplex.addLe(cplex.abs(powerDifferenceEl1), cplex.prod(timeInterval, sumMaxRamp));

			}


			// --------------------- System state by power limits ----------------------------------
			for (int i = 0; i < arrayLength; i++) {
				cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[i]), 1);

				IloNumExpr powerMinSum = cplex.numExpr();
				IloNumExpr powerMaxSum = cplex.numExpr();
				IloNumExpr powerOutputMaxSum = cplex.numExpr();
				for (int j = 0; j < resourceParameters.get(0).getNumberOfSystemStates(); j++) {
					powerMinSum = cplex.sum(powerMinSum, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMinPower()));
					powerMaxSum = cplex.sum(powerMaxSum, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMaxPower()));
					powerOutputMaxSum = cplex.sum(powerOutputMaxSum, cplex.prod(statesIntArrayElectrolyzer1[i][j], resourceParameters.get(0).getSystemStates().get(j).getMaxPowerOutput()));

				}
				cplex.addGe(powerInputElectrolyzer1[i], powerMinSum);
				cplex.addLe(powerInputElectrolyzer1[i], powerMaxSum);
				cplex.addLe(powerOutputElectrolyzer1[i], powerOutputMaxSum);
			}




			// --------------------- State sequences and holding duration ----------------------------------

			// initial state	
			//Constraints nur für Zeitpunkt 0: Zustand 0 = 1, alle anderen 0, start in state = off 
			//
			//			cplex.addEq(statesIntArrayElectrolyzer1[0][0], 1);
			//			cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[0]), 1);

			for (int i = 1; i < arrayLength; i++) {

				// only one active state per time step
				//				cplex.addEq(cplex.sum(statesIntArrayElectrolyzer1[i]), 1);

				for (int s = 0; s < resourceParameters.get(0).getNumberOfSystemStates(); s++) {
					// min duration
					if (resourceParameters.get(0).getSystemStates().get(s).getMinStateDuration() == NOLIMIT) {

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
					if (resourceParameters.get(0).getSystemStates().get(s).getMaxStateDuration() == NOLIMIT) {

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

			//	System.out.println(cplex);
			cplex.exportModel("model.lp");

			// set objective function 
			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < arrayLength; i++) {
				objective.addTerm(timeInterval*getElectricityPrice()[i], powerInput[i]);
			}
			cplex.addMinimize(objective);

			// solver specific parameters
			//cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, getOptimalityGap());
			long start = System.currentTimeMillis();
			System.out.println("cplex solve");
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
					optimizationResults[i][15] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][2]);
					optimizationResults[i][16] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][3]);
					optimizationResults[i][17] = cplex.getValue(statesIntArrayElectrolyzer1[i-1][4]);
					optimizationResults[i][18] = getElectricityPrice()[i-1];
					//					System.out.println(cplex.getValue(binaryVariableForAbsoluteDifferenceEl1[i-1]));
					//					optimizationResults[i][19] = cplex.getValue(binaryVariableForAbsoluteDifferenceEl1[i-1]);
				}

				writeResultsToFile(optimizationResults, "designpatterns", "stateOfCharge, powerInput, binary1, binary2, binary3, binary4, binary5, binary6, binary7, binary8, binary9, binary10, powerOutput, systemState1, systemState2,systemState3, systemState4, systemState5, electricityPrice, absoluteValueForRampElectrolyzer1");

			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
	}
}