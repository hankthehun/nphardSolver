
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.*;

// Do not copy this file to WebLab!
// This code is almost exactly the same as the code you get on WebLab,
//     but some classes have been renamed to make the project compile.
// If you copy it back, the names changes might break your testing setup on WebLab.
public class ToyExampleTest {

    @Test
    @Timeout(value = 1100, unit = TimeUnit.MILLISECONDS)
    public void toyExampleTest() {
        // Solve problem
        List<int[]> solutions = ToyExample.solveProblem();

        // Validate output
        if (solutions == null)
            throw new AssertionError("solutions is null");
        for (int i = 0; i < solutions.size(); i++) {
            int[] solution = solutions.get(i);
            if (solutions.get(i) == null)
                throw new AssertionError("solution.get(" + i + ") is null");
            if (solution.length != 4)
                throw new AssertionError("solution.get(" + i + ") has the wrong length");
        }

        // Sort output
        Collections.sort(solutions, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                for (int i = 0; i < a.length; i++) {
                    int cmp = ((Integer) a[i]).compareTo(b[i]);
                    if (cmp != 0)
                        return cmp;
                }
                return 0;
            }
        });

        // Print found solutions
        System.out.println("Found solutions (" + solutions.size() + " in total):");
        System.out.println(" a, b, c, d");
        System.out.println("------------");
        for (int[] solution : solutions) {
            System.out.println(" " + solution[0] + ", " + solution[1] + ", " + solution[2] + ", " + solution[3]);
        }

        // Verify correctness
        List<int[]> expectedSolutions = List.of(
                new int[] {1, 2, 3, 3},
                new int[] {1, 4, 3, 3},
                new int[] {1, 4, 4, 3},
                new int[] {3, 4, 4, 2}
        );
        if (expectedSolutions.size() != solutions.size())
            throw new AssertionError("The result is incorrect");
        for (int i = 0; i < solutions.size(); i++) {
            int[] solution = solutions.get(i);
            int[] expectedSolution = expectedSolutions.get(i);
            for (int j = 0; j < solution.length; j++) {
                if (expectedSolution[j] != solution[j])
                    throw new AssertionError("The result is incorrect");
            }
        }
    }

}