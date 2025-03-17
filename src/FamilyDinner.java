import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class FamilyDinner {
    /**
     * Attempts to find a solution to the Family Dinner problem.
     *
     * @param names A complete list of names of every person attending the dinner.
     * @param hates A list of pairs of people who should not be seated next to each other.
     * @return An array indicating who sits where around the table. null if there is no way to allocate everyone without breaking a constraint.
     */
    public static String[] solveProblem(String[] names, List<String[]> hates) {
        // TODO: Copy your code from "Family Dinner (Part 1)" here
        int n = names.length;
        Map<String, Integer> nameToIndex = new HashMap<>();
        Map<Integer, String> indexToName = new HashMap<>();
        for(int i =0; i<n; i++){
            nameToIndex.put(names[i], i);
            indexToName.put(i, names[i]);
        }

        List<Solver.Variable> variables = new ArrayList<>();
        // TODO: add your variables
        List<Integer> base = new ArrayList();
        for(int i =0; i<n; i++){
            base.add(i);
        }
        for(int i =0; i<n; i++){
            variables.add(new Solver.Variable(base));
        }


        List<Solver.Constraint> constraints = new ArrayList<>();
        // TODO: add your constraints
        Solver.Variable[] variableArray = variables.toArray(new Solver.Variable[n]);//convert to array because the solver wants it that way
        constraints.add(new Solver.AllDiffConstraint(variableArray));
        for(int i =0; i<hates.size(); i++){
            String[] pair = hates.get(i);
            int ind1 = nameToIndex.get(pair[0]);
            int ind2 = nameToIndex.get(pair[1]);
            constraints.add(new Solver.NotEqConstraint( // if you hate each other, you're not next to each other
                    variables.get(ind1),
                    variables.get(ind2),
                    1
            ));
            constraints.add(new Solver.NotEqConstraint(
                    variables.get(ind1),
                    variables.get(ind2),
                    -1
            ));

            constraints.add(new Solver.NotEqConstraint( // prevents them from sitting at the point where the table meets itself
                    variables.get(ind1),
                    variables.get(ind2),
                    n-1
            ));
            constraints.add(new Solver.NotEqConstraint(
                    variables.get(ind2),
                    variables.get(ind1),
                    n-1
            ));

        }

        // Use solver
        Solver solver = new Solver(
                variables.toArray(new Solver.Variable[0]),
                constraints.toArray(new Solver.Constraint[0])
        );
        int[] result = solver.findOneSolution();
        if(result == null){
            return null;
        }
        String[] solution = new String[n];
        for(int i =0; i<n; i++){
            String name = indexToName.get(i);
            solution[result[i]] = name;
        }
        // TODO: construct solution using result
        return solution;
    }
}
