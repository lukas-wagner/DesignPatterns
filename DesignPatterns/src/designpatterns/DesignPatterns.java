package designpatterns;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DesignPatterns.
 *
 * @author Lukas Wagner, lukas.wagner@hsu-hh.de
 * Code for Design Patterns for Energy Optimization.
 * Conference paper "Design Patterns for Optimization Models of Flexible Energy Resources", ONCON 2023
 */
/**
 * @author Lukas Wagner, Helmut-Schmidt-University Hamburg, lukas.wagner@hsu-hh.de
 *
 */
public class DesignPatterns {

	/** The global cplex. */
	static	IloCplex globalCplex = null; 

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
	}

	/**
	 * Creation of decision variables from ResourceParameters.
	 * saves created decision variables to 2 hashmaps, depending on #2 of colums (1 -> vector, 2-> matrix)
	 *
	 * @param maxPowerSystem the max power system
	 * @throws IloException the ilo exception
	 */
	public static void creationOfDecisionVariables (double maxPowerSystem) throws IloException {
		// ----- System decision variables -----
		IloNumVar[] powerInput = getCplex().numVarArray(getArrayLength(),  0 ,  maxPowerSystem);
		getDecisionVariablesVector().put("System"+"-"+INPUT+"-"+POWER, powerInput);

		IloNumVar[] powerOutputSystem = getCplex().numVarArray(getArrayLength(), 0, Double.MAX_VALUE);
		getDecisionVariablesVector().put("System"+"-"+OUTPUT+"-"+POWER, powerOutputSystem);

		// --- Create decision variables for each resource in resourceParameters -----
		List<String> converterList = new ArrayList<String>();
		List<String> storageList = new ArrayList<String>();
		// extract names of list 
		for (int i = 0; i < getResourceParameters().size(); i++) {
			ResourceParameters resource = getResourceParameters().get(i);
			if (resource.isStorage()==false) {
				// get name of resource
				converterList.add(resource.getName());
			} else {
				// get name of storage
				storageList.add(resource.getName());

			}
		}

		// --- Converters ---
		String nameOfResource; 
		for (int resource = 0; resource < converterList.size(); resource++) {
			nameOfResource = converterList.get(resource);			

			int indexOfResource = -1;
			indexOfResource = findIndexByName(nameOfResource);
			if (indexOfResource==-1) System.err.println("Resource not found in list of getResourceParameters()!");

			IloNumVar[] powerOutputResource = getCplex().numVarArray(
					getArrayLength(), 
					getResourceParameters().get(indexOfResource).getMinPowerOutput(), 
					getResourceParameters().get(indexOfResource).getMaxPowerOutput()
					);
			getDecisionVariablesVector().put(nameOfResource+"-"+OUTPUT+"-"+POWER, powerOutputResource);
			//		System.out.println("Output variable "+ nameOfResource);

			// Create multiple decision variable per resource, one for each input (power input && (if pla) binary pla, power pla)
			for (int numberOfInput = 0; numberOfInput < getResourceParameters().get(indexOfResource).getPlaList().size(); numberOfInput++) {
				IloNumVar[] powerInputResource = getCplex().numVarArray(
						getArrayLength(), 
						getResourceParameters().get(indexOfResource).getMinPowerInputs().get(numberOfInput), 
						getResourceParameters().get(indexOfResource).getMaxPowerInputs().get(numberOfInput)
						);
				getDecisionVariablesVector().put(nameOfResource+"-"+INPUT+"-"+Integer.toString(numberOfInput)+"-"+POWER, powerInputResource);
				System.out.println(nameOfResource+"-"+INPUT+"-"+Integer.toString(numberOfInput)+"-"+POWER);
				//			System.out.println("Input variable "+ nameOfResource);

				if (getResourceParameters().get(indexOfResource).getPlaList().get(0).size()!=0) {
					// only create decision variable, if necessary

					// binary decision variable for segements of pla
					IloIntVar[][] binariesPlaResource = new IloIntVar[getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size()][getArrayLength()]; 
					for (int plaSegment = 0; plaSegment < getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size(); plaSegment++) {
						binariesPlaResource[plaSegment] = getCplex().intVarArray(getArrayLength(), 0 , 1);
					}
					getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+Integer.toString(numberOfInput)+"-"+BINARY, binariesPlaResource);
					System.out.println("binary power variable "+ nameOfResource+"-"+POWER+"-"+Integer.toString(numberOfInput)+"-"+BINARY);

					// discrete decision variable for power inputs of each segement
					IloNumVar[][] powerInputLinearSegmentsResource = new IloNumVar[getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size()][getArrayLength()]; 
					for (int plaSegment = 0; plaSegment < getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size(); plaSegment++) {
						powerInputLinearSegmentsResource[plaSegment] = getCplex().numVarArray(
								getArrayLength(),
								getResourceParameters().get(indexOfResource).getMinPowerInputs().get(numberOfInput), 
								getResourceParameters().get(indexOfResource).getMaxPowerInputs().get(numberOfInput)
								);
					}
					getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+Integer.toString(numberOfInput)+"-"+SEGMENT, powerInputLinearSegmentsResource);
					System.out.println("power segment variable "+ nameOfResource+POWER+"-"+Integer.toString(numberOfInput)+"-"+SEGMENT);
				}
			}
			if (getResourceParameters().get(indexOfResource).getNumberOfSystemStates()!=0) {
				// only create decision variable, if necessary
				IloIntVar[][] statesIntArrayResource = new IloIntVar[getArrayLength()+1][getResourceParameters().get(indexOfResource).getNumberOfSystemStates()];
				for (int timeStep = 0; timeStep < getArrayLength()+1; timeStep++) {
					for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
						statesIntArrayResource[timeStep][state] = getCplex().intVar(0, 1);
					}
				}
				getDecisionVariablesMatrix().put(nameOfResource+"-"+POWER+"-"+STATE, statesIntArrayResource);
				//				System.out.println("state variable "+ nameOfResource);
			}
			System.out.println("Created variables for "+ nameOfResource);
		}


		// --- Storage ---
		String nameOfStorage = ""; 
		for (int storage = 0; storage < storageList.size(); storage++) {
			nameOfStorage = storageList.get(storage);		

			int indexOfStorage = -1;
			indexOfStorage = findIndexByName(nameOfStorage);
			if (indexOfStorage==-1) System.err.println("Storage System not found in list of getResourceParameters()!");

			// check if resource is actually storage!
			if (getResourceParameters().get(indexOfStorage).isStorage()==true) {
				IloNumVar[] powerInputStorage = getCplex().numVarArray(
						getArrayLength(), 
						getResourceParameters().get(indexOfStorage).getMinPowerInput(), 
						getResourceParameters().get(indexOfStorage).getMaxPowerInput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+INPUT+"-"+POWER, powerInputStorage);
				//				System.out.println("power input variable "+ nameOfStorage);
				IloNumVar[] stateOfCharge = getCplex().numVarArray(
						getArrayLength()+1, 
						getResourceParameters().get(indexOfStorage).getMinimumStorageCapacity(), 
						getResourceParameters().get(indexOfStorage).getMaximumStorageCapacity()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+SOC+"-"+POWER, stateOfCharge);
				//				System.out.println("soc variable "+ nameOfStorage);
				IloNumVar[] powerOutputStorage = getCplex().numVarArray(
						getArrayLength(), 
						getResourceParameters().get(indexOfStorage).getMinPowerOutput(), 
						getResourceParameters().get(indexOfStorage).getMaxPowerOutput()
						);
				getDecisionVariablesVector().put(nameOfStorage+"-"+OUTPUT+"-"+POWER, powerOutputStorage);
				//				System.out.println("power output variable "+ nameOfStorage);


				if (getResourceParameters().get(indexOfStorage).getNumberOfSystemStates()!=0) {
					// only create decision variable, if necessary
					IloIntVar[][] statesIntArrayResource = new IloIntVar[getArrayLength()+1][getResourceParameters().get(indexOfStorage).getNumberOfSystemStates()];
					for (int timeStep = 0; timeStep < getArrayLength()+1; timeStep++) {
						for (int state = 0; state < getResourceParameters().get(indexOfStorage).getNumberOfSystemStates(); state++) {
							statesIntArrayResource[timeStep][state] = getCplex().intVar(0, 1);
						}
					}
					getDecisionVariablesMatrix().put(nameOfStorage+"-"+POWER+"-"+STATE, statesIntArrayResource);
					//				System.out.println("state variable "+ nameOfResource);
				}

				if (getResourceParameters().get(indexOfStorage).getDegradation()!=0) {
					IloNumVar[] stateOfChargeMax = getCplex().numVarArray(
							getArrayLength()+1, 
							getResourceParameters().get(indexOfStorage).getMinimumStorageCapacity(), 
							getResourceParameters().get(indexOfStorage).getMaximumStorageCapacity()
							);
					getDecisionVariablesVector().put(nameOfStorage+"-"+SOC+"MAX"+"-"+POWER, stateOfChargeMax);
				}
			}
			System.out.println("Created variables for "+ nameOfStorage);
		}
	}

	/**
	 * Find index by name in list getResourceParameters().
	 *
	 * @param nameToFind the name to find
	 * @return the index
	 */
	public static int findIndexByName(String nameToFind) {
		for (int i = 0; i < getResourceParameters().size(); i++) {
			ResourceParameters resource = getResourceParameters().get(i);
			if (resource.getName().equals(nameToFind)) {
				return i;
			}
		}
		return -1; // Return -1 if name not found
	}

	/**
	 * Gets the decision variable from matrix.
	 *
	 * @param name the name
	 * @param medium the medium
	 * @param numberOfInput the number of input, if -1, omitted
	 * @param type the type
	 * @return the decision variable from matrix
	 */
	public static IloNumVar[][] getDecisionVariableFromMatrix(String name, String medium, int numberOfInput, String type) {
		if (numberOfInput==-1) {
			return getDecisionVariablesMatrix().get(name+"-"+medium+"-"+type);
		}
		else {
			return getDecisionVariablesMatrix().get(name+"-"+medium+"-"+Integer.toString(numberOfInput)+"-"+type);
		}
	}

	/**
	 * Gets the decision variable from vector.
	 *
	 * @param name the name
	 * @param port the port
	 * @param numberOfInput the number of input, if -1, omitted
	 * @param medium the medium
	 * @return the decision variable from vector
	 */
	public static IloNumVar[] getDecisionVariableFromVector(String name, String port, int numberOfInput, String medium) {
		if (numberOfInput==-1) {
			return getDecisionVariablesVector().get(name+"-"+port+"-"+medium);
		} else {
			return getDecisionVariablesVector().get(name+"-"+port+"-"+Integer.toString(numberOfInput)+"-"+medium);
		}
	}

	/**
	 * Generate input output relationship.
	 *
	 * @param nameOfResource the name of resource
	 * @throws IloException the ilo exception
	 */
	public static void generateInputOutputRelationship (String nameOfResource) throws IloException {
		// Pattern input output
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of resourceParameters!");

		// get decision variables from vector/matrix
		IloNumVar[] powerOutputResource = getDecisionVariableFromVector(nameOfResource, OUTPUT, -1, POWER);

		if (getResourceParameters().get(indexOfResource).getNumberOfLinearSegments()==0
				&& getResourceParameters().get(indexOfResource).getEfficiency()!=0
				//				&& getResourceParameters().get(indexOfResource).getNumberOfInputs()==1
				) {
			// Case 1: efficiency
			System.out.println("Input-Output Relationship by Efficiency for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				for (int numberOfInput = 0; numberOfInput < getResourceParameters().get(indexOfResource).getPlaList().size(); numberOfInput++) {
					powerOutputSum = getCplex().sum(
							powerOutputSum, 
							getCplex().prod(
									getDecisionVariableFromVector(nameOfResource, INPUT, numberOfInput, POWER)[timestep], 
									getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(0).getSlope()
									)
							);
				}
				getCplex().addEq(powerOutputResource[timestep], powerOutputSum);
			} 
		} else if (getResourceParameters().get(indexOfResource).getPlaList().size()==0
				&& getResourceParameters().get(indexOfResource).getEfficiency()==0
				&& getResourceParameters().get(indexOfResource).getSlope()!=0) {
			// y = ax+b
			System.out.println("Input-Output Relationship y = ax+b for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				for (int numberOfInput = 0; numberOfInput < getResourceParameters().get(indexOfResource).getPlaList().size(); numberOfInput++) {
					powerOutputSum = getCplex().sum(
							powerOutputSum, 
							getCplex().sum(getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(0).getIntercept(), 
									getCplex().prod(
											getDecisionVariableFromVector(nameOfResource, INPUT,numberOfInput, POWER)[timestep], 
											getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(0).getSlope()
											)
									)
							);
				}
				getCplex().addEq(powerOutputResource[timestep], powerOutputSum);
			}
		} else if (getResourceParameters().get(indexOfResource).getPlaList().size()>0) {
			// Case 3: pla
			System.out.println("Input-Output Relationship piecewise linear approximation for "+ nameOfResource);
			for (int timestep = 0; timestep < getArrayLength(); timestep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				// sum binaries[i] = 1 part 1

				for (int numberOfInput = 0; numberOfInput < getResourceParameters().get(indexOfResource).getPlaList().size(); numberOfInput++) {
					IloNumExpr binarySum = getCplex().numExpr();
					IloNumExpr powerInputSum = getCplex().numExpr();

					for (int plaSegment = 0; plaSegment < getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size(); plaSegment++) {

						// lowerBound * power <= binary
						getCplex().addLe(
								getCplex().prod(
										getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(plaSegment).getLowerBound(), 
										(IloIntVar) getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, BINARY)[plaSegment][timestep]
										), 
								getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, SEGMENT)[plaSegment][timestep]
								);

						//power <= upperBound * binary
						getCplex().addLe(
								getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, SEGMENT)[plaSegment][timestep], 
								getCplex().prod(
										getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(plaSegment).getUpperBound(), 
										(IloIntVar) getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, BINARY)[plaSegment][timestep]
										)
								);

						// sum binaries[i] = 1 part 2
						binarySum = getCplex().sum(binarySum,(IloIntVar) getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, BINARY)[plaSegment][timestep]);
						powerInputSum = getCplex().sum(powerInputSum, getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, SEGMENT)[plaSegment][timestep]);
					}

					// sum binaries[i] = 1 part 3           
					getCplex().addEq(binarySum, 1);

					// sum powerInputElectrolyzer1LinearSegments[i] = powerOutputElectrolyzer1[i] part 3
					getCplex().addEq(powerInputSum, getDecisionVariableFromVector(nameOfResource, INPUT, numberOfInput, POWER)[timestep]);

					for (int plaSegment = 0; plaSegment < getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).size(); plaSegment++) {
						getCplex().add(
								getCplex().ifThen(
										getCplex().eq((IloIntVar) getDecisionVariableFromMatrix(nameOfResource, POWER, numberOfInput, BINARY)[plaSegment][timestep], 1),
										getCplex().eq(powerOutputResource[timestep],
												getCplex().sum(
														getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(plaSegment).getIntercept(),
														getCplex().prod(
																powerInputSum,
																//powerInputResource[timestep],
																//powerInputResourceLinearSegments[plasegment][timestep],
																getResourceParameters().get(indexOfResource).getPlaList().get(numberOfInput).get(plaSegment).getSlope()
																)
														)
												)
										)
								);
					}
				}
				// TODO funktioniert nicht
				//				getCplex().addEq(powerOutputResource[timestep], powerOutputSum);
			}
		}
		System.out.println("Input-output successfully created for resource " + nameOfResource);
	}

	/**
	 * Generate system state selection by power limits.
	 * System state selection always based on 1st input!
	 *
	 * @param nameOfResource the name of resource
	 * @throws IloException the ilo exception
	 */
	public static void generateSystemStateSelectionByPowerLimits (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of getResourceParameters()!");

		if (getResourceParameters().get(indexOfResource).isStorage()==false) {
			// 1st input is always relevant for system states
			IloNumVar[] powerInputResource = getDecisionVariableFromVector(nameOfResource, INPUT, 0, POWER);
			IloNumVar[] powerOutputResource  = getDecisionVariableFromVector(nameOfResource, OUTPUT, -1, POWER);
			IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, -1, STATE);

			//set initial system state, all other states = 0 
			getCplex().addEq(statesIntArrayResource[0][getResourceParameters().get(indexOfResource).getInitialSystemState()], 1);
			getCplex().addEq(getCplex().sum(statesIntArrayResource[0]), 1);

			for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {
				getCplex().addEq(getCplex().sum(statesIntArrayResource[timestep]), 1);

				IloNumExpr powerMinSum = getCplex().numExpr();
				IloNumExpr powerMaxSum = getCplex().numExpr();
				IloNumExpr powerOutputMaxSum = getCplex().numExpr();
				for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
					powerMinSum = getCplex().sum(
							powerMinSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinPower()
									)
							);
					powerMaxSum = getCplex().sum(
							powerMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxPower()
									)
							);
					powerOutputMaxSum = getCplex().sum(
							powerOutputMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxPowerOutput()
									)
							);
				}
				getCplex().addGe(powerInputResource[timestep-1], powerMinSum);
				getCplex().addLe(powerInputResource[timestep-1], powerMaxSum);
				getCplex().addLe(powerOutputResource[timestep-1], powerOutputMaxSum);
			}
			System.out.println("System states created for "+ nameOfResource);
		}
		else {
			// Create system states for storage by SOC

			IloNumVar[] powerOutputResource  = getDecisionVariableFromVector(nameOfResource, OUTPUT,-1, POWER);
			IloNumVar[] stateOfChargeResource  = getDecisionVariableFromVector(nameOfResource, SOC,-1, POWER);
			IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, -1,  STATE);

			//set initial system state, all other states = 0 
			getCplex().addEq(statesIntArrayResource[0][getResourceParameters().get(indexOfResource).getInitialSystemState()], 1);
			//		getCplex().addEq(statesIntArrayResource[0][1], 1);
			getCplex().addEq(getCplex().sum(statesIntArrayResource[0]), 1);

			for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {
				getCplex().addEq(getCplex().sum(statesIntArrayResource[timestep]), 1);

				IloNumExpr socMinSum = getCplex().numExpr();
				IloNumExpr socMaxSum = getCplex().numExpr();
				IloNumExpr powerOutputMaxSum = getCplex().numExpr();
				for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
					socMinSum = getCplex().sum(
							socMinSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinPower()
									)
							);
					socMaxSum = getCplex().sum(
							socMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxPower()
									)
							);
					powerOutputMaxSum = getCplex().sum(
							powerOutputMaxSum, 
							getCplex().prod(
									statesIntArrayResource[timestep][state], 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxPowerOutput()
									)
							);
				}
				getCplex().addGe(stateOfChargeResource[timestep-1], socMinSum);
				getCplex().addLe(stateOfChargeResource[timestep-1], socMaxSum);
				getCplex().addLe(powerOutputResource[timestep-1], powerOutputMaxSum);
			}
			System.out.println("System states (storage) created for "+ nameOfResource);
		}
	}

	/**
	 * Generate state sequences and holding duration.
	 *
	 * @param nameOfResource the name of resource
	 * @throws IloException the ilo exception
	 */
	public static void generateStateSequencesAndHoldingDuration (String nameOfResource) throws IloException {
		// find id by name
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of getResourceParameters()!");
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, -1, STATE);

		for (int timestep = 1; timestep < getArrayLength()+1; timestep++) {

			for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
				// min duration
				if (getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinStateDuration() == NOLIMIT) {

				} else {
					IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep][state],statesIntArrayResource[timestep-1][state]);
					IloNumExpr constraintRightSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinStateDuration(); timeInStateCounter++) {

						try {
							constraintRightSide = getCplex().sum(constraintRightSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 
						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(getCplex().prod(
							constraintLeftSide, 
							Math.min(counter, 
									getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinStateDuration()
									)
							), 
							constraintRightSide);
				}
				//max Duration
				if (getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxStateDuration() == NOLIMIT) {

				}else{
					IloNumExpr constraintLeftSide = statesIntArrayResource[timestep][state];

					int counter = 1;
					for (int timeInStateCounter = 1; timeInStateCounter < getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()+1; timeInStateCounter++) {

						try {
							constraintLeftSide = getCplex().sum(constraintLeftSide,statesIntArrayResource[timestep+timeInStateCounter][state]);
							counter++; 

						} catch (Exception e) {
							// TODO: handle exception
						} 
					}
					getCplex().addLe(constraintLeftSide, Math.min(counter,getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxStateDuration()));
				}


				//State sequences
				IloNumExpr constraintLeftSide = getCplex().diff(statesIntArrayResource[timestep-1][state],statesIntArrayResource[timestep][state]);
				IloNumExpr constraintRightSide = statesIntArrayResource[timestep][getResourceParameters().get(indexOfResource).getSystemStates().get(state).getFollowerStates()[0]];

				for (int followerStatesCounter = 1; followerStatesCounter < getResourceParameters().get(indexOfResource).getSystemStates().get(state).getFollowerStates().length; followerStatesCounter++) {
					constraintRightSide = getCplex().sum(constraintRightSide, statesIntArrayResource[timestep][getResourceParameters().get(indexOfResource).getSystemStates().get(state).getFollowerStates()[followerStatesCounter]]);
				}
				getCplex().addLe(constraintLeftSide, constraintRightSide);
			}
		}
		System.out.println("State sequences and holding duration created for " + nameOfResource);
	}

	/**
	 * Generate ramp limits for 1st input.
	 *
	 * @param nameOfResource the name of resource
	 * @param port the side (INPUT XOR OUTPUT)
	 * @throws IloException the ilo exception
	 */
	public static void generateRampLimits (String nameOfResource, String port) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of getResourceParameters()!");

		IloNumVar[] relevantPowerFlowResource = getDecisionVariableFromVector(nameOfResource, port, 0, POWER); 
		IloIntVar[][] statesIntArrayResource = (IloIntVar[][]) getDecisionVariableFromMatrix(nameOfResource, POWER, -1, STATE);

		if (port == INPUT) {
			// --------------------- ramp limits ----------------------------------
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin

				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();
				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				if (getResourceParameters().get(indexOfResource).getNumberOfSystemStates()==0) {
					// Model without states -> only one value for ramp
					getCplex().addGe(getCplex().abs(powerDifferenceEl1), getTimeInterval()*getResourceParameters().get(indexOfResource).getMinRampInput());
					getCplex().addLe(getCplex().abs(powerDifferenceEl1), getTimeInterval()*getResourceParameters().get(indexOfResource).getMaxRampInput());
				} else {
					// Case with multiple states, |states| values for ramp
					IloNumExpr sumMinRamp = getCplex().numExpr();
					IloNumExpr sumMaxRamp = getCplex().numExpr();

					for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
						sumMinRamp = getCplex().sum(
								sumMinRamp, 
								getCplex().prod(
										statesIntArrayResource[timestep][state], // TODO check if timestep+1 is correct
										getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinRampInput()
										)
								);
						sumMaxRamp = getCplex().sum(
								sumMaxRamp, 
								getCplex().prod(
										statesIntArrayResource[timestep][state], 
										getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxRampInput()
										)
								);
					}
					getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
					getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
				}

			}
		} else if (port == OUTPUT) {
			for (int timestep = 1; timestep < getArrayLength(); timestep++) {
				//				TODO was ist für das erste Intervall? -> pI[0] <= rampmax, pi[0]>=rampmin
				IloNumExpr powerDifferenceEl1 = getCplex().numExpr();
				powerDifferenceEl1 = getCplex().diff(relevantPowerFlowResource[timestep], relevantPowerFlowResource[timestep-1]);

				if (getResourceParameters().get(indexOfResource).getNumberOfSystemStates()==0) {
					// Model without states -> only one value for ramp
					getCplex().addGe(getCplex().abs(powerDifferenceEl1), getTimeInterval()*getResourceParameters().get(indexOfResource).getMinRampOutput());
					getCplex().addLe(getCplex().abs(powerDifferenceEl1), getTimeInterval()*getResourceParameters().get(indexOfResource).getMaxRampOutput());
				} else {
					IloNumExpr sumMinRamp = getCplex().numExpr();
					IloNumExpr sumMaxRamp = getCplex().numExpr();

					for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
						sumMinRamp = getCplex().sum(
								sumMinRamp, 
								getCplex().prod(
										statesIntArrayResource[timestep][state], 
										getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMinRampOutput()
										)
								);
						sumMaxRamp = getCplex().sum(
								sumMaxRamp, 
								getCplex().prod(
										statesIntArrayResource[timestep][state], 
										getResourceParameters().get(indexOfResource).getSystemStates().get(state).getMaxRampOutput()
										)
								);
					}
					getCplex().addGe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMinRamp));
					getCplex().addLe(getCplex().abs(powerDifferenceEl1), getCplex().prod(getTimeInterval(), sumMaxRamp));
				}
			}
		}
		System.out.println("Ramp limits created for "+ nameOfResource);
	}

	/**
	 * Generate energy balance for storage system.
	 *
	 * @param nameOfResource the name of resource
	 * @throws IloException the ilo exception
	 */
	public static void generateEnergyBalanceForStorageSystem (String nameOfResource) throws IloException {
		int indexOfResource = -1;
		indexOfResource = findIndexByName(nameOfResource);
		if (indexOfResource==-1) System.err.println("Resource not found in list of getResourceParameters()!");
		if (getResourceParameters().get(indexOfResource).isStorage()==false) System.err.println("System "+ nameOfResource + " is not a storage system!");

		IloNumVar[] stateOfCharge = getDecisionVariableFromVector(nameOfResource, SOC, -1, POWER);
		IloNumVar[] powerInputStorage = getDecisionVariableFromVector(nameOfResource, INPUT, -1, POWER); 
		IloNumVar[] powerOutputStorage = getDecisionVariableFromVector(nameOfResource, OUTPUT, -1, POWER);
		IloNumVar[] maxStateOfCharge = getDecisionVariableFromVector(nameOfResource, SOC+"MAX", -1, POWER);

		// --------------------- storage and loss ----------------------------------
		for (int timestep = 0; timestep < getArrayLength()+1; timestep++) {
			if(timestep==0) {
				getCplex().addEq(stateOfCharge[timestep], getResourceParameters().get(indexOfResource).getInitalCapacity());

				if (getResourceParameters().get(indexOfResource).getDegradation()>0) {
					// set socmax to maximum storage cap.
					getCplex().addEq(maxStateOfCharge[timestep], getResourceParameters().get(indexOfResource).getMaximumStorageCapacity());
				}

			} else {
				//			------	degradation  ------
				if (getResourceParameters().get(indexOfResource).getDegradation()>0) {
					// if deg > 0, socmax(t) = socmax(t-1)-deg && soc(t) <= socmax(t)
					getCplex().addEq(maxStateOfCharge[timestep],
							getCplex().diff(
									maxStateOfCharge[timestep-1],
									getResourceParameters().get(indexOfResource).getDegradation()
									)
							);
					getCplex().addLe(stateOfCharge[timestep],
							maxStateOfCharge[timestep]
							);
				}

				//			------	energy balance ------
				getCplex().addEq(stateOfCharge[timestep], 
						getCplex().sum(
								stateOfCharge[timestep-1],
								getCplex().prod(getResourceParameters().get(indexOfResource).getUnitConversionFactorStorage(),
										getCplex().diff(
												getCplex().prod(powerInputStorage[timestep-1], getResourceParameters().get(indexOfResource).getEfficiencyInputStorage()*getTimeInterval()),
												getCplex().sum(
														getCplex().sum(
																getCplex().prod(powerOutputStorage[timestep-1], getResourceParameters().get(indexOfResource).getEfficiencyOutputReciprocal()*getTimeInterval()),
																getResourceParameters().get(indexOfResource).getStaticEnergyLoss()*getTimeInterval()
																), 
														getCplex().prod(
																getResourceParameters().get(indexOfResource).getDynamicEnergyLoss()*getTimeInterval(), 
																getCplex().diff(
																		getResourceParameters().get(indexOfResource).getReferenceDynamicEnergyLoss(),
																		getCplex().prod(0.5, 
																				getCplex().sum(														
																						stateOfCharge[timestep], 
																						stateOfCharge[timestep-1]
																						)
																				)
																		)
																)
														)
												)
										)
								)
						);

				if (getResourceParameters().get(indexOfResource).getNumberOfSystemStates()!=0) {
					for (int state = 0; state < getResourceParameters().get(indexOfResource).getNumberOfSystemStates(); state++) {
						if (getResourceParameters().get(indexOfResource).getSystemStates().get(state).isInputIsEqualToOutput()==true) {
							getCplex().add(
									getCplex().ifThen(									
											getCplex().eq(
													getDecisionVariableFromMatrix(nameOfResource, POWER, -1, STATE)[timestep][state], 
													1
													),
											getCplex().eq(
													getCplex().prod(
															powerInputStorage[timestep-1], 
															getResourceParameters().get(indexOfResource).getEfficiencyInputStorage()*getTimeInterval()
															),
													getCplex().sum(
															getCplex().sum(
																	getCplex().prod(
																			powerOutputStorage[timestep-1],
																			getResourceParameters().get(indexOfResource).getEfficiencyOutputReciprocal()*getTimeInterval()
																			),
																	getResourceParameters().get(indexOfResource).getStaticEnergyLoss()*getTimeInterval()
																	), 
															getCplex().prod(
																	getResourceParameters().get(indexOfResource).getDynamicEnergyLoss()*getTimeInterval(), 
																	getCplex().diff(
																			getResourceParameters().get(indexOfResource).getReferenceDynamicEnergyLoss(),
																			getCplex().prod(0.5, 
																					getCplex().sum(														
																							stateOfCharge[timestep], 
																							stateOfCharge[timestep-1]
																							)
																					)
																			)
																	)
															)
													)
											)
									);
						}
					}
				}
			}
		}
		if (getResourceParameters().get(indexOfResource).getCapacityTarget()!=-1) {
			if(getResourceParameters().get(indexOfResource).getCapacityTargetComparator()=="Eq") 	getCplex().addEq(stateOfCharge[getArrayLength()], getResourceParameters().get(indexOfResource).getCapacityTarget());
			if(getResourceParameters().get(indexOfResource).getCapacityTargetComparator()=="Ge") 	getCplex().addGe(stateOfCharge[getArrayLength()], getResourceParameters().get(indexOfResource).getCapacityTarget());
			if(getResourceParameters().get(indexOfResource).getCapacityTargetComparator()=="Le") 	getCplex().addLe(stateOfCharge[getArrayLength()], getResourceParameters().get(indexOfResource).getCapacityTarget());
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
	public static void generateCorrelativeDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// sum powerOutput[i] = sum PowerInput[i]
		if (powerOutputs.length >0 && powerInputs.length >0) {

			for (int timeStep = 0; timeStep < getArrayLength(); timeStep++) {
				IloNumExpr powerOutputSum = getCplex().numExpr();
				IloNumExpr powerInputSum = getCplex().numExpr();
				for (int outputI = 0; outputI < powerOutputs.length; outputI++) {
					powerOutputSum = getCplex().sum(powerOutputSum, powerOutputs[outputI][timeStep]);
					//					System.out.println("output" + timeStep + " " + outputI + " added");
				}
				for (int inputI = 0; inputI < powerInputs.length; inputI++) {
					powerInputSum = getCplex().sum(powerInputSum, powerInputs[inputI][timeStep]);			
					//					System.out.println("input" + timeStep + " " + inputI + " added");
				}
				getCplex().addEq(powerOutputSum, powerInputSum);
				//				System.out.println(timeStep + "corr dep added");
			}
		} else {
			System.err.println("Input or Output empty");
		}

	}

	/**
	 * Generate restrictive dependency.
	 * only one active member per side, all others == 0
	 *
	 * @param powerOutputs array of all relevant power output arrays (new IloNumVar [][] {})
	 * @param powerInputs array of all relevant power input arrays (new IloNumVar [][] {})
	 * @return the ilo int var[][][] of all binary decision variables created in method
	 *  [2 -> powerOutputs, powerInputs][getArrayLength()][number of Elements in powerOutputs/power Inputs, depends on index in first[]	]
	 * @throws IloException the ilo exception
	 */
	public static IloIntVar[][][] generateRestrictiveDependency (IloNumVar[][] powerOutputs, IloNumVar[][] powerInputs) throws IloException {
		// binary variables for dependency
		if (powerOutputs.length >0 && powerInputs.length >0) {

			IloIntVar[][] binaryVariablesOutputSide = new IloIntVar[getArrayLength()][powerOutputs.length];
			IloIntVar[][] binaryVariablesInputSide = new IloIntVar[getArrayLength()][powerInputs.length];

			// [2 -> powerOutputs, powerInputs][getArrayLength()][number of Elements in powerOutputs/power Inputs, depends on index in first[]	]
			IloIntVar[][][] variablesRestDep = new IloIntVar[][][] {binaryVariablesOutputSide, binaryVariablesInputSide};
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
					System.out.println("Input "+inputI + " added to restrictive dependency");

				}

				// sum time step = 1
				getCplex().addEq(binarySumInput, 1);
				getCplex().addEq(binarySumOutput, 1);
			}
			// return all binary decision variables created in method
			return variablesRestDep; 
		}
		System.err.println("at least one side of rest. dep empty");
		return null; 
	}

	/**
	 * Write results to file.
	 *
	 * @param contentToWrite the content to write
	 * @param fileName the file name
	 * @param header the header
	 * @param filePath the file path
	 */
	public static void writeResultsToFile (double[][] contentToWrite, String fileName, String header, String filePath) {
		String date = Double.toString(System.currentTimeMillis());
		try {
			//	double currentTime = System.currentTimeMillis(); 
			FileWriter myWriter = new FileWriter(filePath+fileName+date+".csv");
			myWriter.write("id;"+header);
			myWriter.write("\n");
			for (int i = 0; i < contentToWrite.length; i++) {
				myWriter.write(Double.toString(i).replace(".", ","));
				for(int j = 0; j < contentToWrite[0].length; j++) {
					myWriter.write(";"); // Use semicolon as separator
					//myWriter.write(Double.toString(contentToWrite[i][j]));
					myWriter.write(Double.toString(contentToWrite[i][j]).replace(".", ",")); // Replace decimal point with comma
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
	 * Sets the pla per input.
	 *
	 * @param interceptpla the interceptpla
	 * @param slopepla the slopepla
	 * @param lowerboundpla the lowerboundpla
	 * @param upperboundpla the upperboundpla
	 * @return the piecewise linear approximation
	 */
	public static PiecewiseLinearApproximation setPla (double interceptpla, double slopepla, double lowerboundpla, double upperboundpla) {
		PiecewiseLinearApproximation segY = new PiecewiseLinearApproximation(); 
		segY.setPla(interceptpla, slopepla, lowerboundpla, upperboundpla);
		return segY;
	}

	/**
	 * Sets the linear relationship per input.
	 *
	 * @param intercept the intercept
	 * @param slope the slope
	 * @return the piecewise linear approximation
	 */
	public static PiecewiseLinearApproximation setLinearRelationship (double intercept, double slope) {
		PiecewiseLinearApproximation segY = new PiecewiseLinearApproximation(); 
		segY.setLinearRelationship(intercept, slope);
		return segY;
	}

	/**
	 * Sets the efficiency per input.
	 *
	 * @param efficiency the efficiency
	 * @return the piecewise linear approximation
	 */
	public static PiecewiseLinearApproximation setEfficiency (double efficiency) {
		PiecewiseLinearApproximation segY = new PiecewiseLinearApproximation(); 
		segY.setEfficiency(efficiency);
		return segY;
	}
	/**
	 * Gets the electricity price by arrayLength.
	 *
	 * @return the electricity price
	 */
	public static double[] getElectricityPrice() {
		double[] electricityPrice = new double[] { // January 1, 2022, 15 min intervals
				85.87,37.21,32.62,32.27,51.03,63.76,44.84,39.2,58.28,43.46,42.47,47.48, 58.96,42.99,43.27,50.5,
				80.21,30.64,21.47,35.41,15.67,24.15,43.98,55.36,23.58,42.56,60.2,87.49,22.46,38.95,	31.68,60.19,
				17.49,37.23,40.51,57.98,46.62,46.74,61.23,71.33,57.2,55.6,59.39,81.35,48.79,58.3,58.01,57.73,71.85,
				64.5,57.07,57.84,44.25,38.6,65.02,67.76,42.31,62.06,83.36,100.31,57.25,66.51,84.52,95.44,73.57,84.5,
				85.41,98.72,75.05,98.16,101.62,96.88,127.03,92.24,102.1,79.11,140.98,93.1,91.67,66.07,125.87,89.96,
				76.41,48.12,100.5,74.96,74.03,64.77,100.75,75.26,87.21,38.04,85.35,60.38,56.9,35.95
		};
		//		double[] electricityPrice = new double[] { // August 26, 2023, 00:00 - August 27, 2023, 00:00, 15 min intervals
		//		115.88,104.66,99.99,95.36,109,107.89,94.54,87.74,106.97,102.6,101.23,106.16,91.08,89.26,94.37,107.41,
		//		97.82,90.27,73.52,62.21,95.23,85.87,85.79,77.51,127.21,110.5,93.04,78.65,123.35,102.21,75.56,53.01,
		//		104.54,79.9,59.67,19.16,60.36,44.72,26.03,-7.05,36.85,24.22,-3.42,-16.45,20.59,4.73,13.12,36.61,
		//		13.92,60.28,105.42,112.23,30.37,77.14,97.85,114.95,56.74,83.3,86.28,103.04,62.73,88,108.72,125.41,
		//		83.86,84.63,85.69,111.87,94.58,103.35,117.73,139.37,129.27,121.79,116.69,113.81,150.2,117.93,110.31,
		//		99.2,127.92,116.31,110.86,97.64,117.74,98.9,92.52,83.89,101.77,104.11,95.6,92.89,98.93,101.33,92.34,91,102.06
		//		};
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
	 * Gets the decision variables vector.
	 *
	 * @return the decisionVariables
	 */
	public static HashMap<String, IloNumVar[]> getDecisionVariablesVector() {
		return decisionVariablesVector;
	}

	/**
	 * Sets the decision variables vector.
	 *
	 * @param decisionVariables the decisionVariables to set
	 */
	public static void setDecisionVariablesVector(HashMap<String, IloNumVar[]> decisionVariables) {
		DesignPatterns.decisionVariablesVector = decisionVariables;
	}

	/**
	 * Gets the decision variables matrix.
	 *
	 * @return the decisionVariablesMatrix
	 */
	public static HashMap<String, IloNumVar[][]> getDecisionVariablesMatrix() {
		return decisionVariablesMatrix;
	}

	/**
	 * Sets the decision variables matrix.
	 *
	 * @param decisionVariablesMatrix the decisionVariablesMatrix to set
	 */
	public static void setDecisionVariablesMatrix(HashMap<String, IloNumVar[][]> decisionVariablesMatrix) {
		DesignPatterns.decisionVariablesMatrix = decisionVariablesMatrix;
	}

	/**
	 * Gets the resource parameters.
	 *
	 * @return the getResourceParameters()
	 */
	public static List<ResourceParameters> getResourceParameters() {
		return resourceParameters;
	}

	/**
	 * Sets the resource parameters.
	 *
	 * @param resourceParameters the resourceParameters to set
	 */
	public static void setResourceParameters(List<ResourceParameters> resourceParameters) {
		DesignPatterns.resourceParameters = resourceParameters;
	}
}