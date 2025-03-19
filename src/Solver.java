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
        public Stack<TreeSet<Integer>> domainStack;

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
            this.domainStack = new Stack<>();
            this.domainStack.push(new TreeSet<>(domain));
        }


        public void copy(){
            if(this.domainStack.isEmpty()) return;
            this.domainStack.push(new TreeSet<>(this.domainStack.peek()));
        }

        public TreeSet<Integer> pop(){
            return this.domainStack.pop();
        }
        public TreeSet<Integer> getCurrentDomain(){
            return this.domainStack.peek();
        }

        /**
         * Removes all values in the domain that are outside the given range.
         * Then checks if the domain is empty.
         * [lower, upper]
         * @param lower The lower bound of the domain (inclusive)
         * @param upper The upper bound of the domain (inclusive)
         * @return whether the domain is empty
         */
        public boolean clamp(int lower, int upper) {
            this.domain.removeIf(x -> x < lower || x > upper);
            return this.domain.isEmpty();
        }

        /**
         * @return The highest value in the domain.
         */
        public int getUpperBound(){
            if (this.domain.isEmpty())
                return Integer.MIN_VALUE;
            return this.domain.get(this.domain.size() - 1);
        }
    }

    static abstract class Constraint {
        public abstract boolean isValid(List<Variable> variables, List<Integer> assignment);

        public abstract boolean updateDomains(Variable variable);
    }

    static class NotEqConstraint extends Constraint {
        private final Variable x1;
        private final Variable x2;
        private final int c;

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
        }

        @Override
        public boolean isValid(List<Variable> variables, List<Integer> assignment) {
            int index1 = variables.indexOf(x1);
            int index2 = variables.indexOf(x2);
            return assignment.get(index1) != assignment.get(index2) + c;
        }

        @Override
        public boolean updateDomains(Variable variable) {
            return false;
        }
    }

    static class AllDiffConstraint extends Constraint {
        private final Variable[] xs;

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
        }

        @Override
        public boolean isValid(List<Variable> variables, List<Integer> assignment) {
            Set<Integer> set = new HashSet<>();
            for(Variable var : xs) {
                int index = variables.indexOf(var);
                if(set.contains(assignment.get(index))){
                    return false;
                }
                set.add(assignment.get(index));
            }
            return true;
        }

        @Override
        public boolean updateDomains(Variable variable) {
            return false;
        }
    }

    static class IneqConstraint extends Constraint {
        private final Variable[] xs;
        private final int[] ws;
        private final int c;
        private final int amount;

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
            this.amount = Math.min(xs.length, ws.length);
        }

        @Override
        public boolean isValid(List<Variable> variables, List<Integer> assignment) {
            int sum = 0;
            for(int i = 0; i < amount; i++){
                sum += ws[i] * assignment.get(variables.indexOf(xs[i]));
            }
            return sum >= c;
        }

        @Override
        public boolean updateDomains(Variable variable) {
            for(int i = 0; i < xs.length; i++){
                if(xs[i] == variable) continue;

                int sum = 0;
                for(int j = 0; j < xs.length; j++){
                    if (j == i) continue;
                    sum += ws[j] * xs[j].getUpperBound();
                }
                int upperbound = (c - sum) / ws[i];
                if (xs[i].clamp(Integer.MIN_VALUE, upperbound))
                    return false;
            }
            return true;
        }
    }

    private final List<Constraint> constraints;
    private final List<Variable> variables;
    private final List<int[]> foundSolutions;
    private Map<Variable, Integer> variableToIndex;

    /**
     * Constructs a Solver using a list of variables and constraints.
     * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
     *     However, you are allowed to change its behavior in any way you want.
     */
    public Solver(Variable[] variables, Constraint[] constraints) {
        // Initialize variables
        this.variables = new ArrayList<>(List.of(variables));
        this.constraints = new ArrayList<>(List.of(constraints));
        this.foundSolutions = new LinkedList<>();
        this.variableToIndex = new HashMap<>();
        for(int i = 0; i<variables.length; i++){
            variableToIndex.put(variables[i], i);
        }
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
        solveBacktracking(0, findAll);
    }



    public boolean isValid(){
        return variables.stream().noneMatch(v -> v.domainStack.peek().isEmpty());
//        return constraints.stream().allMatch(c -> c.isValid(variables, currentAssignment));
    }

    private void solveBacktracking(int n, boolean findAll){
        if(!findAll && !foundSolutions.isEmpty()){
            return;
        }
        if(n == variables.size()){
            if(isValid()){
                int[] solution = new int[variables.size()];
                for(int i = 0; i<variables.size(); i++){
                    solution[i] = variables.get(i).domainStack.peek().first();
                }
                foundSolutions.add(solution);
            }
        }
        else{
            Variable v = variables.get(n);
            List<Integer> elementsToIterate = new ArrayList<>(v.domainStack.peek()); // Create a separate list to iterate
            for (Integer x : elementsToIterate) {
                v.domainStack.push(new TreeSet<>(List.of(x))); //Assign a value to this variable
                updateDomains(n);
                solveBacktracking(n+1, findAll);
                v.domainStack.pop();
            }
//            for(int i = 0; i < variables.get(n).domain.size(); i++){
//                currentAssignment.add(variables.get(n).domain.get(i));
//                solveBacktracking(n+1, findAll);
//                currentAssignment.remove(currentAssignment.size()-1);
//            }
        }
    }
    public void updateDomains(int n){
        for(Constraint c: constraints){
            if(c instanceof AllDiffConstraint) {
                AllDiffConstraint constraint = (AllDiffConstraint) c;
                Set<Variable> vars = new HashSet<>();
                Collections.addAll(vars, constraint.xs);
                for(Variable v: constraint.xs){
                    v.copy();
                }
                Integer x = variables.get(n).domainStack.peek().first();
                if (vars.contains(variables.get(n))) {
                    for (int i = 0; i < constraint.xs.length; i++) {
                        int subjIndex = variableToIndex.get(constraint.xs[i]);
                        if (subjIndex != n) {
                            for(Integer num : constraint.xs[i].domainStack.peek()){
                                if(num == x){
                                    constraint.xs[i].getCurrentDomain().remove(x);
                                }
                            }
                        }
                    }
                }
            }
            else if(c instanceof NotEqConstraint){
                NotEqConstraint constraint = (NotEqConstraint) c;
                int check = variables.get(n).getCurrentDomain().first();
                if(constraint.x1 == variables.get(n)){
                    constraint.x2.copy();
                    constraint.x2.getCurrentDomain().remove(check - constraint.c);
                }
                if(constraint.x2 == variables.get(n)){
                    constraint.x1.copy();
                    constraint.x1.getCurrentDomain().remove(check + constraint.c);
                }
            }
            if (c instanceof IneqConstraint) {
                IneqConstraint constraint = (IneqConstraint) c;
                constraint.updateDomains(variables.get(n));
            }
        }
    }

    // You are free to add any helper methods you might want to use within
    //     the solver. Note, however, that you would not be allowed to call
    //     them directly from within the `solveProblem`-method, since you
    //     must copy your `solveProblem`-code from Part 1, which wouldn't
    //     have these helper methods defined.

}