
public class countbit {


    static int CompareStrings(String s1, String s2){
        
        if(s1.length() != s2.length())
        {
            System.out.println("Strings of different length, exiting");
            return 0;
        }
        int sameBits = 0;
        for (int i = 0; i < s1.length(); i++){
            sameBits += CompareChars(s1.charAt(i), s2.charAt(i));
        }

        return sameBits;
    }

    static int CompareChars(char c1, char c2){
        String s1 = CharToBinary(c1);
        String s2 = CharToBinary(c2);
        int count = 0;

        for (int i = 0; i < 4; i++){
            if(s1.charAt(i) == s2.charAt(i))
                count++;
        }

        return count;
    }

    static String CharToBinary(char s1){
        if(s1 == '0')
            return "0000";

        if(s1 == '1')
            return "0001";
        
        if(s1 == '2')
            return "0010";

        if(s1 == '3')
            return "0011";

        if(s1 == '4')
            return "0100";

        if(s1 == '5')
            return "0101";

        if(s1 == '6')
            return "0110";

        if(s1 == '7')
            return "0111";
        
        if(s1 == '8')
            return "1000";
        
        if(s1 == '9')
            return "1001";
        
        if(s1 == 'a')
            return "1010";
        
        if(s1 == 'b')
            return "1011";
        
        if(s1 == 'c')
            return "1100";

        if(s1 == 'd')
            return "1101";

        if(s1 == 'e')
            return "1110";

        if(s1 == 'f')
            return "1111";

        System.out.println("Unrecognized Character");
        return " ";

    }
    
    public static void main(String[] args) {

        String md5new = "07cf6ef674fe522f97920e574315d8fe";
        String md5old = "aaf4d2d0e824f0ea8005054bc7f01e84";
        String sha256new = "a343151c6ace567135859039edfa327dc87167b269188c14a531985910449cde";
        String sha256old = "9e3e66fba5d87bc9290a07d199bbe40433e637e40855c7bcbe69c8cd4ebf915e";

        int a = CompareStrings(md5new, md5old);
        int b = CompareStrings(sha256new, sha256old);

        System.out.println(a + " bits are the same of md5new and md5old");
        System.out.println(b + " bits are the same of sha256new and sha256old");

    }
}