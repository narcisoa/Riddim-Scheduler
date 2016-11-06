import java.util.*;

public class RiddimScheduler {

  // INSTANCE VARIABLES

  //n = number of rows and columns in the matrix
  //n = number of matchings between choreographers and time slots
  public int n;

  //initial cost matrix (input of the Hungarian Alg)
  public int[][] costMatrix;

  //contains matrix of -1, 0 and 1
  //-1 = "crossed out" = c and t combination is no longer possible
  //      i.e. that choreographer or that time slot was already assigned
  //0 = could be used as a match
  //1 = best match between c and t
  public int[][] bestMatch;


  /** PRE-PROCESSING STEP: Convert maximization to a minimization problem
    in order for the problem to be solved using the Hungarian Alg
    BY finding the maximum benefit value, subtracting all of the values in the
    matrix from that max, thereby producing a cost matrix (lowest = best)

  Input: n x n matrix
    i (rows) = choreographers
    j (columns) = time slots
    c(i,j) = benefit of that choreographer / time slot combination

  Output: n x n matrix
    c(i,j) = cost of that choreographer / time slot combination
  **/
  public static int[][] maxToMin(int[][] benefitMatrix) {
    int max = maxInMatrix(benefitMatrix);
    int n = benefitMatrix.length;
    int[][] costMatrix = new int[n][n];      //creates an empty cost matrix

    for (int i = 0; i < n; i++) {            //fills the cost matrix
      for (int j = 0; j < n; j++) {
        costMatrix[i][j] = max - benefitMatrix[i][j];
      }
    }
    return costMatrix;
  }

  //PRE-PROCESSING: HELPER FUNCTIONS

  //Prints the Matrix
  public static void printMatrix(int[][] matrix) {
    int numRows = matrix.length;      //REMOVE???? --> instance var?
    int numCols = matrix[0].length;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
  }

  //Returns the maximum integer in an integer matrix
  public static int maxInMatrix(int[][] matrix) {
    int max = Integer.MIN_VALUE;
    int numRows = matrix.length;      //REMOVE????
    int numCols = matrix[0].length;
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        if (matrix[i][j] > max) max = matrix[i][j];
      }
    }
    return max;
  }

  /** INIT
  Input: n x n cost matrix
  Initializes the instance variables for the Hungarian Alg:
    n = num of rows and cols
    costMatrix = inputted cost matrix
    bestMatch = corresponding matrix where 1 indicates best match
  **/
  public void init(int[][] costMatrix) {
    // System.out.println("INIT MEDTHOD CALLED");
    this.n = costMatrix.length;
    this.costMatrix = new int[n][n];         //initializes the cost matrix
    for (int i = 0; i < n; i++) {            //fills it in by copying in
      for (int j = 0; j < n; j++) {           //values from inputted cost matrix
        this.costMatrix[i][j] = costMatrix[i][j];
      }
    }
    this.bestMatch = new int[n][n];         //creates the best match matrix
    for (int i = 0; i < n; i++) {            //fills it in w/ 0s (ie no match)
      for (int j = 0; j < n; j++) {
        this.bestMatch[i][j] = 0;
      }
    }
  }

  /**
  HUNGARIAN ALGORITHM - APPLIED

  Input: n x n matrix
    i (rows) = choreographers
    j (columns) = time slots
    c(i,j) = cost of that choreographer / time slot combination

  Output: ???

  Note: |i| = |j|
    Riddim schedules the same number of time slots as there are choreographers
    thus, in this application, there are the same number of rows and columns

  **/
  public void hungarianAlg(int[][] costMatrix) {
    //PUT ALL IN HERE
    System.out.println("INPUT: Cost Matrix");
    printMatrix(costMatrix);

    System.out.println("0. Init");
    init(costMatrix);
    printMatrix(this.costMatrix);

    System.out.println("1. REDUCE");
    reduce();

    System.out.println("Cost Matrix");
    printMatrix(this.costMatrix);

    System.out.println("2.1. SCAN ROW");
    scanRow();

    System.out.println("Cost Matrix");
    printMatrix(this.costMatrix);

    System.out.println("Best Match Matrix");
    printMatrix(this.bestMatch);

    System.out.print("2.1.1 ALL 0'S CROSSED OUT?: ");
    System.out.println(checkOptimalMatches());
  }

  // METHODS - HUNGARIAN ALGORITHM

  /** PHASE 1: ROW AND COLUMN REDUCTIONS
    Sets the minimum of each row and each column to 0
    Thus, making it easier to 'spot' the best (lowest) cost assignments
    between choreographers and time slots

    Within each row: subtract the minimum from each value
    Within each column: subtract the minimum from each value
  **/

  public void reduce() {
    // System.out.println("REDUCE FUNCTION CALLED");
    // Row Reductions
    for (int i = 0; i < n; i++) {
      int minRow = minInRow(costMatrix[i]);
      for (int j = 0; j < n; j++) {
        costMatrix[i][j] = costMatrix[i][j] - minRow;
      }
    }
    //Column Reductions
    for (int j = 0; j < n; j++) {
      int minCol = minInCol(costMatrix, j);
      for (int i = 0; i < n; i++) {
        costMatrix[i][j] = costMatrix[i][j] - minCol;
      }
    }
  }

  // 1. REDUCE: HELPER FUNCTIONS

  //Returns the minimum integer in the row of a matrix (array)
  public static int minInRow(int[] row) {
    int min = (int)Double.POSITIVE_INFINITY;
    int len = row.length;      //REMOVE????
    for (int i = 0; i < len; i++) {
      if (row[i] < min) min = row[i];
    }
    return min;
  }

  //Returns the minimum integer in the column of a matrix
  public static int minInCol(int[][] matrix, int colIndex) {
    int min = (int)Double.POSITIVE_INFINITY;
    int numRows = matrix.length;      //REMOVE????
    int numCols = matrix[0].length;
    for (int i = 0; i < numRows; i++) {
        if (matrix[i][colIndex] < min) min = matrix[i][colIndex];
    }
    return min;
  }

  /** PHASE 2: ASSIGN


  public void assign() {
    int step = 1;               // start with the first step
    Boolean done = false;
    int x = 10;
    int y = 10;

    // Repeats the algorithm - make assignments and remove unnecessary combos
    // until all necessary assignments have been made (= done)
    while (!done) {

      switch (step) {

        // 1. Row Scanning
        case 1:
          System.out.println("step 1: row scanning");
          scanRow();

          if (checkOptimalMatches()) {
                                // if (all 0s covered w/ lines)
                                // i.e. all potential optimal matches (cost = 0)
                                // have been assigned
                                // OR can't be used (c OR t already assigned)
            step = 3;           //  go to step 3  // skip column scanning
            break;
          }
        // else               // follow regular flow
        //  go to step 2: column scanning

        // 2. Column Scanning
        case 2:
          System.out.println("went to step 2: col scanning!");
          // INSERT COLUMN SCANNING CODE !!!

          // make sure all 0's covered w/ lines --> necessary???
          // continues to step 3: check if all matches are made

        // 3. Check: All Matches Made (are we done)?
        //           i.e. has each choreographer been assigned to a time slot?
        case 3:
          System.out.println("went to step 3 : check if done");

          if (y == 10) {            // if all matches are made, DONE
                                    // exit switch statement and while loop
            done = true;
            break;
          }
          // else                 // need to run through assignment again
                                  // follow regular flow
          //   continue to step 4

        //4. Not all matches made. Thus, update table and re-run assignment
        case 4:
          // INSERT UPDATE TABLE CODE HERE !!!

          // go to step 1
          step = 1;
          break;

      }
    }
  }

  **/

  // 2. ASSIGN: HELPER FUNCTIONS

  /** 2.1 SCAN ROW
  Idea:
  checks each row...
  if there is exactly 1 optimal time slot, t, for that choreographer, c (row)
  makes c-t assignment and removes that time slot option for other c's

  Pseudo Code:
  for each row
    check that row (scan j)
      update how many 0s there are (numZeros)
      upate where that 0 is (zerojIndex)
    if there is exactly one available 0 (1 optimal matching)
      (available = not already removed as an option, or used for an assignment)
      update column @ zerojIndex --> best match matrix
       1. if that is the only 0 in row --> bestMatrix  = 1 (assigned)
       2. else (not best match) --> bestMatrix -= 1 (remove option to match)

   Note:
   don't want to make assignment if there are two optimal matches
   i.e. numZeros !=1 b/c don't know which will be better in the end
  **/
  public void scanRow() {
    int numZeros, zerojIndex, zeroiIndex;

    // for each row
    for (int i = 0; i < n; i++) {
      numZeros = 0;
      zerojIndex = -1;                    //-1: no zero found in row
      zeroiIndex = -1;                    //note: these vars re-init per row

      // checks each entry in that row
      for (int j = 0; j < n; j++) {
        // if cost = 0 (i.e. cheapest matching)
        // AND the c and t are available to be matched
        if ((costMatrix[i][j] == 0) && (bestMatch[i][j] == 0)) {
          numZeros++;                       //counts num of 0s in the row
          zeroiIndex = i;                   //stores location of 0
          zerojIndex = j;
        }
      }
      // if there is exactly one 0 (0 = cheapest/best matching)
      // i.e. that row/choreographer has exactly 1 optimal time slot combination
      if (numZeros == 1) {
         //iIndexInJ iterates through column: zeroJIndex
        for (int iIndexInJ = 0; iIndexInJ < n; iIndexInJ++) {
          //match c w/ t (in best match matrix)
          if (iIndexInJ == zeroiIndex) bestMatch[iIndexInJ][zerojIndex] = 1;
          else { //remove option for other c's, b/c time slot already taken
            bestMatch[iIndexInJ][zerojIndex] -= 1;
          }
        }
      }
      // System.out.println("Best Match. i = " + i);
      // printMatrix(bestMatch);
    }
  }

  /** 2.1.1 Check if all the optimal matches have been assigned

  Idea:
  If all 0s have been covered w/ lines
  i.e. all potential optimal matches (cost = 0)
  have been assigned (1) OR can't be used (c OR t already assigned) (-1,-2)
        bestMatch[i][j] != 0), where 0 means available for matching
  returns true, otherwise returns false

  Implementation:
  If any 0 has not been covered w/ lines
  i.e. there exists an optimal match (cost = 0)
  that hasn't been assigned (best match = 0)
  returns false (b/c not all optimal matches assigned), otherwise returns true
  **/
  public boolean checkOptimalMatches() {
    boolean allMatchesMade = true;
    // go through all of the items in the matrix
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        // if there exists an optimal match AND it's not assigned --> F
        if (costMatrix[i][j] == 0 && bestMatch[i][j] == 0) return false;
      }
    }
    return allMatchesMade;
  }

  /** 2.1 SCAN COLUMN
  Idea:
  checks each column...
  if there is exactly 1 optimal time slot, t, for that choreographer, c (row)
  makes c-t assignment and removes that time slot option for other c's

  Pseudo Code:
  for each row
    check that row (scan j)
      update how many 0s there are (numZeros)
      upate where that 0 is (zerojIndex)
    if there is exactly one available 0 (1 optimal matching)
      (available = not already removed as an option, or used for an assignment)
      update column @ zerojIndex --> best match matrix
       1. if that is the only 0 in row --> bestMatrix  = 1 (assigned)
       2. else (not best match) --> bestMatrix -= 1 (remove option to match)

   Note:
   don't want to make assignment if there are two optimal matches
   i.e. numZeros !=1 b/c don't know which will be better in the end
  **/
  public void scanRow() {
    int numZeros, zerojIndex, zeroiIndex;

    // for each row
    for (int i = 0; i < n; i++) {
      numZeros = 0;
      zerojIndex = -1;                    //-1: no zero found in row
      zeroiIndex = -1;                    //note: these vars re-init per row

      // checks each entry in that row
      for (int j = 0; j < n; j++) {
        // if cost = 0 (i.e. cheapest matching)
        // AND the c and t are available to be matched
        if ((costMatrix[i][j] == 0) && (bestMatch[i][j] == 0)) {
          numZeros++;                       //counts num of 0s in the row
          zeroiIndex = i;                   //stores location of 0
          zerojIndex = j;
        }
      }
      // if there is exactly one 0 (0 = cheapest/best matching)
      // i.e. that row/choreographer has exactly 1 optimal time slot combination
      if (numZeros == 1) {
         //iIndexInJ iterates through column: zeroJIndex
        for (int iIndexInJ = 0; iIndexInJ < n; iIndexInJ++) {
          //match c w/ t (in best match matrix)
          if (iIndexInJ == zeroiIndex) bestMatch[iIndexInJ][zerojIndex] = 1;
          else { //remove option for other c's, b/c time slot already taken
            bestMatch[iIndexInJ][zerojIndex] -= 1;
          }
        }
      }
      // System.out.println("Best Match. i = " + i);
      // printMatrix(bestMatch);
    }
  }

  public static void main(String[] args) {

    //EXAMPLE 1

    // Simple Example - matrix
    int[][] exampleMatrix1 = {{0, 1, 2}, {4, 6, 5}, {10, 9, 8}};
    // printMatrix(exampleMatrix1);

    //Tester Code for individual methods
    // RiddimScheduler example0 = new RiddimScheduler();
    // System.out.println("Example 0:");
    // example0.init(exampleMatrix1);
    // System.out.println("Cost Matrix");
    // printMatrix(example0.costMatrix);
    // example0.reduce();
    // System.out.println("1. Reduce");
    // printMatrix(example0.costMatrix);

    //Tester Code
    // System.out.println("min in row: " + minInRow(exampleMatrix1[1]));
    // System.out.println("min in col 0: " + minInCol(exampleMatrix1, 0));

    int[][] exampleCostMatrix1 = maxToMin(exampleMatrix1);
    // printMatrix(exampleCostMatrix1);

    /**Note to self:
    normal (non-static) methods --> used on objects, called using an object
    static methods --> not ^, called in a program / by class name
    **/

    RiddimScheduler example1 = new RiddimScheduler();
    System.out.println("Example 1:");
    example1.hungarianAlg(exampleCostMatrix1);
    //printMatrix(example1.costMatrix);
    // System.out.println("n: " + example1.n);


    //EXAMPLE 2

    int[][] exampleCostMatrix2 = {{9, 11, 14, 11, 7}, {6, 15, 13, 13, 10},
                                  {12, 13, 6, 8, 8}, {11, 9, 10, 12, 9},
                                  {7, 12, 14, 10, 14}};

    RiddimScheduler example2 = new RiddimScheduler();
    System.out.println("Example 2:");
    example2.hungarianAlg(exampleCostMatrix2);



  }

}
