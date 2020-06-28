import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class PasswordCrack{

    ArrayList<String> dict;
    ArrayList<String> passwords;
    ArrayList<String> salts;
    ArrayList<String> names;
    FileWriter writer;
    PrintWriter printWriter;

    boolean dictRead = false;
    boolean passwordsRead = false;

    public void FetchPasswords(String fileName) throws IOException{
        
        File textFile = new File(fileName);
        Scanner scanner = new Scanner(textFile);
        passwords = new ArrayList();
        salts = new ArrayList();
        names = new ArrayList();

        while(scanner.hasNextLine()){
            String [] words = scanner.nextLine().split(":");
            passwords.add(words[1]);
            salts.add(words[1].substring(0, 2));
            names.add(words[4]);
        }

        passwordsRead = true;
        scanner.close();
    }

    public void FetchDictionary(String fileName) throws IOException{
        File textFile = new File(fileName);
        Scanner scanner = new Scanner(textFile);
        dict = new ArrayList();

        while(scanner.hasNext()){
            dict.add(scanner.next());
        }

        dict.add("0000");
        dict.add("00000");
        dict.add("000000");
        dict.add("0000000");
        dict.add("1234");
        dict.add("12345");
        dict.add("123456");
        dict.add("123456");
        dict.add("1234567");
        dict.add("4321");
        dict.add("54321");
        dict.add("654321");
        dict.add("7654321");
        scanner.close();

        dictRead = true;
    }

    public void CrackPasswords(){

        if(!dictRead || !passwordsRead){
            System.out.println("Inputfiles incorrectly read, can't crack passwords. Exiting");
            return;
        }

        /*
            First we try to mangle the user name 
            to see if we can find the password
        */

        ArrayList<String> mangledDictList = new ArrayList ();
        int index = 0;
        ArrayList<String> foundSalts = new ArrayList();
        ArrayList<String> foundPasswords = new ArrayList();

        for (String user : names) {
            boolean foundPassword = false;
            String salt = salts.get(index);
            String pass = passwords.get(index);
            String[] nameList = user.split(" ");

            for (String name : nameList) {
                ArrayList<String> mangledNameList = MangleWord(name);

                for (String mangledWord : mangledNameList) {
                    String hashedPass = jcrypt.crypt(salt, mangledWord);

                    if(pass.contains(hashedPass)){

                        System.out.println(mangledWord);
                        foundPassword = true;
                        WriteToFile(mangledWord);
                        break;
                    }
                }
                if(foundPassword)
                    break;
            }

            if(foundPassword){
                foundSalts.add(salt);
                foundPasswords.add(pass);
            }

            index++;
        }

        for(String salt : foundSalts)
            salts.remove(salt);
        
        for(String pass : foundPasswords)
            passwords.remove(pass);

        foundSalts.clear();
        foundPasswords.clear();

        /*
            Then we try to mangle common passwords from the
            dictionary to see if we can find the password
        */

        mangledDictList = MangleList(dict);

        for(int i = 0; i < passwords.size(); i++){
            String salt = salts.get(i);
            String pass = passwords.get(i);

            for (String mangledWord : mangledDictList) {
                String hashedPass = jcrypt.crypt(salt, mangledWord);

                if(pass.contains(hashedPass)){

                    System.out.println(mangledWord);
                    foundSalts.add(salt);
                    foundPasswords.add(pass);
                    WriteToFile(mangledWord);
                    break;
                }
            }
        }
        
        for(String salt : foundSalts)
            salts.remove(salt);
    
        for(String pass : foundPasswords)
            passwords.remove(pass);

        foundSalts.clear();
        foundPasswords.clear();

        /*
            Then we try to mangle the mangled words from the
            dictionary to see if we can find the password
            **WARNING** this last check is very time consuming
        */

        for (String word : mangledDictList) {
            ArrayList<String> mangledmangledlist = MangleWord(word);

            for(int i = 0; i < passwords.size(); i++){
                String salt = salts.get(i);
                String pass = passwords.get(i);
    
                for (String mangledWord : mangledmangledlist) {
                    String hashedPass = jcrypt.crypt(salt, mangledWord);
    
                    if(pass.contains(hashedPass)){
                        System.out.println(mangledWord);
                        foundSalts.add(salt);
                        foundPasswords.add(pass);
                        WriteToFile(mangledWord);
                        break;
                    }
                }
            }

            for(String salt : foundSalts)
                salts.remove(salt);
    
            for(String pass : foundPasswords)
                passwords.remove(pass);

            foundSalts.clear();
            foundPasswords.clear();
        }

        printWriter.close();
    }

    ArrayList<String> MangleList(ArrayList<String> list){

        ArrayList<String> mangledList = new ArrayList ();

        for(String word : list){
            ArrayList<String> tempList = MangleWord(word);

            for(String tempWord : tempList)
                mangledList.add(tempWord);
        }

        return mangledList;
    }

    ArrayList<String> MangleWord(String text){
        String[] names = text.split(" ");
        ArrayList<String> userDict = new ArrayList();

        for (String name : names) {
            userDict.add(name);

            for(int val = 0; val < 3; val++){
                userDict.add(PrependChar(name, String.valueOf(val)));
                userDict.add(AppendChar(name, String.valueOf(val)));
            }
            
            userDict.add(PrependChar(name, name.substring(0, 1)));
            userDict.add(AppendChar(name, name.substring(0, 1)));
            userDict.add(PrependChar(name, name.substring(name.length()-1, name.length())));
            userDict.add(AppendChar(name, name.substring(name.length()-1, name.length())));
            userDict.add(DeleteFirstChar(name));
            userDict.add(DeleteLastChar(name));
            userDict.add(ReverseName(name));
            userDict.add(DuplicateName(name));
            userDict.add(ReflectFirst(name));
            userDict.add(ReflectSecond(name));
            userDict.add(UpperCase(name));
            userDict.add(LowerCase(name));
            userDict.add(Capitalize(name));
            userDict.add(Ncapitalize(name));
            userDict.add(ToggleCaseFirst(name));
            userDict.add(ToggleCaseSecond(name));
        }

        return userDict;
    }

    String PrependChar(String name, String val){
        return val + name;
    }

    String AppendChar(String name, String val){
        return name + val;
    }

    String DeleteFirstChar(String name){
        return name.substring(1, name.length()) ;
    }

    String DeleteLastChar(String name){
        return name.substring(0, name.length() - 1);
    }

    String ReverseName(String name){
        StringBuilder sb = new StringBuilder(name);
        return sb.reverse().toString();
    }

    String DuplicateName(String name){
        return name + name;
    }

    String ReflectFirst(String name){
        return ReverseName(name) + name;
    }

    String ReflectSecond(String name){
        return name + ReverseName(name);
    }

    String UpperCase(String name){
        return name.toUpperCase();
    }

    String LowerCase(String name){
        return name.toLowerCase();
    }

    String Capitalize(String name){
        return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    }

    String Ncapitalize(String name){
        return name.substring(0, 1).toLowerCase() + name.substring(1, name.length()).toUpperCase();
    }

    String ToggleCaseFirst(String name){
        String toggledName = "";

        for (int i = 0; i < name.length(); i++){
            String temp = name.substring(i, i+1);

            if(i % 2 == 0)
                temp = temp.toUpperCase();
            else
                temp = temp.toLowerCase();

            toggledName = toggledName + temp;
        }

        return toggledName;
    }

    String ToggleCaseSecond(String name){
        String toggledName = "";

        for (int i = 0; i < name.length(); i++){
            String temp = name.substring(i, i+1);

            if(i % 2 == 1)
                temp = temp.toUpperCase();
            else
                temp = temp.toLowerCase();

            toggledName = toggledName + temp;
        }

        return toggledName;
    }

    void WriteToFile(String text){  
        try{
            writer = new FileWriter("passwd2-plain.txt", true);
        } catch(IOException e){
            System.out.println("Problem initiating writer, cant write to file");
            return;
        }
            printWriter = new PrintWriter(writer);
            printWriter.print(text + "\n");
            printWriter.close();
    }

    public static void main(String[] args) {
        String dictionary = args[0];
        String passwords = args[1];

        PasswordCrack pc = new PasswordCrack();

        try{
            pc.FetchDictionary(dictionary);
        } catch (IOException e){
            System.out.println("Invalid dictionary input");
        }
        
        try{
            pc.FetchPasswords(passwords);
        } catch (IOException e){
            System.out.println("Invalid password input");
        }

        pc.CrackPasswords();
    }
}
