import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Hidenc {

    Cipher cipher;
    byte[] input;
    byte[] hashedInput;
    byte[] b_key;
    byte[] h_key;
    byte[] ctr;
    byte[] template;
    boolean isCtr;

    void ChooseMode() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{

        if(!isCtr){

            SecretKeySpec s_key = new SecretKeySpec(b_key, "AES");
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, s_key);

        }else{
            SecretKeySpec s_key = new SecretKeySpec(b_key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ctr);
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, s_key, iv);

        }

    }

    void EncryptData( byte [] dataBlob, int offset)throws IllegalBlockSizeException, BadPaddingException {
        byte [] encryptedBlob = cipher.doFinal(dataBlob);
        int count = 0;

        for (int i = offset; i < offset + encryptedBlob.length; i++){
            template[i] = encryptedBlob[count];
            count++;
        }
    }

    byte [] OpenFile( String fileName){

        try{

            File newFile = new File(fileName);
            FileInputStream fis = new FileInputStream(newFile);
            byte [] byteFile = new byte[(int)newFile.length()];

            try{

                byteFile = fis.readAllBytes();
                fis.close();
                return byteFile;    

            }catch( IOException error){

                System.out.println("Cant read bytes from inputfile, exiting");
                fis.close();
                return null;

            }
        }catch( IOException error){

            System.out.println("Invalid inputfile, exiting");
            return null;

        }

    }

    byte [] HashValue( byte[] val){
        byte [] result = new byte [val.length];
        try{
             MessageDigest md = MessageDigest.getInstance("MD5");
            result = md.digest(val);

            //BigInteger no = new BigInteger(1, result);
            //String hash = no.toString(16);
            //System.out.println(hash);
        }
        catch( NoSuchAlgorithmException exception){
            System.out.println("The hash algorithm is incorrect");
        }

        return result;
    }

    byte [] InsertData(){

        //hashedInput = HashValue(input);
        byte[] dataBlob = new byte[h_key.length + input.length + h_key.length + hashedInput.length];
        int i = 0;
        

        for (byte b : h_key) {
            dataBlob[i] = b;
            i++;
        }

        for (byte b : input) {
            dataBlob[i] = b;
            i++;
        }

        for (byte b : h_key) {
            dataBlob[i] = b;
            i++;
        }

        for (byte b : hashedInput) {
            dataBlob[i] = b;
            i++;
        }

        return dataBlob;
    }

    void WriteToFile( String outputFile){

        try{
            FileOutputStream fos = new FileOutputStream(outputFile);
            try{
                fos.write(template);
            } catch(IOException e){
                System.out.println("Failed to write to file");
                fos.close();
                return;
            }
            fos.close();
        }catch(IOException e){
            System.out.println("Failed to write to file");
            return;
        }
    }

    byte [] HexStringToByteArray( String s){

        byte[] array = new byte[s.length() / 2];

        for (int i = 0; i < array.length; i++){
            int count = i * 2;
            int val = Integer.parseInt(s.substring(count, count + 2), 16);
            array[i] = (byte) val;
        }

        return array;
    }

    public static void main( String[] args){

        String inputKey = "";
        String ctr = "";
        String outputFile = "";
        int offset = -1;
        int size = 0;

        Hidenc hc = new Hidenc();

        for (String str : args) {
            String [] strings = str.split("=");

            if(strings[0].contains("key")){
                inputKey = strings[1];
            }

            if(strings[0].contains("ctr")){
                ctr = strings[1];
            }   

            if(strings[0].contains("offset")){
                offset = Integer.parseInt(strings[1]);
            }

            if(strings[0].contains("input")){
                hc.input = hc.OpenFile(strings[1]);
            }

            if(strings[0].contains("output")){
                outputFile = strings[1];
            }

            if(strings[0].contains("template")){
                hc.template = hc.OpenFile(strings[1]);
            }

            if(strings[0].contains("size")){
                size = Integer.parseInt(strings[1]);
            }

        }

        if(hc.template == null && size == -1)
        {
            System.out.println("A template or a size has to be specified to proceed, exiting");
            return;
        }

        if(hc.input == null || inputKey == ""){
            System.out.println("Incorrect inputkey or file");
            return;
        }

        /*
            Create template of designated size
            if one is not provided
        */

        if(hc.template == null){
            hc.template = new byte[size];
            ThreadLocalRandom.current().nextBytes(hc.template);
        }

        hc.b_key = hc.HexStringToByteArray(inputKey);
        hc.h_key = hc.HashValue(hc.b_key);
        hc.hashedInput = hc.HashValue(hc.input);
        
        if(ctr != ""){

            String [] strings = ctr.split("=");
            hc.isCtr = strings[0].contains("ctr");
            hc.ctr = hc.HexStringToByteArray(strings[1]);
        }

        byte [] dataBlob = hc.InsertData();

        if(offset == -1)
            offset = ThreadLocalRandom.current().nextInt(0, hc.template.length - dataBlob.length);

        if(offset + dataBlob.length > hc.template.length){
            System.out.println("Offset set incorrectly, adjusting to place the data at the end of the file");
            offset = hc.template.length - dataBlob.length - 1;
        }

        try{
            hc.ChooseMode();
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException error){
            System.out.println("Problem initiating mode, exiting");
            return;
        }

        try{
            hc.EncryptData(dataBlob, offset);
        } catch (IllegalBlockSizeException | BadPaddingException error) {
            System.out.println("Problem Encrypting Data, exiting");
            return;
        }

        hc.WriteToFile(outputFile);

    }
}


