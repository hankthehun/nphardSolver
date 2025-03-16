import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ProblemInput {
    public String[] names;
    public List<String[]> hates;
    public int solvable;

    public ProblemInput(String[] names, List<String[]> hates, int solvable) {
        this.names = names;
        this.hates = hates;
        this.solvable = solvable;
    }
}

// Do not copy this file to WebLab!
// This code is almost exactly the same as the code you get on WebLab,
//     but some classes have been renamed to make the project compile.
// If you copy it back, the names changes might break your testing setup on WebLab.
public class FamilyDinnerTest {

    public String file;

    private long time = 0;

    public static Stream<Arguments> data() {
        // n, numInstances
        List<int[]> testDescriptions = List.of(
//                new int[] { 3, 2 },
//                new int[] { 6, 10 },
                new int[] { 8, 10 },
                new int[] { 10, 9 },
                new int[] { 12, 8 },
                new int[] { 13, 5 },
                new int[] { 26, 1 }
        );

        List<Arguments> files = new ArrayList<>();
        for (int[] data : testDescriptions) {
            int n = data[0];
            int numInstances = data[1];
            for (int nr = 1; nr <= numInstances; nr++)
                files.add(Arguments.of("instances/family_dinner/n_" + n + "_nr_" + nr + ".txt"));
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

    private static ProblemInput parseInput(String filename) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(filename)));
        Scanner sc = new Scanner(fileContent);

        int n = sc.nextInt();
        int m = sc.nextInt();

        String[] names = new String[n];
        List<String[]> hates = new ArrayList<>();
        for (int i = 0; i < n; i++)
            names[i] = sc.next();
        for (int i = 0; i < m; i++) {
            String name1 = sc.next();
            String name2 = sc.next();
            hates.add(new String[] { name1, name2 });
        }
        int solvable = sc.nextInt();

        sc.close();

        return new ProblemInput(names, hates, solvable);
    }

    @ParameterizedTest
    @MethodSource("data")
    @Timeout(value = 1100, unit = TimeUnit.MILLISECONDS)
    public void testFamilyDinner(String file) {
        this.file = file;

        ProblemInput problemInput;
        try {
            problemInput = parseInput(file);
        } catch (IOException e) {
            throw new AssertionError("Couldn't find file: " + file);
        }

        String[] names = problemInput.names;
        List<String[]> hates = problemInput.hates;
        int solvable = problemInput.solvable;

        int n = names.length;
        int m = hates.size();

        // Copy to prevent students from tampering with the input to pass the tests
        String[] namesCopy = new String[n];
        List<String[]> hatesCopy = new ArrayList<>();
        for (int i = 0; i < n; i++)
            namesCopy[i] = names[i];
        for (String[] hate : hates)
            hatesCopy.add(new String[] { hate[0], hate[1] });

        String[] solution = FamilyDinner.solveProblem(namesCopy, hatesCopy);

        if (solvable == 0) {
            if (solution != null)
                throw new AssertionError("A solution was returned, even though none actually exist");
        } else {
            if (solution == null)
                throw new AssertionError("No solution was found, even though one exists");
            FamilyDinnerVerifier.verifyCorrectness(names, hates, solution);
        }
    }

    // Throws an error if the solution is not correct
    public void verifyCorrectness(String[] names, List<String[]> hates, String[] solution) {
        int n = names.length;

        // Check validity of solution
        if (solution == null)
            throw new AssertionError("No solution was found, even though one exists");
        if (solution.length != n)
            throw new AssertionError("Expected an output of length " + n + ", but got one of length " + solution.length);
        for (int i = 0; i < n; i++) {
            if (solution[i] == null)
                throw new AssertionError("Solution contains null at index " + i);
        }
        Set<String> namesSet = new HashSet<>(Arrays.asList(names));
        Set<String> solutionSet = new HashSet<>(Arrays.asList(solution));
        if (!namesSet.equals(solutionSet))
            throw new AssertionError("Solution does not contain the correct names");

        // Check hates-constraint
        Map<String, Set<String>> cannotStand = new HashMap<>();
        for (String name : names)
            cannotStand.put(name, new HashSet<>());
        for (String[] hate : hates) {
            cannotStand.get(hate[0]).add(hate[1]);
            cannotStand.get(hate[1]).add(hate[0]);
        }
        for (int i = 0; i < n; i++) {
            String name1 = solution[i];
            String name2 = solution[(i + 1) % n];
            if (cannotStand.get(name1).contains(name2))
                throw new AssertionError(name1 + " should not sit next to " + name2);
        }
    }

}

class FamilyDinnerVerifier {

    // Throws an error if the solution is not correct
    public static void verifyCorrectness(String[] names, List<String[]> hates, String[] solution) {
        int n = names.length;

        // Check validity of solution
        if (solution == null)
            throw new AssertionError("No solution was found, even though one exists");
        if (solution.length != n)
            throw new AssertionError("Expected an output of length " + n + ", but got one of length " + solution.length);
        for (int i = 0; i < n; i++) {
            if (solution[i] == null)
                throw new AssertionError("Solution contains null at index " + i);
        }
        Set<String> namesSet = new HashSet<>(Arrays.asList(names));
        Set<String> solutionSet = new HashSet<>(Arrays.asList(solution));
        if (!namesSet.equals(solutionSet))
            throw new AssertionError("Solution does not contain the correct names");

        // Check hates-constraint
        Map<String, Set<String>> cannotStand = new HashMap<>();
        for (String name : names)
            cannotStand.put(name, new HashSet<>());
        for (String[] hate : hates) {
            cannotStand.get(hate[0]).add(hate[1]);
            cannotStand.get(hate[1]).add(hate[0]);
        }
        for (int i = 0; i < n; i++) {
            String name1 = solution[i];
            String name2 = solution[(i + 1) % n];
            if (cannotStand.get(name1).contains(name2))
                throw new AssertionError(name1 + " should not sit next to " + name2);
        }
    }

}