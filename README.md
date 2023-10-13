<h1 align="center">Design Patterns for Energy Optimization</h1>
<hr>

# Table of Contents
1. [Introduction](https://github.com/lukas-wagner/DesignPatterns#Introduction)
2. [Requirements](https://github.com/lukas-wagner/DesignPatterns#Requirements)
3. [Description of Design Pattners](https://github.com/lukas-wagner/DesignPatterns#description-of-design-pattners)
<hr>

# Introduction
Energy optimization makes use of recurrent constraints. An analysis of which constraints are necessary for the representation of energy resources for optimization was conducted. The identified sets of constraints have been implemented as design patterns to facilitate reuse. 

# Requirements
The use of the design patterns requires an installation of Eclipse and IBM ILOG Cplex. The optimization problem can simply be created by setting parameters and calling the respective methods. 
Results will be written to a .csv-file. 

# Description of Design Pattners

## D1: Operational Boundaries
### Motivation
Most energy resources have a rated power capacity that cannot be exceeded during operation without damaging the resource. 
### Purpose:
This design pattern ensures that the power of material flow suggested by the optimization algorithm is within the operational boundaries / physical limitations of the energy resource.
### Applicability
The overall operational boundaries of an energy resource irrespective of its operational state is specified. I.e., the maximum power which can theoretically flow into or out of the resource is specified.
### Known Applications
<ul>
<li> P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010.</li>
<li> M. T. Baumhof, E. Raheli, A. G. Johnsen, and J. Kazempour, “Optimization of Hybrid Power Plants: When is a Detailed Electrolyzer Model Necessary?” in Proceedings of 2023 IEEE PowerTech, 2023, pp. 1–10.</li>
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li>
<li> M. K. Petersen, K. Edlund, L. H. Hansen, J. Bendtsen, and J. Stoustrup, “A taxonomy for modeling flexibility and a computationally efficient algorithm for dispatch in Smart Grids,” in 2013 American Control Conference. IEEE, 2013, pp. 1150–1156.</li>
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li>
<li> R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li>
<li> A. Maheshwari, N. G. Paterakis, M. Santarelli, and M. Gibescu, “Optimizing the operation of energy storage using a non-linear lithium-ion battery degradation model,” Applied Energy, vol. 261, p. 114360, 20</li>
</ul>
    
### Structure
For every time step of the optimization horizon, a decision variable is created representing the power flow into a resource for that time step. The decision variable is constrained to be lower than the maximum power flow of the resource.
### Consequences (Advantages and Disadvantages)
Energy Optimization problems become meaningful only if appropriate constraints are implemented. Operational boundaries need to be specified for nearly every energy resources.
Caution needs to be taken, that the optimization problem remains feasible. If operational targets are specified, which require a higher power flow than the operational boundaries, the optimization problem becomes infeasible. 
### Implementation
Optimization frameworks usually provide the possibility to constrain an optimization variable to be within a minimum and maximum boundary at the creation of the variable. This function is utilized to specify the operational boundary. 
### Method
designpatterns.DesignPatterns.creationOfDecisionVariables()

## D2: Input-Output Relationship
### Motivation 
For many energy resources, a relationship between the input flow(s) into the system to the output flow from the system can be formulated mathematically. Examples of this relationship are the efficiency of an electrolyser, or the coefficient of performance of a heat pump. 
### Purpose
Mathematically formulate the relationship between input and output of an energy resource. This assures that for a given (power) input into the system, the system provides a certain utility. 
### Applicability
The design pattern is applicable to any energy resource, that has a known relationship between its input and its output. The resource can have multiple inputs and outputs. Non-linear relationships need to be linearized. Targets for any input / output can be formulated to assure a certain utility of the energy resource. 
### Known Applications
<ul>
 <li>P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010.</li>
 <li>M. T. Baumhof, E. Raheli, A. G. Johnsen, and J. Kazempour, “Optimization of Hybrid Power Plants: When is a Detailed Electrolyzer Model Necessary?” in Proceedings of 2023 IEEE PowerTech, 2023, pp. 1–10.</li>
 <li>N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li>
 <li>M. K. Petersen, K. Edlund, L. H. Hansen, J. Bendtsen, and J. Stoustrup, “A taxonomy for modeling flexibility and a computationally efficient algorithm for dispatch in Smart Grids,” in 2013 American Control Conference. IEEE, 2013, pp. 1150–1156.</li>
 <li>L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li>
 <li>R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li>
</ul>

### Structure
At first it needs to be determined whether the energy resource exhibits a constant efficiency / COP, a linear input output relationship with a non-zero intercept, or a piecewise linear input-output relationship. The necessary constraints are enforced based one of the three aforementioned cases. 
### Consequences (Advantages and Disadvantages)
Often a trade-off between computational performance and accuracy of the modelled resource behaviour needs to be made. E.g., the computational effort should be assumed to scale exponentially with the number of piece-wise linear elements. 
### Implementation
Equality constraints, provided by every common optimization framework (CPLEX, Gurobi,…)  are used to enforce the input output relationships. Furhtermore, conditional constraints are used for the selection of the active segment.
Piece-wise linear relationships require the use of binary variables indicating the applicable linear element of the piece-wise approximation. 
### Method
designpatterns.DesignPatterns.generateInputOutputRelationship(String)
### Note
The object “String” is the name of the resource for which the input-output relationship is to be formulated. 

## D3 System states
### Motivation
Many energy resources can exhibit different states of operation, e.g., start-up, operation, shut-down, idle, off, etc.. 
### Purpose
Formulate mathematical expressions so that different system behaviours in different states of operations can be considered in the optimization model.  
### Applicability
The design pattern is applicable to any energy resource, that exhibits distinct states of operation. States can be used to represent actual system states, which represent states of a control strategy. Furthermore, states can be used to model predefined trajectories/patterns of energy consumption as time series (note: this must be done in conjunction with design pattern: state sequences).
### Known Applications
<ul>
<li> M. T. Baumhof, E. Raheli, A. G. Johnsen, and J. Kazempour, “Optimization of Hybrid Power Plants: When is a Detailed Electrolyzer Model Necessary?” in Proceedings of 2023 IEEE PowerTech, 2023, pp. 1–10.</li> 
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li> 
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li> 
<li> R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li> 
</ul>

### Structure
Each state is represented by a binary variable, indicating whether the respective state is active or not. The sum of all state binary variables must be one at every time step to ensure that only one state is active at any given time. The state binary variables are then integrated into input-output relationship-equations (design pattern: [input-output relationship](https://github.com/lukas-wagner/DesignPatterns#d2-input-output-relationship)), to assure that inactive states do not cause any energy conversion. 
### Consequences (Advantages and Disadvantages)
Often a trade-off between computational performance and accuracy of the modelled resource behaviour needs to be made. E.g., the computational effort should be assumed to scale exponentially with the number of system states of operation. 
### Implementation
Integer variables, which are constrained by upper and lower bounds (x = {0,1}), provided by every common optimization framework (CPLEX, Gurobi,…)  are used to enforce the system states of operation. 
### Method:
designpatterns.DesignPatterns.generateSystemStateSelectionByPowerLimits(String)
### Note: 
The object “String” is the name of the resource for which the system states are to be formulated. 

## D4 State Sequences
### Motivation
Many energy resources can exhibit different states of operation, e.g., start-up, operation, shut-down, idle, off, etc.. These states must sometimes follow predefined sequences, e.g., the state off may always have to follow the state shut-down.
### Purpose
Formulate mathematical expressions so that sequences of states always follow predefined rules.
### Applicability
The design pattern is applicable to any energy resource, that exhibits distinct states of operation, where the states must follow certain sequences. State sequences can be used to represent actual states in control programs but they can also be used to represent known trajectories/patterns of energy consumption (e.g. start-up process of an induction machine or a given washing machine program). 
### Known Applications
<ul>
<li> P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010.</li> 
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li> 
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li> 
<li> R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li> 
</ul>

### Structure
Each state is represented by a binary variable, indicating whether the respective state is active or not. The switch-off of a given state requires one state out of a predefined set of permissive follower states to be active. 
### Consequences (Advantages and Disadvantages)
Often a trade-off between computational performance and accuracy of the modelled resource behaviour needs to be made. E.g., the computational effort should be assumed to scale exponentially with the number of system states of operation. 
### Implementation
Binary variables (x = {0,1}), provided by every common optimization framework (CPLEX, Gurobi,…)  are used to enforce the system states of operation. 
Equality constraints are used to enforce that the switch-off of one state at the beginning of a given time step ‘t’ must be answered by the switch-on of a permissive follower state at the beginning of the same time step ‘t’. 
### Method
designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration(String)
### Note
The object “String” is the name of the resource for which the system states are to be formulated. 

## D5 Holding Durations
### Motivation
Many energy resources can exhibit different states of operation, e.g., start-up, operation, shut-down, idle, off, etc.. These states must sometimes be held for a minimum/maximum amount of time. 
### Purpose
Formulate mathematical expressions so that states of energy resources can be held no longer/shorter than realistically possible.
### Applicability
The design pattern is applicable to any energy resource, that exhibits distinct states of operation, where the states can only be held for / can be held no longer than a given amount of time. Holding Durations can be used in conjunction with systems states and state sequences to represent actual states in control programs but they can also be used to represent known trajectories/patterns of energy consumption (e.g. start-up process of an induction machine or a given washing machine program). 
### Known Applications
<ul>
<li> P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010.</li> 
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end users,” Applied Energy, vol. 282, p. 116183, 2021.</li> 
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018.</li>  
<li> R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li> 
</ul>

### Structure
Each state is represented by a binary variable, indicating whether the respective state is active or not. The sum of binary variables for a given state over a given time interval {t,…,t+intervalLength} must be within the permissive minimum/maximum holding duration.  
### Consequences (Advantages and Disadvantages)
Often a trade-off between computational performance and accuracy of the modelled resource behaviour needs to be made. E.g., the computational effort should be assumed to increase with increasing holding durations. 
### Implementation
Binary variables (z = {0,1}), provided by every common optimization framework (CPLEX, Gurobi,…)  are used to enforce the system states of operation. 
Inequality constraints are used to enforce that an active state can be active no longer/shorter than the permissive maximum/minimum holding durations by summing up the state variables from the time of switch-on of a state to one time step past the maximum/minimum holding duration and enforcing that this sum is lower or equal / greater or equal to the maximum/minimum holding duration. 
### Method
designpatterns.DesignPatterns.generateStateSequencesAndHoldingDuration(String)
### Note
The object “String” is the name of the resource for which the system states are to be formulated. 

## D6 Ramp Limits
### Motivation
Many energy resources are limited by the rate at which their operating point can be ramped up/ramped down, e.g., it takes some time to ramp up the operating state of a thermal power station. 
### Purpose
Formulate mathematical expressions so that the operating point of a resource can only be altered within the permissive ramping limits. 
### Applicability
The design pattern is applicable to any energy resource, that exhibits system dynamics (ramp up/ramp down) that are relatively slow compared to the chosen time interval. While almost every energy resource exhibits some kind of ramping limits, the limits can be neglected if the system dynamics are fast compared to the chosen time interval. 
### Known Applications
<ul>
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li> 
<li> M. K. Petersen, K. Edlund, L. H. Hansen, J. Bendtsen, and J. Stoustrup, “A taxonomy for modeling flexibility and a computationally efficient algorithm for dispatch in Smart Grids,” in 2013 American Control Conference. IEEE, 2013, pp. 1150–1156.</li> 
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li> 
</ul>

### Structure
The operating state is represented by a decision variable. The difference of this decision variable between any two adjacent time steps is enforced to be within the ramping limits. 
### Consequences (Advantages and Disadvantages)
Ramping limits can be used to limit rapid alteration of the operating point, e.g., to prevent excessive degradation of the energy resource. 
Enforcing ramping limits should be assumed to increase computational effort of the optimization problem. 
### Implementation
Inequality constraints are used to enforce that the absolute difference between two values of the decision variable at two adjacent time steps remain within the ramping limits.  
### Method
designpatterns.DesignPatterns.generateRampLimits(String1, String2)
### Note
The object “String1” is the name of the resource for which the system states are to be formulated. 
The object "String2” determines whether the ramping constraint is enforced at the input or output of a given resource (note that input and output of a resource are usually coupled via “input-output relationships). 

## D7 Storage Systems
### Motivation
(Energy) storage systems play a vital role for energy flexibility utilization and therefore need to be modelled in optimization problems.
### Purpose
Formulate mathematical equations to represent energy storage systems in Optimization Problems. 
### Applicability
The design pattern is applicable to any storage system relevant to the optimization problem. Energy storage systems can be modelled as well as material storage systems which act as “virtual energy storage systems”. 
### Known Applications
<ul>
<li> P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010.</li> 
<li> M. T. Baumhof, E. Raheli, A. G. Johnsen, and J. Kazempour, “Optimization of Hybrid Power Plants: When is a Detailed Electrolyzer Model Necessary?” in Proceedings of 2023 IEEE PowerTech, 2023, pp. 1–10.</li> 
<li> N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021.</li> 
<li> M. K. Petersen, K. Edlund, L. H. Hansen, J. Bendtsen, and J. Stoustrup, “A taxonomy for modeling flexibility and a computationally efficient algorithm for dispatch in Smart Grids,” in 2013 American Control Conference. IEEE, 2013, pp. 1150–1156.</li> 
<li> L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018. </li> 
<li> R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022.</li> 
</ul>

### Structure
Energy Storage Systems are modelled by means of an energy balance. The energy balance considers the energy content of a storage system at the beginning of a given time step and sums up all flows into and out of a given storage system over a given time interval to determine the energy content at the end of that time interval. This calculation is done for every time step. Energy losses can be considered as well as energy degradation. 
### Consequences (Advantages and Disadvantages):
Energy Storage system equations can lead to infeasible optimization problems if the storage size is too small to fulfil the given conversion targets. 
Energy storage degradation models can increase the computational burden especially for non linear degradation behaviour. Simple degradation models can be sufficient to represent resource degradation. 
### Implementation
Equality constraints, which are offered by all common optimization frameworks (CPLEX, Gurobi) are used to enforce energy balances over every time step. 
### Method
designpatterns.DesignPatterns.generateEnergyBalanceForStorageSystem(String)
### Note
The object “String” is the name of the resource for which the energy balance is to be formulated. 

## D8 Dependencies 
### Motivation
It is often insufficient to consider just one energy resource. Often several energy resources and their dependencies need to be considered to obtain valid operation schedules.  
### Purpose
Formulate mathematical equations to represent dependencies between different energy resources.  
### Applicability
The design pattern is applicable to sets of energy resources between which dependencies exist. Dependencies can be such that the output of one or more energy resources feed into one or more other energy resources in an n:m relationship. Correlative as well as restrictive dependencies can be formulated  
### Known Applications  
 <ul>
<li>  P. Ahcin and M. Šikic, “Simulating demand response and energy storage in energy distribution systems,” in 2010 International Conference on Power System Technology. IEEE, 2010. </li> 
<li>  M. T. Baumhof, E. Raheli, A. G. Johnsen, and J. Kazempour, “Optimization of Hybrid Power Plants: When is a Detailed Electrolyzer Model Necessary?” in Proceedings of 2023 IEEE PowerTech, 2023, pp. 1–10. </li> 
 <li>   N. Wanapinit, J. Thomsen, C. Kost, and A. Weidlich, “An MILP model for evaluating the optimal operation and flexibility potential of end uers,” Applied Energy, vol. 282, p. 116183, 2021. </li> 
 <li>   M. K. Petersen, K. Edlund, L. H. Hansen, J. Bendtsen, and J. Stoustrup, “A taxonomy for modeling flexibility and a computationally efficient algorithm for dispatch in Smart Grids,” in 2013 American Control Conference. IEEE, 2013, pp. 1150–1156. </li> 
 <li>   L. F. J. Barth, N. N. Ludwig, E. Mengelkamp, and P. Staudt, “A comprehensive modelling framework for demand side flexibility in smart grids,” Computer Science - Research and Development, vol. 33, no. 1-2, pp. 13–23, 2018.  </li> 
 <li>   R. Bahmani, C. van Stiphoudt, S. P. Menci, M. Schöpf, and G. Fridgen, “Optimal industrial flexibility scheduling based on generic data format,” Energy Informatics, vol. 5, no. S1, 2022. </li> 
</ul>

### Structure
Dependencies are formulated such that the output of ‘m’ energy resources must equal the input of ‘n’ energy resources. In a correlative dependency ‘m’ resources can feed into ‘n’ resources without further restrictions. In restrictive dependencies, one of the ‘n’ resources obtaining input can only do so from one of the ‘m’ resources providing output at any given time step.  

### Consequences (Advantages and Disadvantages)
Optimization problems of flexible energy resources often only become valid once the proper dependencies are formulated.  
Restrictive energy balances have been observed to significantly increase computational effort since they make use of binary decision variables.  

### Implementation
Equality constraints, which are offered by all common optimization frameworks (CPLEX, Gurobi) are used to enforce equality between ‘m’ inputs on one side and ‘n’ outputs on the other side. 
In restrictive dependencies, a matrix of ‘m’x’n’ binary variables is used for each time step, each element taking the value 1 if  resource ‘m’ feeds into resource ‘n’ at the respective time step. The sum of each column and each row must be no greater than 1.  The implementation of restrictive dependencies makes use of conditional constraints. 

### Method
<ul>
<li>designpatterns.DesignPatterns.generateRestrictiveDependency(IloNumVar1[][], IloNumVar2[][])</li> 
<li>designpatterns.DesignPatterns.generateCorrelativeDependency(IloNumVar1[][], IloNumVar2[][]) </li> 
</ul>
