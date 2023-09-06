package designpatterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * ResourceParameters for Design Patterns.
 */
public class ResourceParameters {

	/** The name. */
	String name; 

	/** The number of inputs. */
	int numberOfInputs = 1; 

	/** The energy carrier input. */
	String energyCarrierInput; 

	/** The energy carrier inputs. */
	List<String> energyCarrierInputs = new ArrayList<String>();
	
	/** The energy carrier output. */
	String energyCarrierOutput; 

	/** The relevant energy carrier. */
	String relevantEnergyCarrier; 

	/** The min/max power input. */
	double minPowerInput; 

	/** The list of min power input. */
	List<Double> minPowerInputs = new ArrayList<Double>();
	
	/** The max power input. */
	double maxPowerInput = Double.MAX_VALUE; 

	/** The list of max power input. */
	List<Double> maxPowerInputs = new ArrayList<Double>(); 
	
	/** The min/max power output. */
	double minPowerOutput;

	/** The max power output. */
	double maxPowerOutput = Double.MAX_VALUE;

	/** The min/max ramp. */
	double minRampInput; 

	/** The max ramp. */
	double maxRampInput = Double.MAX_VALUE;
	
	/** The min ramp output. */
	double minRampOutput; 
	
	/** The max ramp output. */
	double maxRampOutput = Double.MAX_VALUE;

	/** The latency of output. */
	int latencyOfOutput;

	/** The system states. */
	List<SystemState> systemStates = new ArrayList<SystemState>();

	/** The number of system states. */
	int numberOfSystemStates;

	/** The initial system state. */
	int initialSystemState = 0; 
	
	/** The is secondary resource. */
	boolean isSecondaryResource = false; 
	
	/** The primary resource. */
	String primaryResource = ""; 

	/**  Efficiencies/IO Relation. */
	double efficiency; 
	
	/** The intercept. */
	double slope, intercept; 
	
	/** The pla list for all inputs. */
	List<List<PiecewiseLinearApproximation>> plaList = new ArrayList<List<PiecewiseLinearApproximation>>();
	
	/**  Storage Specific parameters. */
	boolean isStorage = false; 

	/** The maximum storage capacity. */
	double maximumStorageCapacity;

	/** The minimum storage capacity. */
	double minimumStorageCapacity; 

	/** The unit conversion factor storage. */
	double unitConversionFactorStorage = 1; 

	/** The capacity set points. */
	HashMap<Integer, Double> capacitySetPoints = new HashMap<Integer, Double>();

	/** The static power loss. */
	double staticEnergyLoss;

	/** The dynamic energy loss. */
	double dynamicEnergyLoss;

	/** The reference value for dynamic energy loss. */
	double referenceDynamicEnergyLoss; 

	/** The efficiency input storage. */
	double efficiencyInputStorage = 1; 

	/** The efficiency output storage. */
	double efficiencyOutputStorage = 1; 

	/** The efficiency output reciprocal. */ 
	double efficiencyOutputReciprocal = 1/efficiencyOutputStorage;

	/** The degradation. */
	double degradation;
	
	/**
	 * Creates the pla list from arguments.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 */
//	public void createPlaList (double interceptpla, double slopepla, double lowerboundpla, double upperboundpla) {
//		PiecewiseLinearApproximation plaItem = new PiecewiseLinearApproximation();
//		plaItem.setIntercept(interceptpla);
//		plaItem.setSlope(slopepla);
//		plaItem.setUpperBound(upperboundpla);
//		plaItem.setLowerBound(lowerboundpla);
//		this.pla.add(plaItem);
//	}


	/**
	 * Adds the system state.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 */
	public void addSystemState (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with ramp.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param minRampState the min ramp state
	 * @param maxRampState the max ramp state
	 */
	public void addSystemStateWithRamp (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double minRampState, double maxRampState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMinRampInput(minRampState);
		state.setMaxRampInput(maxRampState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 */
	public void addSystemStateWithMaxPowerOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output and ramp input.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateInput the min ramp state input
	 * @param maxRampStateInput the max ramp state input
	 */
	public void addSystemStateWithMaxPowerOutputAndRampInput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateInput, double maxRampStateInput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampInput(minRampStateInput);
		state.setMaxRampInput(maxRampStateInput);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with latency.
	 * sets values of minHoldingDuration and maxHoldingDuration to double latencyState
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param latencyState the latency state
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 */
	public void addSystemStateWithLatency (int stateID, String stateName, double latencyState,  int[] idsOfFollowerStates, double minPowerState, double maxPowerState) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(latencyState);
		state.setMaxStateDuration(latencyState);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		this.systemStates.add(state);
	}


	/**
	 * Adds the system state with max power output and ramp output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateOutput the min ramp state output
	 * @param maxRampStateOutput the max ramp state output
	 */
	public void addSystemStateWithMaxPowerOutputAndRampOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateOutput, double maxRampStateOutput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampOutput(minRampStateOutput);
		state.setMaxRampOutput(maxRampStateOutput);
		this.systemStates.add(state);
	}

	/**
	 * Adds the system state with max power output and ramp input output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param maxPowerOutputState the max power output state
	 * @param minRampStateInput the min ramp state input
	 * @param maxRampStateInput the max ramp state input
	 * @param minRampStateOutput the min ramp state output
	 * @param maxRampStateOutput the max ramp state output
	 */
	public void addSystemStateWithMaxPowerOutputAndRampInputOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, double maxPowerOutputState, double minRampStateInput, double maxRampStateInput, double minRampStateOutput, double maxRampStateOutput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setMaxPowerOutput(maxPowerOutputState);
		state.setMinRampInput(minRampStateInput);
		state.setMaxRampInput(maxRampStateInput);
		state.setMinRampOutput(minRampStateOutput);
		state.setMaxRampOutput(maxRampStateOutput);
		this.systemStates.add(state);
	}
	
	/**
	 * Adds the system state for storage, input is equal to sum output.
	 *
	 * @param stateID the state ID
	 * @param stateName the state name
	 * @param minHoldingDuration the min holding duration
	 * @param maxHoldingDuration the max holding duration
	 * @param idsOfFollowerStates the ids of follower states
	 * @param minPowerState the min power state
	 * @param maxPowerState the max power state
	 * @param inputIsEqualToOutput the input is equal to output
	 */
	public void addSystemStateStorageEqualInputOutput (int stateID, String stateName, double minHoldingDuration, double maxHoldingDuration, int[] idsOfFollowerStates, double minPowerState, double maxPowerState, boolean inputIsEqualToOutput) {
		SystemState state = new SystemState();
		state.setStateID(stateID);
		state.setStateName(stateName);
		state.setMinStateDuration(minHoldingDuration);
		state.setMaxStateDuration(maxHoldingDuration);
		state.setFollowerStates(idsOfFollowerStates);
		state.setMinPower(minPowerState);
		state.setMaxPower(maxPowerState);
		state.setInputIsEqualToOutput(inputIsEqualToOutput);
		this.systemStates.add(state);
	}
	
	/**
	 * Gets the min power.
	 *
	 * @return the minPower
	 */
	public double getMinPowerInput() {
		return minPowerInput;
	}

	/**
	 * Sets the min power.
	 *
	 * @param minPower the minPower to set
	 */
	public void setMinPowerInput(double minPower) {
		this.minPowerInput = minPower;
	}

	/**
	 * Gets the max power.
	 *
	 * @return the maxPower
	 */
	public double getMaxPowerInput() {
		return maxPowerInput;
	}

	/**
	 * Sets the max power.
	 *
	 * @param maxPower the maxPower to set
	 */
	public void setMaxPowerInput(double maxPower) {
		this.maxPowerInput = maxPower;
	}

	/**
	 * Gets the min ramp.
	 *
	 * @return the minRamp
	 */
	public double getMinRamp() {
		return minRampInput;
	}

	/**
	 * Sets the min ramp.
	 *
	 * @param minRamp the minRamp to set
	 */
	public void setMinRamp(double minRamp) {
		this.minRampInput = minRamp;
	}

	/**
	 * Gets the max ramp input.
	 *
	 * @return the maxRamp
	 */
	public double getMaxRampInput() {
		return maxRampInput;
	}

	/**
	 * Sets the max ramp input.
	 *
	 * @param maxRampInput the maxRamp to set
	 */
	public void setMaxRampInput(double maxRampInput) {
		this.maxRampInput = maxRampInput;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the relevant energy carrier.
	 *
	 * @return the relevantEnergyCarrier
	 */
	public String getRelevantEnergyCarrier() {
		return relevantEnergyCarrier;
	}

	/**
	 * Sets the relevant energy carrier.
	 *
	 * @param relevantEnergyCarrier the relevantEnergyCarrier to set
	 */
	public void setRelevantEnergyCarrier(String relevantEnergyCarrier) {
		this.relevantEnergyCarrier = relevantEnergyCarrier;
	}

	/**
	 * Gets the efficiency.
	 *
	 * @return the efficiency
	 */
	public double getEfficiency() {
		return efficiency;
	}

	/**
	 * Sets the efficiency.
	 *
	 * @param efficiency the efficiency to set
	 */
	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

	/**
	 * Gets the slope.
	 *
	 * @return the slope
	 */
	public double getSlope() {
		return slope;
	}

	/**
	 * Sets the slope.
	 *
	 * @param slope the slope to set
	 */
	public void setSlope(double slope) {
		this.slope = slope;
	}

	/**
	 * Gets the intercept.
	 *
	 * @return the intercept
	 */
	public double getIntercept() {
		return intercept;
	}

	/**
	 * Sets the intercept.
	 *
	 * @param intercept the intercept to set
	 */
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}



	/**
	 * Checks if is storage.
	 *
	 * @return the isStorage
	 */
	public boolean isStorage() {
		return isStorage;
	}


	/**
	 * Sets the resource as storage.
	 *
	 * @param isStorage the isStorage to set
	 */
	public void setResourceAsStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}


	/**
	 * Gets the maximum storage capacity.
	 *
	 * @return the maximumStorageCapacity
	 */
	public double getMaximumStorageCapacity() {
		return maximumStorageCapacity;
	}


	/**
	 * Sets the maximum storage capacity.
	 *
	 * @param maximumStorageCapacity the maximumStorageCapacity to set
	 */
	public void setMaximumStorageCapacity(double maximumStorageCapacity) {
		this.maximumStorageCapacity = maximumStorageCapacity;
	}


	/**
	 * Gets the minimum storage capacity.
	 *
	 * @return the minimumStorageCapacity
	 */
	public double getMinimumStorageCapacity() {
		return minimumStorageCapacity;
	}


	/**
	 * Sets the minimum storage capacity.
	 *
	 * @param minimumStorageCapacity the minimumStorageCapacity to set
	 */
	public void setMinimumStorageCapacity(double minimumStorageCapacity) {
		this.minimumStorageCapacity = minimumStorageCapacity;
	}




	/**
	 * Gets the static energy loss (energy loss independent of power flows or storage level).
	 *
	 * @return the staticEnergyLoss
	 */
	public double getStaticEnergyLoss() {
		return staticEnergyLoss;
	}


	/**
	 * Sets the static energy loss (energy loss independent of power flows or storage level).
	 *
	 * @param staticEnergyLoss the staticPowerLoss to set
	 */
	public void setStaticEnergyLoss(double staticEnergyLoss) {
		this.staticEnergyLoss = staticEnergyLoss;
	}

	/**
	 * Gets the min power output.
	 *
	 * @return the minPowerOutput
	 */
	public double getMinPowerOutput() {
		return minPowerOutput;
	}

	/**
	 * Sets the min power output.
	 *
	 * @param minPowerOutput the minPowerOutput to set
	 */
	public void setMinPowerOutput(double minPowerOutput) {
		this.minPowerOutput = minPowerOutput;
	}

	/**
	 * Gets the max power output.
	 *
	 * @return the maxPowerOutput
	 */
	public double getMaxPowerOutput() {
		return maxPowerOutput;
	}

	/**
	 * Sets the max power output.
	 *
	 * @param maxPowerOutput the maxPowerOutput to set
	 */
	public void setMaxPowerOutput(double maxPowerOutput) {
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * Gets the efficiency input storage.
	 *
	 * @return the efficiencyInputStorage
	 */
	public double getEfficiencyInputStorage() {
		return efficiencyInputStorage;
	}

	/**
	 * Sets the efficiency input storage.
	 *
	 * @param efficiencyInputStorage the efficiencyInputStorage to set
	 */
	public void setEfficiencyInputStorage(double efficiencyInputStorage) {
		this.efficiencyInputStorage = efficiencyInputStorage;
	}

	/**
	 * Gets the efficiency output storage.
	 *
	 * @return the efficiencyOutputStorage
	 */
	public double getEfficiencyOutputStorage() {
		return efficiencyOutputStorage;
	}

	/**
	 * Sets the efficiency output storage.
	 *
	 * @param efficiencyOutputStorage the efficiencyOutputStorage to set
	 */
	public void setEfficiencyOutputStorage(double efficiencyOutputStorage) {
		this.efficiencyOutputStorage = efficiencyOutputStorage;
	}

	/**
	 * Gets the efficiency output reciprocal.
	 *
	 * @return the efficiencyOutputReciprocal
	 */
	public double getEfficiencyOutputReciprocal() {
		return efficiencyOutputReciprocal;
	}

	/**
	 * Sets the efficiency output reciprocal.
	 *
	 * @param efficiencyOutputReciprocal the efficiencyOutputReciprocal to set
	 */
	public void setEfficiencyOutputReciprocal(double efficiencyOutputReciprocal) {
		this.efficiencyOutputReciprocal = efficiencyOutputReciprocal;
	}


	/**
	 * Gets the system states.
	 *
	 * @return the systemStates
	 */
	public List<SystemState> getSystemStates() {
		return systemStates;
	}


	/**
	 * Sets the system states.
	 *
	 * @param systemStates the systemStates to set
	 */
	public void setSystemStates(List<SystemState> systemStates) {
		this.systemStates = systemStates;
	}


	/**
	 * Gets the number of system states.
	 *
	 * @return the numberOfSystemStates
	 */
	public int getNumberOfSystemStates() {
		return numberOfSystemStates;
	}


	/**
	 * Sets the number of system states.
	 *
	 * @param numberOfSystemStates the numberOfSystemStates to set
	 */
	public void setNumberOfSystemStates(int numberOfSystemStates) {
		this.numberOfSystemStates = numberOfSystemStates;
	}


	/**
	 * Gets the latency of output.
	 *
	 * @return the latencyOfOutput
	 */
	public int getLatencyOfOutput() {
		return latencyOfOutput;
	}


	/**
	 * Sets the latency of output.
	 *
	 * @param latencyOfOutput the latencyOfOutput to set
	 */
	public void setLatencyOfOutput(int latencyOfOutput) {
		this.latencyOfOutput = latencyOfOutput;
	}


	/**
	 * Gets the energy carrier input.
	 *
	 * @return the energyCarrierInput
	 */
	public String getEnergyCarrierInput() {
		return energyCarrierInput;
	}

	/**
	 * Sets the energy carrier of both sides.
	 *
	 * @param energyCarrier the new energy carrier
	 */
	public void setEnergyCarrier(String energyCarrier) {
		this.energyCarrierInput = energyCarrier;
		this.energyCarrierOutput = energyCarrier;
	} 

	/**
	 * Sets the energy carrier input.
	 *
	 * @param energyCarrierInput the energyCarrierInput to set
	 */
	public void setEnergyCarrierInput(String energyCarrierInput) {
		//		this.energyCarrierInput = energyCarrierInput;
	}


	/**
	 * Gets the energy carrier output.
	 *
	 * @return the energyCarrierOutput
	 */
	public String getEnergyCarrierOutput() {
		return energyCarrierOutput;
	}


	/**
	 * Sets the energy carrier output.
	 *
	 * @param energyCarrierOutput the energyCarrierOutput to set
	 */
	public void setEnergyCarrierOutput(String energyCarrierOutput) {
		this.energyCarrierOutput = energyCarrierOutput;
	}


	/**
	 * Gets the dynamic energy loss (energy loss dependent of power flows or storage level).
	 *
	 * @return the dynamicEnergyLoss
	 */
	public double getDynamicEnergyLoss() {
		return dynamicEnergyLoss;
	}


	/**
	 * Sets the dynamic energy loss (energy loss dependent of power flows or storage level)
	 * loss = dynamicLoss * (maxSOC - SOC[i]).
	 *
	 * @param dynamicEnergyLoss the dynamicEnergyLoss to set
	 */
	public void setDynamicEnergyLoss(double dynamicEnergyLoss) {
		this.dynamicEnergyLoss = dynamicEnergyLoss;
	}


	/**
	 * Gets the reference dynamic energy loss.
	 *
	 * @return the referenceDynamicEnergyLoss
	 */
	public double getReferenceDynamicEnergyLoss() {
		return referenceDynamicEnergyLoss;
	}


	/**
	 * Sets the reference dynamic energy loss.
	 *
	 * @param referenceDynamicEnergyLoss the referenceDynamicEnergyLoss to set
	 */
	public void setReferenceDynamicEnergyLoss(double referenceDynamicEnergyLoss) {
		this.referenceDynamicEnergyLoss = referenceDynamicEnergyLoss;
	}


	/**
	 * Gets the initial system state.
	 *
	 * @return the initialSystemState
	 */
	public int getInitialSystemState() {
		return initialSystemState;
	}


	/**
	 * Sets the initial system state.
	 *
	 * @param initialSystemState the initialSystemState to set
	 */
	public void setInitialSystemState(int initialSystemState) {
		this.initialSystemState = initialSystemState;
	}


	/**
	 * Gets the unit conversion factor storage.
	 *
	 * @return the unitConversionFactorStorage
	 */
	public double getUnitConversionFactorStorage() {
		return unitConversionFactorStorage;
	}


	/**
	 * Sets the unit conversion factor storage f -> SOC[i] = SOC[i-1] + f(P_in*eta*t-Pout/eta*t-Ploss).
	 *
	 * @param unitConversionFactorStorage the unitConversionFactorStorage to set
	 */
	public void setUnitConversionFactorStorage(double unitConversionFactorStorage) {
		this.unitConversionFactorStorage = unitConversionFactorStorage;
	}


	/**
	 * Gets the number of inputs.
	 *
	 * @return the numberOfInputs
	 */
	public int getNumberOfInputs() {
		return numberOfInputs;
	}


	/**
	 * Sets the number of inputs.
	 *
	 * @param numberOfInputs the numberOfInputs to set
	 */
	public void setNumberOfInputs(int numberOfInputs) {
		this.numberOfInputs = numberOfInputs;
	}


	/**
	 * Gets the pla list for all inputs.
	 *
	 * @return the plaList
	 */
	public List<List<PiecewiseLinearApproximation>> getPlaList() {
		return plaList;
	}


	/**
	 * Sets the pla list (list of pla for all inputs).
	 *
	 * @param plaList the plaList to set
	 */
	public void setPlaList(ArrayList<List<PiecewiseLinearApproximation>> plaList) {
		this.plaList = plaList;
	}

	/**
	 * Gets the energy carrier inputs.
	 *
	 * @return the energyCarrierInputs
	 */
	public List<String> getEnergyCarrierInputs() {
		return energyCarrierInputs;
	}

	/**
	 * Sets the energy carrier inputs.
	 *
	 * @param energyCarrierInputs the energyCarrierInputs to set
	 */
	public void setEnergyCarrierInputs(List<String> energyCarrierInputs) {
		this.energyCarrierInputs = energyCarrierInputs;
	}

	/**
	 * Gets the min power inputs.
	 *
	 * @return the minPowerInputs
	 */
	public List<Double> getMinPowerInputs() {
		return minPowerInputs;
	}

	/**
	 * Sets the min power inputs.
	 *
	 * @param minPowerInputs the minPowerInputs to set
	 */
	public void setMinPowerInputs(List<Double> minPowerInputs) {
		this.minPowerInputs = minPowerInputs;
	}

	/**
	 * Gets the max power inputs.
	 *
	 * @return the maxPowerInputs
	 */
	public List<Double> getMaxPowerInputs() {
		return maxPowerInputs;
	}

	/**
	 * Sets the max power inputs.
	 *
	 * @param maxPowerInputs the maxPowerInputs to set
	 */
	public void setMaxPowerInputs(List<Double> maxPowerInputs) {
		this.maxPowerInputs = maxPowerInputs;
	}

	/**
	 * Gets the degradation.
	 *
	 * @return the degradation
	 */
	public double getDegradation() {
		return degradation;
	}

	/**
	 * Sets the degradation.
	 *
	 * @param degradation the degradation to set
	 */
	public void setDegradation(double degradation) {
		this.degradation = degradation;
	}

	/**
	 * Gets the min ramp input.
	 *
	 * @return the minRampInput
	 */
	public double getMinRampInput() {
		return minRampInput;
	}

	/**
	 * Sets the min ramp input.
	 *
	 * @param minRampInput the minRampInput to set
	 */
	public void setMinRampInput(double minRampInput) {
		this.minRampInput = minRampInput;
	}

	/**
	 * Gets the min ramp output.
	 *
	 * @return the minRampOutput
	 */
	public double getMinRampOutput() {
		return minRampOutput;
	}

	/**
	 * Sets the min ramp output.
	 *
	 * @param minRampOutput the minRampOutput to set
	 */
	public void setMinRampOutput(double minRampOutput) {
		this.minRampOutput = minRampOutput;
	}

	/**
	 * Gets the max ramp output.
	 *
	 * @return the maxRampOutput
	 */
	public double getMaxRampOutput() {
		return maxRampOutput;
	}

	/**
	 * Sets the max ramp output.
	 *
	 * @param maxRampOutput the maxRampOutput to set
	 */
	public void setMaxRampOutput(double maxRampOutput) {
		this.maxRampOutput = maxRampOutput;
	}

	/**
	 * Checks if is secondary resource.
	 * default = false
	 *
	 * @return the isSecondaryResource
	 */
	public boolean isSecondaryResource() {
		return isSecondaryResource;
	}

	/**
	 * Sets the secondary resource.
	 *
	 * @param isSecondaryResource the isSecondaryResource to set
	 */
	public void setSecondaryResource(boolean isSecondaryResource) {
		this.isSecondaryResource = isSecondaryResource;
	}

	/**
	 * Gets the primary resource.
	 *
	 * @return the primaryResource
	 */
	public String getPrimaryResource() {
		return primaryResource;
	}

	/**
	 * Sets the name primary resource.
	 *
	 * @param primaryResource the primaryResource to set
	 */
	public void setPrimaryResource(String primaryResource) {
		this.primaryResource = primaryResource;
	}

	/**
	 * Gets the capacity set points.
	 * Key: timestep
	 * Value: setpoint
	 *
	 * @return the capacitySetPoints
	 */
	public HashMap<Integer, Double> getCapacitySetPoints() {
		return capacitySetPoints;
	}

	/**
	 * Sets the capacity set points.
	 *
	 * @param capacitySetPoints the capacitySetPoints to set
	 */
	public void setCapacitySetPoints(HashMap<Integer, Double> capacitySetPoints) {
		this.capacitySetPoints = capacitySetPoints;
	}
}
