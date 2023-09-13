package designpatterns;

/**
 * The Class PiecewiseLinearApproximation.
 */
public class PiecewiseLinearApproximation {
		
	/** The lower bound. */
	double lowerBound; 
	
	/** The upper bound. */
	double upperBound; 
	
	/** The slope. */
	double slope;
	
	/** The intercept. */
	double intercept;
	
	/**
	 * Gets the lower bound.
	 *
	 * @return the lowerBound
	 */
	public double getLowerBound() {
		return lowerBound;
	}
	
	/**
	 * Sets the lower bound.
	 *
	 * @param lowerBound the lowerBound to set
	 */
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	/**
	 * Gets the upper bound.
	 *
	 * @return the upperBound
	 */
	public double getUpperBound() {
		return upperBound;
	}
	
	/**
	 * Sets the upper bound.
	 *
	 * @param upperBound the upperBound to set
	 */
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
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
	 * Sets the pla.
	 *
	 * @param interceptpla the interceptpla
	 * @param slopepla the slopepla
	 * @param lowerboundpla the lowerboundpla
	 * @param upperboundpla the upperboundpla
	 */
	public void setPla (double interceptpla, double slopepla, double lowerboundpla, double upperboundpla) {
		this.intercept = interceptpla; 
		this.slope  = slopepla; 
		this.lowerBound = lowerboundpla; 
		this.upperBound = upperboundpla; 
	}
	
	/**
	 * Sets the efficiency, intercept = 0, lb = 0, ub = Double.maxvalue.
	 *
	 * @param efficiency the new efficiency
	 */
	public void setEfficiency (double efficiency) {
		this.intercept = 0; 
		this.slope  = efficiency; 
		this.lowerBound = 0; 
		this.upperBound = Double.MAX_VALUE; 
	}
	
	/**
	 * Sets the linear relationship, lb = 0, ub = Double.maxvalue.
	 *
	 * @param interceptLinear the intercept linear
	 * @param slopeLinear the slope linear
	 */
	public void setLinearRelationship (double interceptLinear, double slopeLinear) {
		this.intercept = interceptLinear; 
		this.slope  = slopeLinear; 
		this.lowerBound = 0; 
		this.upperBound = Double.MAX_VALUE; 
	}
	
}
