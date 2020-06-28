import java.io.*;
import java.util.*;

public class HillCipher{

    public Boolean validText(String textFile, int radix) throws FileNotFoundException{
        File txtFile = new File(textFile);
        Scanner scanner = new Scanner(txtFile);

        while(scanner.hasNext()){
            String next = scanner.next();
            if(next == " ")
                continue;
            
            if(Integer.parseInt(next) > radix)
                return false;
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

        //if(textArray.size() % blockSize != 0)
        //    textArray = paddText(blockSize, textArray);

        scanner.close();
        return textArray;
    }

    ArrayList paddText(int blockSize, ArrayList text){

        int paddcount = blockSize - (text.size() % blockSize);
        int size = text.size();
        for (int i = size; i < size + paddcount; i++){
            text.add(i, paddcount);
        }

        return text;
    }

    public int[][] readKey(int blockSize, String keyFile) throws FileNotFoundException{

        File file = new File(keyFile);
        int [][] key = new int[blockSize][blockSize];
        Scanner scanner = new Scanner(file);

        for (int i = 0; i < blockSize; i++){
            for (int j = 0; j < blockSize; j++){
                key[i][j] = scanner.nextInt();
            }
        }
        scanner.close();

        return key;
    }

    public void encodeText(int radix, int blockSize, int[][] key, String cipherFile, ArrayList text){

        FileWriter writer;

        try{
            writer = new FileWriter(cipherFile);
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
        
        HillCipher cipher = new HillCipher();
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
            if(!cipher.validText(plainFile, radix)){
                System.out.println("Text values are not allowed to be larger than requested radix, exiting");
                return;
            }
        } catch (FileNotFoundException e){
            System.out.println("The plainfile could not be read, exiting");
            return;
        }


        try{
            text = cipher.readText(blockSize, plainFile);
        } catch (FileNotFoundException e){
            System.out.println("The plainfile could not be read, exiting");
            return;
        }

        try{
            key = cipher.readKey(blockSize, keyFile);
        } catch(FileNotFoundException e) {
            System.out.println("The keyfile could not be read, exiting");
            return;
        }

        cipher.encodeText(radix, blockSize, key, cipherFile, text);
        System.out.println("Text was succesfully encoded and stored inside the requested cipherfile");
    }
}