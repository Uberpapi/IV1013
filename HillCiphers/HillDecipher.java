import java.io.*;
import java.util.*;

public class HillDecipher{

    public Boolean validText(String textFile, int radix) throws FileNotFoundException{
        File txtFile = new File(textFile);
        Scanner scanner = new Scanner(txtFile);

        while(scanner.hasNext()){
            String next = scanner.next();
            if(next == " ")
                continue;
            
            if(Integer.parseInt(next) > radix){
                scanner.close();
                return false;
            }
        }
        scanner.close();
        return true;
    }

    public ArrayList readText(int blockSize, String filename) throws FileNotFoundException{

        File textFile = new File(filename);
        Scanner scanner = new Scanner(textFile);
        ArrayList textArray = new ArrayList();

        while(scanner.hasNext())
            textArray.add(scanner.next());

        scanner.close();
        return textArray;
    }

    public int[][] readKey(int blockSize, int radix, String keyFile) throws FileNotFoundException{
        File file = new File(keyFile);
        int [][] key = new int[blockSize][blockSize];
        Scanner scanner = new Scanner(file);

        for (int i = 0; i < blockSize; i++){
            for (int j = 0; j < blockSize; j++){
                key[i][j] = scanner.nextInt();
            }
        }
        scanner.close();

        MatrixOperations matrix = new MatrixOperations();
        float [][] invertedKey = matrix.getInverse(key);
        int det = matrix.determinant(key, blockSize);

        int invDet = matrix.inverseDeterminant(det, radix);
        if(invDet < 0)
            invDet += radix;
        //matrix.displayMatrix(invertedKey, det);

        int decryptKey[][] = new int[blockSize][blockSize];
        int determinant = (det * invDet);
        for (int i = 0; i < blockSize; i++){
            for (int j = 0; j < blockSize; j++){
                int n = Math.round((invertedKey[i][j] * determinant) % radix);
                if(n < 0)
                    n += radix;
                decryptKey[i][j] = n;
            }
        }
        //matrix.displayMatrix(decryptKey);
        return decryptKey;
    }

    public void decodeText(int radix, int blockSize, int[][] key, String plainFile, ArrayList text){

        FileWriter writer;

        try{
            writer = new FileWriter(plainFile);
        }
        catch (IOException e){
            System.out.println("problem initiating writer");
            return;
        }
        
        PrintWriter printWriter = new PrintWriter(writer);

        for (int i = 0; i < text.size(); i += blockSize){
            for (int j = 0; j < blockSize; j++){
                int count = 0;

                for (int q = 0; q < blockSize; q++){
                    int charValue = Integer.parseInt(String.valueOf(text.get(i + q)));
                    count += charValue * key[j][q];
                }

                String number = String.valueOf(count % radix) + " ";
                printWriter.print(number);
            }
        }

        printWriter.close();

    }

    public static void main(String[] args) {
        
        HillDecipher cipher = new HillDecipher();
        int[][] key;
        ArrayList text;
        int radix = Integer.parseInt(args[0]);
        int blockSize = Integer.parseInt(args[1]);
        String keyFile = args[2];
        String plainFile = args[3];
        String cipherFile = args[4];

        if(radix > 256 || blockSize > 8){
            System.out.println("Program only supports max radix 256 and a blocksize of max 8, exiting");
            return;
        }

        try{
            if(!cipher.validText(cipherFile, radix)){
                System.out.println("Text values are not allowed to be larger than requested radix, exiting");
                return;
            }
        } catch (FileNotFoundException e){
            System.out.println("The cipherfile could not be read, exiting");
            return;
        }


        try{
            text = cipher.readText(blockSize, cipherFile);
        } catch (FileNotFoundException e){
            System.out.println("The cipherfile could not be read, exiting");
            return;
        }

        try{
            key = cipher.readKey(blockSize, radix, keyFile);
        } catch(FileNotFoundException e) {
            System.out.println("The keyfile could not be read, exiting");
            return;
        }

        cipher.decodeText(radix, blockSize, key, plainFile, text);
        System.out.println("Text was succesfully encoded and stored inside the requested cipherfile");
    }
}