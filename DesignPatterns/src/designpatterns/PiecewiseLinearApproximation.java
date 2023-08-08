package designpatterns;

public class PiecewiseLinearApproximation {
	double lowerBound; 
	double upperBound; 
	double slope;
	double intercept;
	/**
	 * @return the lowerBound
	 */
	public double getLowerBound() {
		return lowerBound;
	}
	/**
	 * @param lowerBound the lowerBound to set
	 */
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	/**
	 * @return the upperBound
	 */
	public double getUpperBound() {
		return upperBound;
	}
	/**
	 * @param upperBound the upperBound to set
	 */
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
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


}
