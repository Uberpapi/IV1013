import java.io.*;
import java.util.Random;

public class HillKeys {

    public int[][] createKey(int radix, int blockSize) {

        int[][] randKey = new int[blockSize][blockSize];
        MatrixOperations matrix = new MatrixOperations();
        Random r = new Random();

        while (true) {

            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    randKey[i][j] = r.nextInt(radix);
                }
            }

            if (matrix.isInvertable(randKey, radix))
                break;

            randKey = new int[blockSize][blockSize];
        }

        return randKey;
    }

    public boolean printKey(String keyFile, int blockSize, int[][] keyMatrix) {

        try {
            FileWriter writer = new FileWriter(keyFile);
            PrintWriter printWriter = new PrintWriter(writer);

            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    String number = keyMatrix[i][j] + " ";
                    printWriter.print(number);
                }
                printWriter.print("\n");
            }
            printWriter.close();

        } catch (IOException e) {
            System.out.println("Problem writing to keyfile, exiting");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        int radix = Integer.parseInt(args[0]);
        int blockSize = Integer.parseInt(args[1]);
        String keyFile = args[2];

        if (radix > 256 || blockSize > 8) {
            System.out.println("Program only supports max radix 256 and a blocksize of max 8, exiting");
            return;
        }

        HillKeys key = new HillKeys();
        int[][] keyMatrix = key.createKey(radix, blockSize);

        if (key.printKey(keyFile, blockSize, keyMatrix))
            System.out.println("Key was succesfully created");

    }

}