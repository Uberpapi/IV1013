import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Scanner;


public class Hiddec {

    Cipher cipher;
    byte[] input;
    byte[] b_key;
    byte[] h_key;
    byte[] ctr;
    boolean isCtr;

    void ChooseMode() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{

        if(!isCtr){
            SecretKeySpec s_key = new SecretKeySpec(b_key, "AES");
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, s_key);

        }else{
            SecretKeySpec s_key = new SecretKeySpec(b_key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ctr);
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, s_key, iv);
        }
    }

    void OpenFile(String fileName){
        try{
            File newFile = new File(fileName);
            FileInputStream fis = new FileInputStream(newFile);
            input = new byte[(int)newFile.length()];

            try{
                input = fis.readAllBytes();
                //fis.read(input);
            }catch(IOException error){
                System.out.println("Cant read bytes from inputfile, exiting");
                fis.close();
                return;
            }
            fis.close();
        }catch(IOException error){
            System.out.println("Invalid inputfile, exiting");
            return;
        }

    }

    byte [] HashValue(byte[] val){
        byte [] result = new byte [val.length];
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            result = md.digest(val);

            //BigInteger no = new BigInteger(1, result);
            //String hash = no.toString(16);
            //System.out.println(hash);
        }
        catch(NoSuchAlgorithmException exception){
            System.out.println("The hash algorithm is incorrect");
        }

        return result;
    }

    byte [] Run(){
            int index = GetIndex(input, 0);
            int endIndex = GetIndex(input, index + 16);

            if(index == -1 || endIndex == -1){
                System.out.println("Could not match key in data, exiting");
                System.exit(0);
            }

            byte[] data = Arrays.copyOfRange(input, index + 16, endIndex);
            data = cipher.update(data);
            byte[] hashedData = Arrays.copyOfRange(input, endIndex + 16, endIndex + 32);
            hashedData = cipher.update(hashedData);

            if(CompareByteArrays(HashValue(data), hashedData))
                return data;
            else{
                System.out.println("Bad data :(");
                return null;
            }
    }

    void WriteToFile(byte[] data, String outputFile){
        Writer writer;
        System.out.println("Writing to " + outputFile);
        try{
            writer = new FileWriter(outputFile);
        } catch(IOException e){
            System.out.println("Problem initiating writer, cant write to file");
            return;
        }
        String dataString = new String(data, StandardCharsets.UTF_8);
        PrintWriter printWriter = new PrintWriter(writer);
        printWriter.print(dataString);
        printWriter.close();

    }

    boolean CompareByteArrays(byte[] array1, byte[] array2){

        for (int i = 0; i < array1.length; i++){

            if(array1[i] != array2[i]){
                return false;
            }
            
        }
        //System.out.println("Found key :D " + array2);
        return true;
    }

    int GetIndex(byte[] array, int startIndex){
        int index = startIndex;

        while(array.length > index){    
            byte [] info = Arrays.copyOfRange(array, index, index + 16);
            info = cipher.update(info);

            if(CompareByteArrays(info, h_key))
                return index;

            index += 16;
        }

        return -1;
    }

    byte [] HexStringToByteArray(String s){

        byte[] array = new byte[s.length() / 2];

        for (int i = 0; i < array.length; i++){
            int count = i * 2;
            int val = Integer.parseInt(s.substring(count, count + 2), 16);
            array[i] = (byte) val;
        }

        return array;
    }

    public static void main(String[] args){

        String inputKey = "";
        String ctr = "";
        String inputFile = "";
        String outputFile = "";

        if(args.length == 3){
            inputKey = args[0];
            inputFile = args[1];
            outputFile = args[2].split("=")[1];
        }
        else if(args.length == 4){
            inputKey = args[0];
            ctr = args[1];
            inputFile = args[2];
            outputFile = args[3].split("=")[1];
        }
        else{
            System.out.println("Invalid amount of inputs");
            return;
        }

        Hiddec hc = new Hiddec();
        hc.b_key = hc.HexStringToByteArray(inputKey.split("=")[1]);
        hc.h_key = hc.HashValue(hc.b_key);
        
        if(ctr != ""){
            String [] strings = ctr.split("=");
            hc.isCtr = strings[0].contains("ctr");
            hc.ctr = hc.HexStringToByteArray(strings[1]);
        }

        hc.OpenFile(inputFile.split("=")[1]);
        try{
            hc.ChooseMode();
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException error){
            System.out.println("Problem initiating mode, exiting");
            return;
        }

        byte [] data = hc.Run();
        hc.WriteToFile(data, outputFile);

    }
}
