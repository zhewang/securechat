import java.math.BigInteger;
import java.security.SecureRandom;
import gnu.getopt.Getopt;


public class RSA{

    public static void main(String[] args){

        StringBuilder kStr = new StringBuilder();
        StringBuilder bitSizeStr = new StringBuilder();
        StringBuilder nStr = new StringBuilder();
        StringBuilder dStr = new StringBuilder();
        StringBuilder eStr = new StringBuilder();
        StringBuilder m = new StringBuilder();

        pcl(args, kStr, bitSizeStr, nStr, dStr, eStr,m);

        if(kStr.toString().equalsIgnoreCase("k")) {
            if(!bitSizeStr.toString().equalsIgnoreCase("")){
                genRSAkey(Integer.parseInt(bitSizeStr.toString()));
            } else {
                genRSAkey(1024);
            }
        }

        if(!eStr.toString().equalsIgnoreCase("")){
            RSAencrypt(m, nStr, eStr);
        }

        if(!dStr.toString().equalsIgnoreCase("")){
            RSAdecrypt(m, nStr, dStr);
        }


    }



    private static void RSAencrypt(StringBuilder m, StringBuilder nStr, StringBuilder eStr) {
        // TODO Auto-generated method stub
    }

    private static void RSAdecrypt(StringBuilder cStr, StringBuilder nStr,
            StringBuilder dStr){
        // TODO Auto-generated method stub
    }

    private static void genRSAkey(int bitSize) {
        // TODO Auto-generated method stub
        System.out.println("generate key");
        System.out.println(bitSize);
    }


    /**
     * This function Processes the Command Line Arguments.
     */
    private static void pcl(String[] args, StringBuilder kStr,
            StringBuilder bitSizeStr,
            StringBuilder nStr, StringBuilder dStr, StringBuilder eStr,
            StringBuilder m) {
        /*
         * http://www.urbanophile.com/arenn/hacking/getopt/gnu.getopt.Getopt.html
         */
        Getopt g = new Getopt("Chat Program", args, "hke:d:b:n:i:");
        int c;
        String arg;
        while ((c = g.getopt()) != -1){
            switch(c){
                case 'i':
                    arg = g.getOptarg();
                    m.append(arg);
                    break;
                case 'e':
                    arg = g.getOptarg();
                    eStr.append(arg);
                    break;
                case 'n':
                    arg = g.getOptarg();
                    nStr.append(arg);
                    break;
                case 'd':
                    arg = g.getOptarg();
                    dStr.append(arg);
                    break;
                case 'k':
                    kStr.append('k');
                    break;
                case 'b':
                    arg = g.getOptarg();
                    bitSizeStr.append(arg);
                    break;
                case 'h':
                    callUsage(0);
                case '?':
                    break; // getopt() already printed an error
                default:
                    break;
            }
        }
    }

    private static void callUsage(int exitStatus) {

        String useage = "";

        System.err.println(useage);
        System.exit(exitStatus);

    }


}
