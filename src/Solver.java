import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        public Stack<TreeSet<Integer>> domainStack;

        /**
         * Constructs a Variable with a specified domain.
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param domain A list of integers, representing the domain of the variable.
         */
        public Variable(List<Integer> domain) {
            this.domainStack = new Stack<>();
            this.domainStack.push(new TreeSet<>(domain));
        }

        /**
         * Pushes a copy of the current domain on the stack.
         */
        public void copy(){
            if(this.domainStack.isEmpty()) return;
            this.domainStack.push(new TreeSet<>(this.domainStack.peek()));
        }

        /**
         * Pushes a new domain on the stack, containing only the given value.
         *
         * @param value The value assigned to this variable.
         */
        public void assign(int value) {
            domainStack.push(new TreeSet<>(List.of(value)));
        }

        /**
         * Pops the domain on top of the stack.
         */
        public void pop(){
            this.domainStack.pop();
        }

        /**
         * Getter for the current domain, obtained by peeking in the stack.
         *
         * @return The current domain of the variable.
         */
        public TreeSet<Integer> getCurrentDomain(){
            return this.domainStack.peek();
        }

        /**
         * Checks if the domain is not empty.
         *
         * @return Whether the domain is valid, that is if it contains at least one value.
         */
        public boolean isDomainValid(){
            return !this.domainStack.isEmpty();
        }

        /**
         * Getter for the first value in the domain.
         * This function does not check if the domain contains multiple values.
         *
         * @return A fixed value for this variable.
         */
        public int getAssignedValue(){
            return this.getLowerBound();
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
            this.getCurrentDomain().removeIf(x -> x < lower || x > upper);
            return this.getCurrentDomain().isEmpty();
        }

        /**
         * @return The highest value in the domain.
         */
        public int getUpperBound(){
            if (this.getCurrentDomain().isEmpty())
                return Integer.MIN_VALUE;
            return this.getCurrentDomain().last();
        }

        /**
         * @return The lowest value in the domain.
         */
        public int getLowerBound(){
            if (this.getCurrentDomain().isEmpty())
                return Integer.MAX_VALUE;
            return this.getCurrentDomain().first();
        }
    }

    static abstract class Constraint {

        /**
         * Propagates the constraint over all the variables concerned based on the variable that was updated.
         *
         * @param variable The variable that was assigned a value.
         * @return Whether the constraint is respected, that is no conflict occurred.
         */
        public abstract boolean updateDomains(Variable variable);

        /**
         * Checks if the variables respect the constraint.
         * This function assumes all variables have an assigned value.
         *
         * @return Whether the constraint is respected or not.
         */
        public abstract boolean isRespected();
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
            this.x1 = x1;
            this.x2 = x2;
            this.c = c;
        }

        @Override
        public boolean updateDomains(Variable variable) {
            int check = variable.getAssignedValue();
            if(x1 == variable){
                x2.getCurrentDomain().remove(check - c);
                return x2.isDomainValid();
            }
            else if(x2 == variable){
                x1.getCurrentDomain().remove(check + c);
                return x1.isDomainValid();
            }
            return true;
        }

        @Override
        public boolean isRespected() {
            return x1.getAssignedValue() != x2.getAssignedValue() + c;
        }
    }

    static class AllDiffConstraint extends Constraint {
        private final Set<Variable> xs;

        /**
         * Constructs an AllDiffConstraint:
         *    AllDifferent(x1, ..., xn)
         * DO NOT REMOVE THIS METHOD OR CHANGE ITS SIGNATURE.
         *     However, you are allowed to change its behavior in any way you want.
         *
         * @param xs An array of a variables that should be different.
         */
        public AllDiffConstraint(Variable[] xs) {
            this.xs = new HashSet<>(Arrays.asList(xs));
        }

        @Override
        public boolean updateDomains(Variable variable) {
            Integer x = variable.getAssignedValue();

            if(!xs.contains(variable))
                return true;

            for (Variable v: xs) {
                if (v != variable){
                    v.getCurrentDomain().remove(x);
                    if(!v.isDomainValid())
                        return false;
                }
            }
            return true;
        }

        @Override
        public boolean isRespected() {
            return xs.stream()
                    .map(Variable::getAssignedValue)
                    .collect(Collectors.toSet())
                    .size() == xs.size();
        }
    }

    static class IneqConstraint extends Constraint {
        private final int[] ws;
        private final Variable[] xs;
        private final int amount;
        private final int c;

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
            this.c = c;
            this.amount = Math.min(xs.length, ws.length);
            this.xs = xs;
            this.ws = ws;
        }

        @Override
        public boolean updateDomains(Variable variable) {
            for(int i = 0; i < amount; i++){
                if(xs[i] == variable) continue;
                if(ws[i] == 0) continue;

                int maxSum = 0;
                for(int j = 0; j < amount; j++){
                    if (j == i) continue;
                    maxSum += ws[j] >= 0 ? ws[j] * xs[j].getUpperBound() : ws[j] * xs[j].getLowerBound();
                }

                if (ws[i] >= 0){
                    int lowerBound = Math.ceilDiv(c - maxSum, ws[i]);
                    if (xs[i].clamp(lowerBound, Integer.MAX_VALUE))
                        return false;
                }
                else{
                    int upperBound = Math.floorDiv(c - maxSum, ws[i]);
                    if (xs[i].clamp(Integer.MIN_VALUE, upperBound))
                        return false;
                }
            }
            return true;
        }

        @Override
        public boolean isRespected() {
            return IntStream.range(0, amount).map(i -> ws[i] * xs[i].getAssignedValue()).sum() >= c;
        }
    }

    private final List<Constraint> constraints;
    private final List<Variable> variables;
    private final List<int[]> foundSolutions;

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
     * @param findAll True if all solutions must be found, false if only one needs to be found.
     */
    private void solve(boolean findAll) {
        solveBacktracking(0, findAll);
    }

    public boolean isValid(){
        return variables.stream().allMatch(Variable::isDomainValid) &&
                constraints.stream().allMatch(Constraint::isRespected);
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
            if(v.getCurrentDomain().size() == 1){
                solveBacktracking(n+1, findAll);
                return;
            }

            List<Integer> elementsToIterate = new ArrayList<>(v.getCurrentDomain());
            for (int x : elementsToIterate) {
                v.assign(x);
                for(Variable variable : variables){
                    if(variable != v)
                        variable.copy();
                }

                if (updateDomains(v))
                    solveBacktracking(n+1, findAll);

                variables.forEach(Variable::pop);
            }
        }
    }

    public boolean updateDomains(Variable variable){
        for(Constraint c: constraints) {
            if (!c.updateDomains(variable))
                return false;
        }
        return true;
    }
}