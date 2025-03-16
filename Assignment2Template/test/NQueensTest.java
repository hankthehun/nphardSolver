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
public class NQueensTest {

    public int n;

    private long time = 0;

    public static Stream<Arguments> data() {
        List<Arguments> ns = new ArrayList<>();
        for (int n = 3; n <= 12; n++) {
            ns.add(Arguments.of(n));
        }

        return ns.stream();
    }

    @BeforeEach
    public void setUp() {
        time = System.currentTimeMillis();
    }

    @AfterEach
    public void tearDown() {
        long duration = System.currentTimeMillis() - time;
        System.out.println("Test '[n = " + n + "]' took " + duration + "ms");
    }

    int[] expectedOutputs = {-1, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596};

    @ParameterizedTest
    @MethodSource("data")
    @Timeout(value = 1100, unit = TimeUnit.MILLISECONDS)
    public void nQueensTest(int n) {
        this.n = n;

        int actual = NQueens.solveProblem(n);
        int expected = expectedOutputs[n];
        if (actual != expected)
            throw new AssertionError("n = " + n + ": expected " + expected + ", but got " + actual);
    }

    @Test
    @Timeout(value = 5100, unit = TimeUnit.MILLISECONDS)
    public void nQueensTestHmmYeahThatsALargeNumber() {
        this.n = 13;

        int actual = NQueens.solveProblem(n);
        int expected = expectedOutputs[n];
        if (actual != expected)
            throw new AssertionError("n = " + n + ": expected " + expected + ", but got " + actual);
    }

    @Test
    @Timeout(value = 15100, unit = TimeUnit.MILLISECONDS)
    public void nQueensTestOhWowThatsAReallyLargeNumber() {
        this.n = 14;

        int actual = NQueens.solveProblem(n);
        int expected = expectedOutputs[n];
        if (actual != expected)
            throw new AssertionError("n = " + n + ": expected " + expected + ", but got " + actual);
    }

}