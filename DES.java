import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.BitSet;
import java.security.SecureRandom;
import java.lang.Math;
import java.math.BigInteger;

import gnu.getopt.Getopt;

// TODO: key expansion
// TODO: f function
// TODO: CBC mode
// TODO: Debug Decryption


public class DES {

    /**
     * S-Box for Initial Permutation
     */
	private static int[] IP = {
		58, 50, 42, 34, 26, 18, 10, 2
		, 60, 52, 44, 36, 28, 20, 12, 4
		, 62, 54, 46, 38, 30, 22, 14, 6
		, 64, 56, 48, 40, 32, 24, 16, 8
		, 57, 49, 41, 33, 25, 17, 9, 1
		, 59, 51, 43, 35, 27, 19, 11, 3
		, 61, 53, 45, 37, 29, 21, 13, 5
		, 63, 55, 47, 39, 31, 23, 15, 7
	};

    /**
     * S-Box for Final Permutation
     */
	private static int[] FP = {
		40, 8, 48, 16, 56, 24, 64, 32
		, 39, 7, 47, 15, 55, 23, 63, 31
		, 38, 6, 46, 14, 54, 22, 62, 30
		, 37, 5, 45, 13, 53, 21, 61, 29
		, 36, 4, 44, 12, 52, 20, 60, 28
		, 35, 3, 43, 11, 51, 19, 59, 27
		, 34, 2, 42, 10, 50, 18, 58, 26
		, 33, 1, 41, 9, 49, 17, 57, 25
	};

    public static void main(String[] args) {

        StringBuilder inputFile = new StringBuilder();
        StringBuilder outputFile = new StringBuilder();
        StringBuilder keyStr = new StringBuilder();
        StringBuilder encrypt = new StringBuilder();

        pcl(args, inputFile, outputFile, keyStr, encrypt);

        if(keyStr.toString() != "" && encrypt.toString().equals("e")){
            encrypt(keyStr, inputFile, outputFile);
        } else if(keyStr.toString() != "" && encrypt.toString().equals("d")){
            decrypt(keyStr, inputFile, outputFile);
        }
    }

    //public static void main(String[] args){
        //BitSet bset = StringtoBitSet("BC6D211577A50B74");
        //System.out.println(BitSettoAscii(bset));
    //}

    //public static void main(String[] args){
        //BitSet plainBlock = StringtoBitSet("bbbbbbb");//generating 56 bits, no effect for now, waiting for DESf to be implemented
        //BitSet[] keys = new BitSet[2];
        //keys[0] = plainBlock; keys[1] = plainBlock;
        //String cipher = feistelNetwork(1,"aaaaaaaa",keys);//testing feistelNetwork
        //System.out.println(cipher);
    //}

    private static void decrypt(StringBuilder keyStr, StringBuilder inputFile,
            StringBuilder outputFile) {
        try {
            PrintWriter writer = new PrintWriter(outputFile.toString(), "UTF-8");
            List<String> lines = Files.readAllLines(Paths.get(inputFile.toString()), Charset.defaultCharset());

            String IVStr = lines.get(0);
            lines.remove(0);
            BitSet lastCBC = StringtoBitSet(IVStr);

            BitSet decrypted;
            for (String line : lines) {
                if(line.length() != 16) {
                    throw new IllegalArgumentException("Decrypt block size should be 64 bits.");
                }
                BitSet block = HextoBitSet(line);

                decrypted = DES_decrypt(block, keyStr);
                //decrypted.xor(lastCBC);
                lastCBC = block;

                writer.print(BitSettoAscii(decrypted));
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * DES encryption
     * @param block One block of the cyphertext. It should be 64 bits as the requirement
     * @param keyStr Decryption key
     */
    private static BitSet DES_decrypt(BitSet block, StringBuilder keyStr) {
        BitSet[] keys = keyExpansion(keyStr, "d");

        permutation(block, DES.IP); // Initial permutation
        //BitSet result = feistelNetwork(16, block, keys);
        BitSet result = block;
        permutation(result, DES.FP); // Final permutation

        return result;
    }

    //for debugging, transform a bitset to hex formation
    private static String BitSettoHex(BitSet block){
        StringBuilder hex = new StringBuilder();
        for(int k = 0; k < block.toByteArray().length; k++){
            String result = String.format("%02X",block.toByteArray()[k]);
            hex.append(result);
        }
        return hex.toString();
    }
    //for debugging, transform a bitset to ascii formation
    private static String BitSettoAscii(BitSet block){
        byte[] b = block.toByteArray();
        String str = new String(b);
        return str;
    }

    // Transform Hex string to BitSet
    private static BitSet HextoBitSet(String input){
        //choose input length carefully so that it fits in 64 bits
        BigInteger bigint = new BigInteger(input, 16);
        byte[] content = Arrays.copyOfRange(bigint.toByteArray(), 1, bigint.toByteArray().length);
        BitSet bits = BitSet.valueOf(content);
        return bits;
    }

    //StringtoBitset: transforms ascii text to bits representation
    //to be used in transforming a plaintext block
    private static BitSet StringtoBitSet(String input){
        //choose input length carefully so that it fits in 64 bits
        byte[] bytes = input.getBytes();
        BitSet plainBlock = new BitSet(64);
        plainBlock.clear();
        int i = 0;
        for(byte b:bytes){
            //System.out.println(b);
            int j = 0;
            while(j < 8){
                if(b%2 == 1)plainBlock.set(i);
                i++;j++;
                b >>= 1;
            }
        }

        return plainBlock;
    }

    //DESf: the f function used by feistel Network
    //todo: now it just returns all 0
    private static BitSet DESf(BitSet tmp0, BitSet key){
        BitSet ans = new BitSet(32);
        ans.clear();
        ans.set(1);
        ans.set(2);
        return ans;
    }

    //feistelnetwork: turns plain(64 bits) into cipher(64 bits), should be called with rounds=16 in DES
    //keys should be 56 bits BitSet
    private static BitSet feistelNetwork(int rounds, BitSet plainBlock, BitSet keys[]){
        BitSet tmp1 = plainBlock.get(0,31);
        BitSet tmp2 = plainBlock.get(32,63);
        BitSet tmp3;
        int i = 0;
        while(i < rounds){

            tmp3 = DESf(tmp2, keys[i]);
            tmp3.xor(tmp1);
            tmp1 = tmp2;

            tmp2 = tmp3;
            i++;
        }
        tmp3 = tmp1;
        tmp1 = tmp2;
        tmp2 = tmp3;
        BitSet cipher = new BitSet(64);
        for(i = 0; i < 32; i ++){
            if(tmp1.get(i) == true){
                cipher.set(i);
            }
        }
        for(i = 0; i < 32; i ++){
            if(tmp2.get(i) == true){
                cipher.set(i+32);
            }
        }
        return cipher;
        //StringBuilder cipher = new StringBuilder();
        //cipher.append(BitSettoAscii(tmp1));
        //cipher.append(BitSettoAscii(tmp2));
        //return cipher.toString();
    }

    private static void encrypt(StringBuilder keyStr, StringBuilder inputFile,
            StringBuilder outputFile) {

        try {
            PrintWriter writer = new PrintWriter(outputFile.toString(), "UTF-8");

            String encryptedText;

            // read input as a single string
            String plaintext = new String(Files.readAllBytes(Paths.get(inputFile.toString())));
            encryptedText = DES_encrypt(plaintext, keyStr);
            writer.print(encryptedText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generate key for each round based on encryption or decryption
     * @param keyStr The key string
     * @param type Encryption or Decryption. Values are 'e' or 'd'.
     * @return BitSet[] A list of keys for each round
     */
    private static BitSet[] keyExpansion(StringBuilder keyStr, String type) {
        BitSet[] keys = new BitSet[16];
        BitSet fakeKey = StringtoBitSet("bbbbbbb");
        for(int i = 0; i < 16; i ++){
            keys[i] = fakeKey;
        }
        return keys;
    }

    /**
     * Print BitSet as 1s and 0s
     */
    private static void printBitSet(BitSet bset){
        for(int i = 0; i < bset.size(); i ++) {
            if(i % 8 == 0) {
                System.out.print(" ");
            }
            if(bset.get(i) == true) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.print("\n");
    }

    /**
     * Initial or Final Permutation
     */
    private static void permutation(BitSet original, int[] sbox) {
        if(original.size() != 64) {
            throw new IllegalArgumentException("Block size is not 64 bit.");
        }

        BitSet permuted = new BitSet(64);
        for(int i = 0; i < 64; i ++) {
            if(original.get(sbox[i]-1) == true) {
                permuted.set(i);
            }
        }

        original = permuted;
    }

    /**
     * Generate IV for CBC mode
     */
    private static BitSet getCBC_IV() {
        BitSet CBC_IV = new BitSet(64);
        SecureRandom generator = new SecureRandom();
        generator.setSeed(System.currentTimeMillis());
        for(int i = 0; i < 64; i ++) {
            CBC_IV.set(i, generator.nextBoolean());
        }
        return CBC_IV;
    }

    /**
     * Use PKCS5 padding algorithm to pad block
     * @param block The block to be padded
     * @return String Padded block
     */
    private static String Padding_PKCS5(String block) {
        BitSet block_bit = StringtoBitSet(block);
        int padding_length = 8 - block.length()%8;
        char padding_char = (char)padding_length;
        for(int i = 0; i < padding_length; i ++) {
            block = block+padding_char;
        }
        return block;
    }

    /**
     * The DES encryption function
     * @param line The input plaintext
     * @return String The encrypted cyphertext
     */
    private static String DES_encrypt(String line, StringBuilder keyStr) {
        int blockSize = 8; // 8 bytes = 64 bits
        int batch = (int)Math.ceil(line.length()*1.0 / blockSize);

        BitSet[] EncryptKeys = keyExpansion(keyStr, "e");

        String line_padded = Padding_PKCS5(line);
        StringBuilder ciphertext = new StringBuilder();
        BitSet lastCBC = getCBC_IV();
        ciphertext.append(BitSettoHex(lastCBC)+"\n");
        for(int i = 0; i < batch; i++){
            BitSet block = StringtoBitSet(line_padded.substring(i*blockSize,i*blockSize+blockSize));

            // Using CBC mode
            //block.xor(lastCBC);

            // Encrypt
            permutation(block, DES.IP); // Initial permutation
            //BitSet result = feistelNetwork(16, block, EncryptKeys);
            BitSet result = block;
            permutation(result, DES.FP); // Final permutation
            lastCBC = result;

            ciphertext.append(BitSettoHex(result)+"\n");
        }
        return ciphertext.toString();
    }


    static void genDESkey(){
        System.out.println("New key goes here");
        return;
    }


    /**
     * This function Processes the Command Line Arguments.
     */
    private static void pcl(String[] args, StringBuilder inputFile,
            StringBuilder outputFile, StringBuilder keyString,
            StringBuilder encrypt) {
        /*
         * http://www.urbanophile.com/arenn/hacking/getopt/gnu.getopt.Getopt.html
         */
        Getopt g = new Getopt("Chat Program", args, "hke:d:i:o:");
        int c;
        String arg;
        while ((c = g.getopt()) != -1){
            switch(c){
                case 'o':
                    arg = g.getOptarg();
                    outputFile.append(arg);
                    break;
                case 'i':
                    arg = g.getOptarg();
                    inputFile.append(arg);
                    break;
                case 'e':
                    arg = g.getOptarg();
                    keyString.append(arg);
                    encrypt.append("e");
                    break;
                case 'd':
                    arg = g.getOptarg();
                    keyString.append(arg);
                    encrypt.append("d");
                    break;
                case 'k':
                    genDESkey();
                    break;
                case 'h':
                    callUseage(0);
                case '?':
                    break; // getopt() already printed an error
                    //
                default:
                    break;
            }
        }

    }

    private static void callUseage(int exitStatus) {

        String useage =
            "    -h            List all command line options\n" +
            "    -k            Generate a DES key\n" +
            "    -e <key>      Specify the key used to encrypt the file\n" +
            "    -d <key>      Specify the key used to decrypt the file\n" +
            "    -i <path>     Specify the input file\n" +
            "    -o <path>     Specify the output file\n";

        System.err.println(useage);
        System.exit(exitStatus);

    }

}
