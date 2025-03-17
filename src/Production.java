import java.util.*;
import java.util.stream.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class Production {
    // Generates a variable that ranges from start to end (both inclusive)
    public static Solver.Variable genRangeVariable(int start, int end){
        return new Solver.Variable(IntStream.rangeClosed(start, end)
                .boxed()
                .collect(Collectors.toList()));
    }

    // Multiply all the array elements by -1
    public static int[] negate(int[] array){
        int[] result = new int[array.length];
        for(int i = 0; i < array.length; i++){
            result[i] = -1 * array[i];
        }
        return result;
    }

    /**
     * Attempts to find a solution to the Production problem.
     *
     * @param C The number of units available for each type of material.
     * @param c A 2D-array describing how many units of a type of material are needed to produce on instance of a particular product-type.
     * @param P The maximum number of instances that can be made of a product-type.
     * @param r How much revenue one instance of a product-type will yield.
     * @param R The desired minimum revenue.
     * @return An array describing how many instances of each product-type should be produced to obtained enough revenue. null if this is not possible.
     */
    public static int[] solveProblem(int[] C, int[][] c, int[] P, int[] r, int R) {
        int numMaterialTypes = C.length;
        int numProductTypes = P.length;

        List<Solver.Variable> variables = new ArrayList<>();
        Solver.Variable[] productVariables = new Solver.Variable[numProductTypes];

        // Add a variable for each product
        for(int i = 0; i < numProductTypes; i++){
            productVariables[i] = genRangeVariable(0, P[i]);
            variables.add(productVariables[i]);
        }

        List<Solver.Constraint> constraints = new ArrayList<>();
        constraints.add(new Solver.IneqConstraint(productVariables, r, R));
        for(int i = 0; i < numMaterialTypes; i++){
            constraints.add(new Solver.IneqConstraint(productVariables, negate(c[i]), -1*C[i]));
        }


        // Use solver
        Solver solver = new Solver(
                variables.toArray(new Solver.Variable[0]),
                constraints.toArray(new Solver.Constraint[0])
        );
        int[] result = solver.findOneSolution();

        return result;
    }
}
