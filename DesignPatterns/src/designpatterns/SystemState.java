package designpatterns;

public class SystemState {
	
	int stateID; 
	double maxStateDuration; 
	double minStateDuration; 
	int[] followerStates;
	
	public SystemState(int a, double b, double c, int[] d) {
		stateID = a;
		maxStateDuration = b; 
		minStateDuration = c; 
		followerStates = d; 
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
}
