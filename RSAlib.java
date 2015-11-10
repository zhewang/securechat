import java.math.BigInteger;
import java.security.SecureRandom;
import gnu.getopt.Getopt;


public class RSAlib{


    public static String RSAencrypt(StringBuilder mStr, StringBuilder nStr, StringBuilder eStr) {
        BigInteger m = new BigInteger(mStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger e = new BigInteger(eStr.toString(),16);
        BigInteger c = m.modPow(e,n);

        return c.toString(16);
    }

    public static String RSAdecrypt(StringBuilder cStr, StringBuilder nStr,
            StringBuilder dStr){
        BigInteger c = new BigInteger(cStr.toString(),16);
        BigInteger n = new BigInteger(nStr.toString(),16);
        BigInteger d = new BigInteger(dStr.toString(),16);

        BigInteger m = c.modPow(d,n);

        return m.toString(16);
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

}
