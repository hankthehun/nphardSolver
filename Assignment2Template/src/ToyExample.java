import java.util.*;

// This model is given to you for free.
// You can use it as an example or to debug your Solver.
class ToyExample {
    /**
     * Attempts to find a solution to the Toy Example problem.
     * You do not need to touch this method.
     *
     * @return All solutions to the Toy Example problem.
     */
    public static List<int[]> solveProblem() {
        // Define variables
        Solver.Variable a = new Solver.Variable(new ArrayList<>(List.of(1, 2, 3, 4)));
        Solver.Variable b = new Solver.Variable(new ArrayList<>(List.of(2, 3, 4)));
        Solver.Variable c = new Solver.Variable(new ArrayList<>(List.of(1, 2, 3, 4)));
        Solver.Variable d = new Solver.Variable(new ArrayList<>(List.of(1, 2, 3)));
        Solver.Variable[] variables = new Solver.Variable[] { a, b, c, d };

        // Define constraints
        Solver.Constraint[] constraints = new Solver.Constraint[] {
                // a, b and d are all different
                new Solver.AllDiffConstraint(
                        new Solver.Variable[] { a, b, d }
                ),
                // 2*c + 3*d >= 14
                new Solver.IneqConstraint(
                        new Solver.Variable[] { c, d },
                        new int[] { 2, 3 },
                        14
                ),
                // c > a (rewritten as "1*c + -1*a >= 1")
                new Solver.IneqConstraint(
                        new Solver.Variable[] { c, a },
                        new int[] { 1, -1 },
                        1
                ),
                // c != b + 2
                new Solver.NotEqConstraint(
                        c,
                        b,
                        2
                ),
                // a != d - 1
                new Solver.NotEqConstraint(
                        a,
                        d,
                        -1
                ),
        };

        // Use solver
        Solver solver = new Solver(variables, constraints);
        List<int[]> result = solver.findAllSolutions();

        return result;
    }
}
