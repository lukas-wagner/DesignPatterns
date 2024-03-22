package designpatterns;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class OptimizationResults.
 */
public class OptimizationResults {

	/** The time stamp. */
	int timeStamp; 

	/** The variable names. */
	String variableName;
	
	/** The optimization results. */
	List<Double> optimizationResults = new ArrayList<Double>();

	/**
	 * @return the timeStamp
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}


	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @param variableName the variableName to set
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * @return the optimizationResults
	 */
	public List<Double> getOptimizationResults() {
		return optimizationResults;
	}

	/**
	 * @param optimizationResults the optimizationResults to set
	 */
	public void setOptimizationResults(List<Double> optimizationResults) {
		this.optimizationResults = optimizationResults;
	}
	 public void addOptimizationResultAt(int index, Double result) {
	        // Ensure the list is large enough.
	        while (optimizationResults.size() <= index) {
	            optimizationResults.add(null); // Fill with nulls or a default value.
	        }

	        // Update the specified index with the result.
	        optimizationResults.set(index, result);
	    }

}
