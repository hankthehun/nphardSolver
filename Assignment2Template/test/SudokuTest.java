import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

// Do not copy this file to WebLab!
// This code is almost exactly the same as the code you get on WebLab,
//     but some classes have been renamed to make the project compile.
// If you copy it back, the names changes might break your testing setup on WebLab.
public class SudokuTest {

    public String file;

    private long time = 0;

    public static Stream<Arguments> data() {
        List<Arguments> files = new ArrayList<>();

        int[] sizes = { 9, 16, 25 };
        List<int[]> diffs = List.of(
                new int[] { 45, 65, 85 },
                new int[] { 35, 45, 55 },
                new int[] {  5, 15, 25, 35, 45 }
        );

        for (int i = 0; i < sizes.length; i++)
            for (int j = 0; j < diffs.get(i).length; j++)
                for (int nr = 1; nr <= 5; nr++)
                    files.add(Arguments.of("instances/sudoku/size_" + sizes[i] + "_diff_" + diffs.get(i)[j] + "_nr_" + nr + ".txt"));

        // files = List.of(Arguments.of("size_9_diff_5_nr_1.txt"));
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

    private static int[][] parseInput(String fileName) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(fileName)));
        Scanner sc = new Scanner(fileContent);

        int n = sc.nextInt();
        int[][] sudoku = new int[n][n];
        for (int iy = 0; iy < n; iy++) {
            for (int ix = 0; ix < n; ix++) {
                sudoku[iy][ix] = sc.nextInt();
            }
        }
        sc.close();

        return sudoku;
    }

    @ParameterizedTest
    @MethodSource("data")
    @Timeout(value = 600, unit = TimeUnit.MILLISECONDS)
    public void testSudoku(String file) {
        this.file = file;

        int[][] sudoku = new int[0][0];
        try {
            sudoku = parseInput(file);
        } catch (IOException e) {
            throw new AssertionError("Couldn't find file: " + file);
        }

        // Copy to prevent students from tampering with the input to pass the tests
        int[][] sudokuCopy = new int[sudoku.length][sudoku[0].length];
        for (int iy = 0; iy < sudoku.length; iy++) {
            for (int ix = 0; ix < sudoku[0].length; ix++) {
                sudokuCopy[iy][ix] = sudoku[iy][ix];
            }
        }

        SudokuVerifier.verifyCorrectness(sudoku, Sudoku.solveProblem(sudokuCopy));
    }
}

class SudokuVerifier {

    // Throws an error if the sudoku solution is not correct
    public static void verifyCorrectness(int[][] sudoku, int[][] solvedSudoku) {
        int n = sudoku.length;
        int sqrt_n = 1;
        while (sqrt_n * sqrt_n < n)
            sqrt_n++;

        // General checks
        if (solvedSudoku.length != n)
            throw new AssertionError("Solution contained " + solvedSudoku.length + " rows, but expected " + n + " rows");
        if (solvedSudoku[0].length != n)
            throw new AssertionError("Solution contained " + solvedSudoku.length + " columns, but expected " + n + " columns");
        for (int iy = 0; iy < n; iy++) {
            for (int ix = 0; ix < n; ix++) {
                if (solvedSudoku[iy][ix] == -1)
                    throw new AssertionError("Solution contained unfilled spot at (" + (ix + 1) + ", " + (iy + 1) + ")");
                if (solvedSudoku[iy][ix] < 1 || solvedSudoku[iy][ix] > n)
                    throw new AssertionError("Solution contained invalid number at (" + (ix + 1) + ", " + (iy + 1) + "): " + solvedSudoku[iy][ix]);
                if (sudoku[iy][ix] != -1 && sudoku[iy][ix] != solvedSudoku[iy][ix])
                    throw new AssertionError("Solution overwrote input digit at (" + (ix + 1) + ", " + (iy + 1) + "): was " + sudoku[iy][ix] + ", is now " + solvedSudoku[iy][ix]);
            }
        }

        // Row check
        for (int iy = 0; iy < n; iy++) {
            Set<Integer> seen = new HashSet<>();
            for (int ix = 0; ix < n; ix++) {
                if (seen.contains(solvedSudoku[iy][ix]))
                    throw new AssertionError("Solution contained at least two " + solvedSudoku[iy][ix] + "'s in row " + (iy + 1));
                seen.add(solvedSudoku[iy][ix]);
            }
        }

        // Column check
        for (int ix = 0; ix < n; ix++) {
            Set<Integer> seen = new HashSet<>();
            for (int iy = 0; iy < n; iy++) {
                if (seen.contains(solvedSudoku[iy][ix]))
                    throw new AssertionError("Solution contained at least two " + solvedSudoku[iy][ix] + "'s in column " + (ix + 1));
                seen.add(solvedSudoku[iy][ix]);
            }
        }

        // Block check
        for (int by = 0; by < n; by += sqrt_n) {
            for (int bx = 0; bx < n; bx += sqrt_n) {
                Set<Integer> seen = new HashSet<>();
                for (int iy = by; iy < by + sqrt_n; iy++) {
                    for (int ix = bx; ix < bx + sqrt_n; ix++) {
                        if (seen.contains(solvedSudoku[iy][ix]))
                            throw new AssertionError("Solution contained at least two " + solvedSudoku[iy][ix] + "'s in block (" + (bx / sqrt_n + 1) + ", " + (by / sqrt_n + 1) + ")");
                        seen.add(solvedSudoku[iy][ix]);
                    }
                }
            }
        }
    }

}