import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ProductionProblemInput {
    public int[] C;
    public int[][] c;
    public int[] P;
    public int[] r;
    public int R;
    public int solvable;

    public ProductionProblemInput(int[] C, int[][] c, int[] P, int[] r, int R, int solvable) {
        this.C = C;
        this.c = c;
        this.P = P;
        this.r = r;
        this.R = R;
        this.solvable = solvable;
    }
}

// Do not copy this file to WebLab!
// This code is almost exactly the same as the code you get on WebLab,
//     but some classes have been renamed to make the project compile.
// If you copy it back, the names changes might break your testing setup on WebLab.
public class ProductionTest {

    private String file;

    private long time = 0;

    public static Stream<Arguments> data() {
        // m, n, numInstances
        List<int[]> testDescriptions = List.of(
                new int[] { 1, 1, 2 },
                new int[] { 2, 2, 5 },
                new int[] { 3, 2, 1 },
                new int[] { 10, 2, 2 },
                new int[] { 2, 3, 3 },
                new int[] { 4, 3, 1 },
                new int[] { 50, 3, 4 },
                new int[] { 10, 4, 4 },
                new int[] { 25, 4, 4 },
                new int[] { 100, 4, 5 },
                new int[] { 500, 4, 5 },
                new int[] { 5, 5, 1 },
                new int[] { 50, 5, 5 },
                new int[] { 100, 5, 5 },
                new int[] { 100, 6, 5 },
                new int[] { 100, 7, 5 },
                new int[] { 100, 10, 5 },
                new int[] { 20, 20, 2 }
        );

        List<Arguments> files = new ArrayList<>();
        for (int[] data : testDescriptions) {
            int m = data[0];
            int n = data[1];
            int numInstances = data[2];
            for (int nr = 1; nr <= numInstances; nr++)
                files.add(Arguments.of("instances/production/m_" + m + "_n_" + n + "_nr_" + nr + ".txt"));
        }

        return files.stream();
    }

    @BeforeEach
    public void setUp() {
        time = System.currentTimeMillis();
    }

    @AfterEach
    public void tearDown() {
        long duration = System.currentTimeMillis() - time;
        System.out.println("Test '[file = " + file + "]' took " + duration + "ms");
    }

    private static ProductionProblemInput parseInput(String filename) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(filename)));
        Scanner sc = new Scanner(fileContent);

        int m = sc.nextInt();
        int n = sc.nextInt();

        int[] C = new int[m];
        int[][] c = new int[m][n];
        int[] P = new int[n];
        int[] r = new int[n];
        for (int i = 0; i < m; i++)
            C[i] = sc.nextInt();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                c[i][j] = sc.nextInt();
        for (int j = 0; j < n; j++)
            P[j] = sc.nextInt();
        for (int j = 0; j < n; j++)
            r[j] = sc.nextInt();
        int R = sc.nextInt();
        int solvable = sc.nextInt();

        sc.close();

        return new ProductionProblemInput(C, c, P, r, R, solvable);
    }

    @ParameterizedTest
    @MethodSource("data")
    @Timeout(value = 800, unit = TimeUnit.MILLISECONDS)
    public void testProduction(String file) {
        this.file = file;

        ProductionProblemInput problemInput;
        try {
            problemInput = parseInput(file);
        } catch (IOException e) {
            throw new AssertionError("Couldn't find file: " + file);
        }

        int[] C = problemInput.C;
        int[][] c = problemInput.c;
        int[] P = problemInput.P;
        int[] r = problemInput.r;
        int R = problemInput.R;
        int solvable = problemInput.solvable;

        int m = C.length;
        int n = P.length;

        // Copy to prevent students from tampering with the input to pass the tests
        int[] CCopy = new int[m];
        int[][] cCopy = new int[m][n];
        int[] PCopy = new int[n];
        int[] rCopy = new int[n];
        for (int i = 0; i < m; i++)
            CCopy[i] = C[i];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                cCopy[i][j] = c[i][j];
        for (int j = 0; j < n; j++)
            PCopy[j] = P[j];
        for (int j = 0; j < n; j++)
            rCopy[j] = r[j];

        int[] x = Production.solveProblem(CCopy, cCopy, PCopy, rCopy, R);

        if (solvable == 0) {
            if (x != null)
                throw new AssertionError("A solution was returned, even though none actually exist");
        } else {
            if (x == null)
                throw new AssertionError("No solution was found, even though one exists");
            ProductionVerifier.verifyCorrectness(C, c, P, r, R, x);
        }
    }
}

class ProductionVerifier {
    // Throws an error if the solution is not correct
    public static void verifyCorrectness(int[] C, int[][] c, int[] P, int[] r, int R, int[] x) {
        int m = C.length;
        int n = P.length;

        // Check validity of numbers in x
        if (x.length != n)
            throw new AssertionError("Expected an output of length " + n + ", but got one of length " + x.length);
        for (int j = 0; j < n; j++) {
            if (x[j] < 0)
                throw new AssertionError("Can't produce " + x[j] + " instances of product-type " + (j + 1));
            if (x[j] > P[j])
                throw new AssertionError("Produced " + x[j] + " instances of product-type " + (j + 1) + ", but only " + P[j] + " are allowed");
        }

        // Check production costs
        for (int i = 0; i < m; i++) {
            int available = C[i];
            int used = 0;
            for (int j = 0; j < n; j++)
                used += c[i][j] * x[j];
            if (used > available)
                throw new AssertionError("Used " + used + " units of material " + (i + 1) + ", but only " + available + " were available");
        }

        // Check desired revenue
        int obtained = 0;
        for (int j = 0; j < n; j++)
            obtained += r[j] * x[j];
        if (obtained < R)
            throw new AssertionError("Obtained a revenue of " + obtained + ", but expected at least " + R);
    }
}
