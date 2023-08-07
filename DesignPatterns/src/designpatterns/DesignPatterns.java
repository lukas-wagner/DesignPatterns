package designpatterns;


import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 

import ilog.concert.*;
import ilog.cplex.*;

public class DesignPatterns {

	static double[] solvingTimes = new double [4]; 

	public static void main(String[] args) {
		double timeInterval = 0.25; // 0.05 = 3Minuten, 0.125 = 7.5 Minuten
		int timeIntervalInMinutes = (int) (timeInterval*60); 

		int arrayLength = 96*100; // select price length
		double[] electricityPrice = getElectricityPrice(arrayLength);

		// parameters
		double minPowerInputResource1 = 0; 
		double maxPowerInputResource1 = 500;
		
		
		// convert price array to price array with appropriate resolution
		arrayLength = electricityPrice.length;

		double minRunTime = 0.25;
		double optimalityGap = 0.001;  
		optimizationModel(timeInterval, arrayLength, electricityPrice, optimalityGap, minPowerInputResource1, maxPowerInputResource1);

	}



	public static void optimizationModel(double timeInterval, int arrayLength, double[] electricityPriceLong, 	double optimalityGap, double minPowerInputResource1,  double maxPowerInputResource1) {
		try {
			@SuppressWarnings("resource")
			IloCplex cplex = new IloCplex();

			
			
			//Decision Variables
			
			IloNumVar[] powerInput= cplex.numVarArray(arrayLength,  0 ,  maxPowerInputResource1);
			


			//	System.out.println(cplex);
			cplex.exportModel("model.lp");

			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int i = 0; i < arrayLength; i++) {
//				objective.addTerm(timeInterval*electricityPriceLong[i], powerInput[i]);
			}

			cplex.addMinimize(objective);
			//			cplex.setParam(IloCplex.Param.Emphasis.Numerical, true);
			cplex.setParam(IloCplex.Param.MIP.Tolerances.MIPGap, optimalityGap); // 1e-3
			cplex.setParam(IloCplex.Param.ClockType, 2);
			long start = System.currentTimeMillis();
			if (cplex.solve()) {
				long end = System.currentTimeMillis();
				long solvingTime = 	(end - start);
				System.out.println("obj = "+cplex.getObjValue());
				System.out.println(cplex.getCplexStatus()); 

				
			} else {
				System.out.println("Model not solved");
			}
		}

		catch (IloException exc) {
			exc.printStackTrace();
		}
	}




	private static double[] getElectricityPrice(int i) {
		double[] electricityPrice = new double[] {
				85.87,37.21,32.62,32.27,51.03,63.76,44.84,39.2,58.28,43.46,42.47,47.48, 58.96,42.99,43.27,50.5,
				80.21,30.64,21.47,35.41,15.67,24.15,43.98,55.36,23.58,42.56,60.2,87.49,22.46,38.95,	31.68,60.19,
				17.49,37.23,40.51,57.98,46.62,46.74,61.23,71.33,57.2,55.6,59.39,81.35,48.79,58.3,58.01,57.73,71.85,
				64.5,57.07,57.84,44.25,38.6,65.02,67.76,42.31,62.06,83.36,100.31,57.25,66.51,84.52,95.44,73.57,84.5,
				85.41,98.72,75.05,98.16,101.62,96.88,127.03,92.24,102.1,79.11,140.98,93.1,91.67,66.07,125.87,89.96,
				76.41,48.12,100.5,74.96,74.03,64.77,100.75,75.26,87.21,38.04,85.35,60.38,56.9,35.95
		};
		double[] electricityPriceNew = new double[i];


		if (i>electricityPrice.length) {
			for (int j = 0; j < electricityPrice.length; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			for (int k = electricityPrice.length; k<i; k++) {
				electricityPriceNew[k] = electricityPrice[(int) (Math.random()*electricityPrice.length)];
			}
			return electricityPriceNew;
		} else {
			for (int j = 0; j < i; j++) {
				electricityPriceNew[j] = electricityPrice[j];
			}
			return electricityPriceNew; 
		}
	}


	
}
