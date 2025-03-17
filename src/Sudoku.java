import java.util.*;
import java.util.stream.*;

// Do not copy the entire class to WebLab, only the contents of the "solveProblem"-method
// On WebLab, your class should always just be called "Solution"
class Sudoku {
    // Class representing a position in the Sudoku
    public static class Pos{
        public final int x;
        public final int y;

        public Pos(int x, int y){
            this.x = x;
            this.y = y;
        }

        public boolean isInSquare(int x1, int y1, int x2, int y2){
            return (x >= x1) && (x < x2) && (y >= y1) && (y < y2);
        }
    }


    // Generates a variable that ranges from 1 to n (inclusive)
    public static Solver.Variable genRangeVariable(int n){
        return new Solver.Variable(IntStream.rangeClosed(1, n)
                .boxed()
                .collect(Collectors.toList()));
    }

    // Generates a variable that has only one possible value
    public static Solver.Variable genConstVariable(int n){
        return new Solver.Variable(List.of(n));
    }

    public static Pos getPosFromIndex(int i, int size){
        return new Pos(i/size, i%size);
    }

    public static int getIndexFromPos(Pos pos, int size){
        return pos.x*size + pos.y;
    }

    public static List<Solver.Variable> findVariables(int[][] sudoku){
        int n = sudoku.length;
        List<Solver.Variable> result = new ArrayList<>(n*n);

        for(int x = 0; x < n; x++){
            for(int y = 0; y < n; y++){
                int value = sudoku[x][y];
                Solver.Variable v = value <= 0 ? genRangeVariable(n) : genConstVariable(value);
                result.add(v);
            }
        }
        return result;
    }

    public static List<Solver.Variable> getLine(List<Solver.Variable> variables, int size, int index, boolean column){
        List<Solver.Variable> result = new ArrayList<>(size);
        for(int i = 0; i < variables.size(); i++){
            int value = column ? getPosFromIndex(i, size).x : getPosFromIndex(i, size).y;
            if(value == index){
                result.add(variables.get(i));
            }
        }
        return result;
    }

    public static List<Solver.Variable> getSubsquare(List<Solver.Variable> variables, int size, int index){
        List<Solver.Variable> result = new ArrayList<>(size*size);
        int x1 = (index / size) * size;
        int y1 = (index % size) * size;

        for(int i = 0; i < variables.size(); i++){
            if(getPosFromIndex(i, size*size).isInSquare(x1, y1, x1+size, y1+size)){
                result.add(variables.get(i));
            }
        }
        return result;
    }

    /**
     * Returns the filled in sudoku grid.
     *
     * @param sudoku The partially filled in sudoku grid. Unfilled positions are marked with -1. Always either of size 9x9, 16x16 or 25x25.
     * @return The fully filled in sudoku grid.
     */
    public static int[][] solveProblem(int[][] sudoku) {
        int n = sudoku.length;
        int sqrt_n = 1;
        while (sqrt_n * sqrt_n < n)
            sqrt_n++;

        List<Solver.Variable> variables = findVariables(sudoku);

        List<Solver.Constraint> constraints = new ArrayList<>();
        for(int i = 0; i < n; i++){
            List<Solver.Variable> row = getLine(variables, n, i, false);
            List<Solver.Variable> column = getLine(variables, n, i, true);
            List<Solver.Variable> square = getSubsquare(variables, sqrt_n, i);

            constraints.add(new Solver.AllDiffConstraint(row.toArray(new Solver.Variable[0])));
            constraints.add(new Solver.AllDiffConstraint(column.toArray(new Solver.Variable[0])));
            constraints.add(new Solver.AllDiffConstraint(square.toArray(new Solver.Variable[0])));
        }

        // Use solver
        Solver solver = new Solver(
                variables.toArray(new Solver.Variable[0]),
                constraints.toArray(new Solver.Constraint[0])
        );
        int[] result = solver.findOneSolution();
        if(result == null)
            return null;

        int[][] solution = new int[n][n];

        for(int i = 0; i < result.length; i++){
            Pos p = getPosFromIndex(i, n);
            solution[p.x][p.y] = result[i];
        }

        return solution;
    }
}
