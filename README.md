# Assignment 2 Template
***CSE3300 Algorithms for NP-Hard Problems***

This is a template project for Assignment 2, which you can use for local development for *Part 2: Solving*.

The project mimics the setup on WebLab; you can decide to keep all of your work on WebLab and use its group collaboration feature to work together, but we provide you with this template if you prefer to work in IntelliJ. You can create a ***private*** GitHub repo to collaborate with your partner. If we find a *public* GitHub repo with your implementation of the assignment, you will be marked for fraud.

### Setup

You might need to manually mark the `src`-directory as *Sources Root* and the `test`-directory as *Test Sources Root*. You can do so by right-clicking the directories in the Project view on the left side of your screen, and navigating to *Mark Directory As*.

### Contents

The project contains:
- *Five problem classes (`ToyExample`, `Production`, `Sudoku`, `FamilyDinner`, `NQueens`) to write your models in.* \
  You should copy the models that you wrote for Part 1 here. Those you should still develop on WebLab, as that is the only place where you will have access to our hidden `Solver`-implementation.
- *A `Solver`-class to write your solver in.* \
  Here you should develop your own solver, which all five classes above should make use of. Keep in mind the rules your `Solver`-implementation should adhere to, as listed on WebLab!
- *Five test suite classes (`ToyExampleTest`, `ProductionTest`, `SudokuTest`, `FamilyDinnerTest`, `NQueensTest`)* \
  These are test suites you can use to test the correctness and performance of your `Solver`. These tests are set up in the exact same as on WebLab, you do not have to touch or change them. Keep in mind that your computer might be faster or slower than the WebLab servers, so the runtime here might not be representative of the runtime on WebLab. Your code should pass all tests on WebLab; you cannot receive points for passing these tests on your own computer.

### Submissions

To submit your work:
- Copy over the contents of all `solveProblem`-methods to their respective WebLab exercises for both *Part 1: Modelling* and *Part 2: Solving*. Make sure the `solveProblem`-code for each problem is ***exactly*** the same in both parts; we have automatic tests that assert this, and if you have any differences, this could lead to a deduction in points. If you defined any helper methods in the `Solution`-class, copy those over too.
- Copy over your full `Solver`-implementation and paste them at the bottom of each of your WebLab submissions in *Part 2: Solving*. Make sure the implementation of your `Solver` is exactly the same in each of the five exercises; again, we have automatic tests that assert this.

---

Have fun! :)