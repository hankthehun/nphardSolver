import java.util.*;

// Copy the below class to each of the Part 2 WebLab exercises
//    when submitting your solution.
// Make sure you copy the EXACT same version of your Solver in each
//    of the five exercises before the deadline, you'll be
//    automatically flagged for review if you don't!

// Write your solver below here.
// A template is already provided. You are allowed to deviate from this
//     template, as long as all classes and methods mentioned in the
//     description exist.

class Solver {
    static class Variable {
        public List<Integer> domain;
        // TODO: Add any fields you want here...

        /**
         * Constructs a Variable with a specified domain.
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param domain A list of integers, representing the domain of the variable.
         */
        public Variable(List<Integer> domain) {
            // Variable initialization
            this.domain = new ArrayList<>(domain);

            // TODO: Add any more logic you want here...
        }

        // TODO: Add any methods you want here...
    }

    static abstract class Constraint {
        // TODO: Add any methods you want here...
    }

    static class NotEqConstraint extends Constraint {
        private Variable x1;
        private Variable x2;
        private int c;
        // TODO: Add any fields you want here...

        /**
         * Constructs a NotEqConstraint:
         *    x1 != x2 + c
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param x1 The first variable.
         * @param x2 The second variable.
         * @param c An integer constant.
         */
        public NotEqConstraint(Variable x1, Variable x2, int c) {
            // Variable initialization
            this.x1 = x1;
            this.x2 = x2;
            this.c = c;

            // TODO: Add any more logic you want here...
        }

        // TODO: Add any methods you want here...
    }

    static class AllDiffConstraint extends Constraint {
        private Variable[] xs;
        // TODO: Add any fields you want here...

        /**
         * Constructs an AllDiffConstraint:
         *    AllDifferent(x1, ..., xn)
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param xs An array of a variables that should be different.
         */
        public AllDiffConstraint(Variable[] xs) {
            // Variable initialization
            this.xs = xs;

            // TODO: Add any more logic you want here...
        }

        // TODO: Add any methods you want here...
    }

    static class IneqConstraint extends Constraint {
        private Variable[] xs;
        private int[] ws;
        private int c;
        // TODO: Add any fields you want here...

        /**
         * Constructs an IneqConstraint:
         *    w1 * x1 + ... + wn * xn >= c
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param xs An array of all variables involved.
         * @param ws An array of respective integer weights.
         * @param c An integer constant.
         */
        public IneqConstraint(Variable[] xs, int[] ws, int c) {
            // Variable initialization
            this.xs = xs;
            this.ws = ws;
            this.c = c;

            // TODO: Add any more logic you want here...
        }

        // TODO: Add any methods you want here...
    }

    private Constraint[] constraints;
    private Variable[] variables;
    private List<int[]> foundSolutions;
    // TODO: Add any fields you want here...
    public List<Integer> currentAssignment;
    private Map<Variable, Integer> variableToIndex;

    /**
     * Constructs a Solver using a list of variables and constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return An integer-array values to assign to the variables,
     *             in the order they are provided.
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        // Initialize variables
        this.variables = variables;
        this.constraints = constraints;
        this.foundSolutions = new LinkedList<>();
        this.currentAssignment = new ArrayList<>();
        this.variableToIndex = new HashMap<>();
        for(int i = 0; i<variables.length; i++){
            variableToIndex.put(variables[i], i);
        }
        // TODO: Add any more logic you want here...
    }

    /**
     * Attempts to find one solution that satisfies the constraints.
     *     Should terminate immediately when a solution is found.
     *     Should return null if no solution exists.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return An integer-array values to assign to the variables,
     *             in the order they are provided.
     */
    public int[] findOneSolution() {
        // Find a solution
        foundSolutions.clear();
        solve(false);

        // Return the found solution (or null if it doesn't exist)
        if (foundSolutions.isEmpty())
            return null;
        return foundSolutions.get(0);
    }

    /**
     * Attempts to find all solutions that satisfy the constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     *
     * @return A list of integer-arrays, each representing a different
    solution to the problem (same format as findOneSolution).
     */
    public List<int[]> findAllSolutions() {
        // Find a solution
        foundSolutions.clear();
        solve(true);

        // Return all found solutions
        return foundSolutions;
    }

    /**
     * Applies search and inference to find value assignments to each variable,
     *    such that all constraints are satisfied. Any found solution is added
     *    to the list `foundSolutions`.
     * You are allowed to change or even remove this method.
     *
     * @param findAll True if all solutions must be found, false if only
     *                    only one needs to be found.
     */
    private void solve(boolean findAll) {
        // TODO: Do search and inference on the variables and constraints to find solutions
        solveBacktracking(0, findAll);
    }



    public boolean isValid(){
        for(int i = 0; i<constraints.length; i++){
            if(constraints[i] instanceof AllDiffConstraint){
                AllDiffConstraint constraint = (AllDiffConstraint) constraints[i];
                Set<Integer> set = new HashSet<>();
                for(int j = 0; j<constraint.xs.length; j++){
                    int ind1 = variableToIndex.get(constraint.xs[j]);
                    if(set.contains(currentAssignment.get(ind1))){
                        return false;
                    }
                    set.add(currentAssignment.get(j));
                }
            }
            else if(constraints[i] instanceof NotEqConstraint){
                NotEqConstraint constraint = (NotEqConstraint) constraints[i];
                int ind1 = variableToIndex.get(constraint.x1);
                int ind2 = variableToIndex.get(constraint.x2);
                int v1 = currentAssignment.get(ind1);
                int v2 = currentAssignment.get(ind2);
                if (v1 == v2 + constraint.c) {
                    return false;
                }
            }
            else if(constraints[i] instanceof IneqConstraint){
                IneqConstraint constraint = (IneqConstraint) constraints[i];
                int sum = 0;
                for(int j = 0; j<constraint.xs.length; j++){
                    sum += constraint.ws[j] * currentAssignment.get(variableToIndex.get(constraint.xs[j]));
                }
                if(sum < constraint.c){
                    return false;
                }
            }
        }
        return true;
    }

    private void solveBacktracking(int n, boolean findAll){
        if(!findAll && foundSolutions.size() > 0){
            return;
        }
        if(n == variables.length){
            if(isValid()){
                int[] solution = new int[variables.length];
                for(int i = 0; i<variables.length; i++){
                    solution[i] = currentAssignment.get(i);
                }
                foundSolutions.add(solution);
            }
        }
        else{
            for(int i = 0; i<variables[n].domain.size(); i++){
                currentAssignment.add(variables[n].domain.get(i));
                solveBacktracking(n+1, findAll);
                currentAssignment.remove(currentAssignment.size()-1);
            }
        }
    }

    // You are free to add any helper methods you might want to use within
    //     the solver. Note, however, that you would not be allowed to call
    //     them directly from within the `solveProblem`-method, since you
    //     must copy your `solveProblem`-code from Part 1, which wouldn't
    //     have these helper methods defined.

}