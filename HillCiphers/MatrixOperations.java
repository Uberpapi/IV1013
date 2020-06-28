
/*
 I Found this code at the following website https://www.geeksforgeeks.org/adjoint-inverse-matrix/
*/

// Java program to find adjoint and inverse of a matrix 
public class MatrixOperations {

    // Function to get cofactor of A[p][q] in temp[][]. n is current
    // dimension of A[][]
    public void getCofactor(int A[][], int temp[][], int p, int q, int n) {
        int i = 0, j = 0;

        // Looping for each element of the matrix
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // Copying into temporary matrix only those element
                // which are not in given row and column
                if (row != p && col != q) {
                    temp[i][j++] = A[row][col];

                    // Row is filled, so increase row index and
                    // reset col index
                    if (j == n - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }

    public int inverseDeterminant(int det, int radix){

        int count = 1;

        while (count < radix){
            int number = (det * count) % radix;
            if(number < 0)
                number += radix;
            //System.out.println(number);
            if(number == 1)
                return count;
            
            count++;
        }
        System.out.println("No inverse determinant was found, unable to decrypt");
        return 0;
    }

    /*
     * Recursive function for finding determinant of matrix. n is current dimension
     * of A[][].
     */

    public int determinant(int A[][], int n) {
        int D = 0; // Initialize result

        // Base case : if matrix contains single element
        if (n == 1)
            return A[0][0];

        int[][] temp = new int[n][n]; // To store cofactors

        int sign = 1; // To store sign multiplier

        // Iterate for each element of first row
        for (int f = 0; f < n; f++) {
            // Getting Cofactor of A[0][f]
            getCofactor(A, temp, 0, f, n);
            D += sign * A[0][f] * determinant(temp, n - 1);

            // terms are to be added with alternate sign
            sign = -sign;
        }

        return D;
    }

    // Function to get adjoint of A[N][N] in adj[N][N].
    public void adjoint(int A[][], int[][] adj) {

        int N = A.length;

        if (N == 1) {
            adj[0][0] = 1;
            return;
        }

        // temp is used to store cofactors of A[][]
        int sign = 1;
        int[][] temp = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // Get cofactor of A[i][j]
                getCofactor(A, temp, i, j, N);

                // sign of adj[j][i] positive if sum of row
                // and column indexes is even.
                sign = ((i + j) % 2 == 0) ? 1 : -1;

                // Interchanging rows and columns to get the
                // transpose of the cofactor matrix
                adj[j][i] = (sign) * (determinant(temp, N - 1));
            }
        }
    }

    // Function to calculate and store inverse, returns false if
    // matrix is singular
    public float[][] getInverse(int A[][]) {
        // Find determinant of A[][]
        int N = A.length;
        int det = determinant(A, N);

        // Find adjoint
        int[][] adj = new int[N][N];
        float[][] inverse = new float[N][N];
        adjoint(A, adj);

        // Find Inverse using formula "inverse(A) = adj(A)/det(A)"
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                inverse[i][j] = adj[i][j] / (float) det;

        return inverse;
    }

    public boolean isInvertable(int A[][], int radix) {

        int det = determinant(A, A.length);
        int offSet = radix % 2;
        int modDet = det % 2;
        if(modDet < 0)
            modDet += 2;

        if (det == 0 || offSet == modDet) {
            //System.out.print("Matrix is not invertable");
            return false;
        }

        return true;
    }

    public void displayMatrix(float[][] matrix, int det){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length;j++){
                System.out.print(matrix[i][j] * det + " ");
            }
            System.out.print("\n");
        }
    }

    public void displayMatrix(int[][] matrix, int det){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length;j++){
                System.out.print(matrix[i][j] * det  + " ");
            }
            System.out.print("\n");
        }
    }


    public void displayMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length;j++){
                System.out.print(matrix[i][j]  + " ");
            }
            System.out.print("\n");
        }
    }

    // Generic function to display the matrix. We use it to display
    // both adjoin and inverse. adjoin is integer matrix and inverse
    // is a float.

    /*
     * // Driver program public static void main(String[] args) { int A[][] = { { 5,
     * -2, 2, 7 }, { 1, 0, 0, 3 }, { -3, 1, 5, 0 }, { 3, -1, -9, 4 } };
     * 
     * int[][] adj = new int[N][N]; // To store adjoint of A[][]
     * 
     * float[][] inv = new float[N][N]; // To store inverse of A[][]
     * 
     * System.out.print("Input matrix is :\n"); display(A);
     * 
     * System.out.print("\nThe Adjoint is :\n"); adjoint(A, adj); display(adj);
     * 
     * System.out.print("\nThe Inverse is :\n"); if (inverse(A, inv)) display(inv);
     * 
     * }
     */
}

// This code is contributed by Rajput-Ji