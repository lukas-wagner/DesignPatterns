package designpatterns;

public class SystemState {
	
	int stateID; 
	String stateName; 
	double maxStateDuration; 
	double minStateDuration; 
	int[] followerStates;
	double minPower; 
	double maxPower; 
	double maxPowerOutput = Double.MAX_VALUE;
	
	public SystemState(int a, double b, double c, int[] d) {
		stateID = a;
		maxStateDuration = b; 
		minStateDuration = c; 
		followerStates = d; 
	}
	
	public SystemState() {
		// TODO Auto-generated constructor stub
	}

	public int getStateID() {
		return stateID;
	}
	public void setStateID(int stateID) {
		this.stateID = stateID;
	}
	public double getMaxStateDuration() {
		return maxStateDuration;
	}
	public void setMaxStateDuration(double maxStateDuration) {
		this.maxStateDuration = maxStateDuration;
	}
	public double getMinStateDuration() {
		return minStateDuration;
	}
	public void setMinStateDuration(double minStateDuration) {
		this.minStateDuration = minStateDuration;
	}
	public int[] getFollowerStates() {
		return followerStates;
	}
	public void setFollowerStates(int[] followerStates) {
		this.followerStates = followerStates;
	}

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * @return the minPower
	 */
	public double getMinPower() {
		return minPower;
	}

	/**
	 * @param minPower the minPower to set
	 */
	public void setMinPower(double minPower) {
		this.minPower = minPower;
	}

	/**
	 * @return the maxPower
	 */
	public double getMaxPower() {
		return maxPower;
	}

	/**
	 * @param maxPower the maxPower to set
	 */
	public void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
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
}
