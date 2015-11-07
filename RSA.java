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
/*	public static void main(String[] args){
	BigInteger test;
BigInteger base = new BigInteger("14",16);
BigInteger exp = new BigInteger("18",16);
BigInteger radix = new BigInteger("25",16);
test = repeatedSquare(base, exp);
System.out.println(test);
test = getExpMod(base, exp,radix);
System.out.println(test);
}*///test encry and decry


    private static void RSAencrypt(StringBuilder mStr, StringBuilder nStr, StringBuilder eStr) {
        BigInteger m = new BigInteger(mStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger e = new BigInteger(eStr.toString(),16);
        BigInteger c = getExpMod(m,e,n);

        //System.out.format("m: %d, n: %d, e: %d\n", m,n,e);

        System.out.println(c.toString(16));
    }

    private static void RSAdecrypt(StringBuilder cStr, StringBuilder nStr,
            StringBuilder dStr){
        BigInteger c = new BigInteger(cStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger d = new BigInteger(dStr.toString(),16);
        //System.out.format("c: %d, n: %d, d: %d\n", c,n,d);

        BigInteger m = getExpMod(c,d,n);


        System.out.println(m.toString(16));
    }

    private static void genRSAkey(int bitSize) {
        BigInteger p = new BigInteger("47");
        BigInteger q = new BigInteger("71");
        BigInteger[] primes = getRndPrime(bitSize);
        p = primes[0];
        q = primes[1];

        BigInteger n = p.multiply(q);
        BigInteger one = new BigInteger("1");
        BigInteger phi_n = p.subtract(one).multiply(q.subtract(one));
        //BigInteger e = new BigInteger("65537");
        BigInteger e = new BigInteger("79");
        BigInteger d = e.modInverse(phi_n);

        //System.out.format("p: %d\n", p);
        //System.out.format("q: %d\n", q);
        //System.out.format("n: %d\n", n);
        //System.out.format("phi: %d\n", phi_n);
        //System.out.format("e: %d\n", e);
        //System.out.format("d: %d\n", d);

        String pKeyStr = "("+e.toString(16)+", "+n.toString(16)+")";
        String sKeyStr = "("+d.toString(16)+", "+n.toString(16)+")";

        System.out.format("Public Key: %s\n", pKeyStr);
        System.out.format("Private Key: %s\n", sKeyStr);
    }

    public static boolean isPrime(BigInteger n) {
        return true;
    }

    private static BigInteger[] getRndPrime(int bits) {
        SecureRandom rnd = new SecureRandom();
        rnd.setSeed(System.currentTimeMillis());
        int shift = rnd.nextInt(bits/2);
        //System.out.format("shift: %d\n", shift);

        BigInteger e = new BigInteger("79");
        BigInteger bigOne = new BigInteger("1");
        BigInteger bigTwo = new BigInteger("2");
        BigInteger upper = bigTwo.pow(bits-shift).subtract(bigOne);
        BigInteger lower = bigTwo.pow(bits-1);

        BigInteger p1 = upper.divide(bigTwo);
        //System.out.format("p1 start: %d\n", p1);
        do {
            p1 = p1.nextProbablePrime();
        } while (p1.gcd(e).intValue() != 1);

        BigInteger p2 = lower.divide(p1);
        do {
            p2 = p2.nextProbablePrime();
        } while(p2.gcd(e).intValue() != 1);

        BigInteger[] primes = new BigInteger[2];
        primes[0] = p1;
        primes[1] = p2;

        return primes;
    }

    private static BigInteger getExpMod(BigInteger base, BigInteger exp, BigInteger mod){
        BigInteger ans = base.modPow(exp, mod);
        return ans;
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
