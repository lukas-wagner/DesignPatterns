package designpatterns;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourceParameters for Design Patterns.
 */
public class ResourceParameters {
	String name; 
	String relevantEnergyCarrier; 
	/** The min/max power input. */
	double minPowerInput; 
	double maxPowerInput = Double.MAX_VALUE; 

	/** The min/max power output. */
	double minPowerOutput;
	double maxPowerOutput = Double.MAX_VALUE;

	/** The min/max ramp. */
	double minRamp; 
	double maxRamp;

	List<SystemState> systemStates = new ArrayList<SystemState>();
	int numberOfSystemStates;

	/** Efficiencies/IO Relation */
	double efficiency; 
	double slope, intercept; 

	List<PiecewiseLinearApproximation> pla = new ArrayList<PiecewiseLinearApproximation>();
	int numberOfLinearSegments; 


	/** Storage Specific parameters */
	boolean isStorage = false; 
	double maximumStorageCapacity;
	double minimumStorageCapacity; 
	double initalCapacity; 
	double staticPowerLoss;
	double efficiencyInputStorage = 1; 
	double efficiencyOutputStorage = 1; 
	double efficiencyOutputReciprocal = 1/efficiencyOutputStorage;

	/**
	 * Creates the pla list from arguments.
	 *
	 * @param interceptpla the interceptpla
	 * @param slopepla the slopepla
	 * @param lowerboundpla the lowerboundpla
	 * @param upperboundpla the upperboundpla
	 */
	public void createPlaList (double interceptpla, double slopepla, double lowerboundpla, double upperboundpla) {
		PiecewiseLinearApproximation plaItem = new PiecewiseLinearApproximation();
		plaItem.setIntercept(interceptpla);
		plaItem.setSlope(slopepla);
		plaItem.setUpperBound(upperboundpla);
		plaItem.setLowerBound(lowerboundpla);
		this.pla.add(plaItem);
	}

	
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
		return minRamp;
	}

	/**
	 * Sets the min ramp.
	 *
	 * @param minRamp the minRamp to set
	 */
	public void setMinRamp(double minRamp) {
		this.minRamp = minRamp;
	}

	/**
	 * Gets the max ramp.
	 *
	 * @return the maxRamp
	 */
	public double getMaxRamp() {
		return maxRamp;
	}

	/**
	 * Sets the max ramp.
	 *
	 * @param maxRamp the maxRamp to set
	 */
	public void setMaxRamp(double maxRamp) {
		this.maxRamp = maxRamp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the relevantEnergyCarrier
	 */
	public String getRelevantEnergyCarrier() {
		return relevantEnergyCarrier;
	}

	/**
	 * @param relevantEnergyCarrier the relevantEnergyCarrier to set
	 */
	public void setRelevantEnergyCarrier(String relevantEnergyCarrier) {
		this.relevantEnergyCarrier = relevantEnergyCarrier;
	}

	/**
	 * @return the efficiency
	 */
	public double getEfficiency() {
		return efficiency;
	}

	/**
	 * @param efficiency the efficiency to set
	 */
	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}

	/**
	 * @return the slope
	 */
	public double getSlope() {
		return slope;
	}

	/**
	 * @param slope the slope to set
	 */
	public void setSlope(double slope) {
		this.slope = slope;
	}

	/**
	 * @return the intercept
	 */
	public double getIntercept() {
		return intercept;
	}

	/**
	 * @param intercept the intercept to set
	 */
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}




	/**
	 * @return the numberOfLinearSegments
	 */
	public int getNumberOfLinearSegments() {
		return numberOfLinearSegments;
	}

	/**
	 * @param numberOfLinearSegments the numberOfLinearSegments to set
	 */
	public void setNumberOfLinearSegments(int numberOfLinearSegments) {
		this.numberOfLinearSegments = numberOfLinearSegments;
	}

	/**
	 * @return the pla
	 */
	public List<PiecewiseLinearApproximation> getPla() {
		return pla;
	}

	/**
	 * @param pla the pla to set
	 */
	public void setPla(List<PiecewiseLinearApproximation> pla) {
		this.pla = pla;
	}


	/**
	 * @return the isStorage
	 */
	public boolean isStorage() {
		return isStorage;
	}


	/**
	 * @param isStorage the isStorage to set
	 */
	public void setResourceAsStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}


	/**
	 * @return the maximumStorageCapacity
	 */
	public double getMaximumStorageCapacity() {
		return maximumStorageCapacity;
	}


	/**
	 * @param maximumStorageCapacity the maximumStorageCapacity to set
	 */
	public void setMaximumStorageCapacity(double maximumStorageCapacity) {
		this.maximumStorageCapacity = maximumStorageCapacity;
	}


	/**
	 * @return the minimumStorageCapacity
	 */
	public double getMinimumStorageCapacity() {
		return minimumStorageCapacity;
	}


	/**
	 * @param minimumStorageCapacity the minimumStorageCapacity to set
	 */
	public void setMinimumStorageCapacity(double minimumStorageCapacity) {
		this.minimumStorageCapacity = minimumStorageCapacity;
	}


	/**
	 * @return the initalCapacity
	 */
	public double getInitalCapacity() {
		return initalCapacity;
	}


	/**
	 * @param initalCapacity the initalCapacity to set
	 */
	public void setInitalCapacity(double initalCapacity) {
		this.initalCapacity = initalCapacity;
	}


	/**
	 * @return the staticPowerLoss
	 */
	public double getStaticPowerLoss() {
		return staticPowerLoss;
	}


	/**
	 * @param staticPowerLoss the staticPowerLoss to set
	 */
	public void setStaticPowerLoss(double staticPowerLoss) {
		this.staticPowerLoss = staticPowerLoss;
	}

	/**
	 * @return the minPowerOutput
	 */
	public double getMinPowerOutput() {
		return minPowerOutput;
	}

	/**
	 * @param minPowerOutput the minPowerOutput to set
	 */
	public void setMinPowerOutput(double minPowerOutput) {
		this.minPowerOutput = minPowerOutput;
	}

	/**
	 * @return the maxPowerOutput
	 */
	public double getMaxPowerOutput() {
		return maxPowerOutput;
	}

	/**
	 * @param maxPowerOutput the maxPowerOutput to set
	 */
	public void setMaxPowerOutput(double maxPowerOutput) {
		this.maxPowerOutput = maxPowerOutput;
	}

	/**
	 * @return the efficiencyInputStorage
	 */
	public double getEfficiencyInputStorage() {
		return efficiencyInputStorage;
	}

	/**
	 * @param efficiencyInputStorage the efficiencyInputStorage to set
	 */
	public void setEfficiencyInputStorage(double efficiencyInputStorage) {
		this.efficiencyInputStorage = efficiencyInputStorage;
	}

	/**
	 * @return the efficiencyOutputStorage
	 */
	public double getEfficiencyOutputStorage() {
		return efficiencyOutputStorage;
	}

	/**
	 * @param efficiencyOutputStorage the efficiencyOutputStorage to set
	 */
	public void setEfficiencyOutputStorage(double efficiencyOutputStorage) {
		this.efficiencyOutputStorage = efficiencyOutputStorage;
	}

	/**
	 * @return the efficiencyOutputReciprocal
	 */
	public double getEfficiencyOutputReciprocal() {
		return efficiencyOutputReciprocal;
	}

	/**
	 * @param efficiencyOutputReciprocal the efficiencyOutputReciprocal to set
	 */
	public void setEfficiencyOutputReciprocal(double efficiencyOutputReciprocal) {
		this.efficiencyOutputReciprocal = efficiencyOutputReciprocal;
	}


	/**
	 * @return the systemStates
	 */
	public List<SystemState> getSystemStates() {
		return systemStates;
	}


	/**
	 * @param systemStates the systemStates to set
	 */
	public void setSystemStates(List<SystemState> systemStates) {
		this.systemStates = systemStates;
	}


	/**
	 * @return the numberOfSystemStates
	 */
	public int getNumberOfSystemStates() {
		return numberOfSystemStates;
	}


	/**
	 * @param numberOfSystemStates the numberOfSystemStates to set
	 */
	public void setNumberOfSystemStates(int numberOfSystemStates) {
		this.numberOfSystemStates = numberOfSystemStates;
	} 

}
