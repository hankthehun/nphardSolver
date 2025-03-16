import java.util.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class NQueens {
    /**
     * Returns the number of solutions to the N-Queens problem.
     *
     * @param n The number of queens and the size of the board.
     * @return The number of valid ways to arrange the queens.
     */
    public static int solveProblem(int n) {
        // TODO: Copy your code from "N-Queens (Part 1)" here
        List<Solver.Variable> variables = new ArrayList<>();
        // TODO: add your variables

        List<Solver.Constraint> constraints = new ArrayList<>();
        // TODO: add your constraints
        List<Integer> base = new ArrayList();
        for(int i =0; i<n; i++){
            base.add(i);
        }
        for(int i =0; i<n; i++){ // add a variable for each of the columns and rows
            variables.add(new Solver.Variable(base));
        }
        constraints.add(new Solver.AllDiffConstraint(variables.toArray(new Solver.Variable[0])));

        for(int i = 0; i<n; i++){
            for(int j =0; j<i; j++){

                constraints.add(new Solver.NotEqConstraint( // you can't be on the same diagonal
                        variables.get(i),
                        variables.get(j),
                        i-j
                ));
                constraints.add(new Solver.NotEqConstraint( // you can't be on the same diagonal
                        variables.get(i),
                        variables.get(j),
                        j-i
                ));
            }
        }


        // Use solver
        Solver solver = new Solver(
                variables.toArray(new Solver.Variable[0]),
                constraints.toArray(new Solver.Constraint[0])
        );
        List<int[]> result = solver.findAllSolutions();

        // TODO: construct solution using result
        return result.size();
    }
}
