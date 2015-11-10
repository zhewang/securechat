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

    private static void RSAencrypt(StringBuilder mStr,
                                   StringBuilder nStr,
                                   StringBuilder eStr) {
        BigInteger m = new BigInteger(mStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger e = new BigInteger(eStr.toString(),16);
        BigInteger c = m.modPow(e,n);

        System.out.println(c.toString(16));
    }

    private static void RSAdecrypt(StringBuilder cStr,
                                   StringBuilder nStr,
                                   StringBuilder dStr){
        BigInteger c = new BigInteger(cStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger d = new BigInteger(dStr.toString(),16);
        BigInteger m = c.modPow(d,n);

        System.out.println(m.toString(16));
    }

    private static void genRSAkey(int bitSize) {
        BigInteger[] primes = getRndPrime(bitSize);
        BigInteger p = primes[0];
        BigInteger q = primes[1];

        BigInteger n = p.multiply(q);
        BigInteger one = new BigInteger("1");
        BigInteger phi_n = p.subtract(one).multiply(q.subtract(one));
        BigInteger e = new BigInteger("65537");
        while (phi_n.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        BigInteger d = e.modInverse(phi_n);

        String pKeyStr = "("+e.toString(16)+", "+n.toString(16)+")";
        String sKeyStr = "("+d.toString(16)+", "+n.toString(16)+")";

        System.out.format("Public Key: %s\n", pKeyStr);
        System.out.format("Private Key: %s\n", sKeyStr);
    }

    private static BigInteger[] getRndPrime(int bits) {
        SecureRandom rnd = new SecureRandom();
        rnd.setSeed(System.currentTimeMillis());

        BigInteger p1 = new BigInteger(bits / 2, 100, rnd);
        BigInteger p2 = new BigInteger(bits / 2, 100, rnd);

        BigInteger[] primes = new BigInteger[2];
        primes[0] = p1;
        primes[1] = p2;

        return primes;
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

        String useage =
            "    -h                     List all command line options\n" +
            "    -k                     Generate a public/private key pair\n" +
            "    -b <bit_size>          Specify the key size\n" +
            "    -e <key>               Specify the key used to encrypt the input\n" +
            "    -d <key>               Specify the key used to decrypt the input\n" +
            "    -n <modulus>           Specify the modulus used to encrypt/decrypt\n" +
            "    -i <input_value>       Specify the input value\n";

        System.err.println(useage);
        System.exit(exitStatus);

    }

}
