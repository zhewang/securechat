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
    /**
     * PC1 used in key expansion
     */
    private static int[]  PC1 = {
        57, 49, 41, 33, 25, 17, 9 ,
        1 , 58, 50, 42, 34, 26, 18,
        10, 2 , 59, 51, 43, 35, 27,
        19, 11, 3 , 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15,
        7 , 62, 54, 46, 38, 30, 22,
        14, 6 , 61, 53, 45, 37, 29,
        21, 13, 5 , 28, 20, 12, 4 };
    /**
     * PC2 used in key expansion
     */
    private static int[] PC2 = {
        14, 17, 11, 24, 1 , 5 ,
        3 , 28, 15, 6 , 21, 10,
        23, 19, 12, 4 , 26, 8 ,
        16, 7 , 27, 20, 13, 2 ,
        41, 52, 31, 37, 47, 55,
        30, 40, 51, 45, 33, 48,
        44, 49, 39, 56, 34, 53,
        46, 42, 50, 36, 29, 32 };
    /**
     * rotations used in key expansion
     */
    private static int[] rotations = {
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    /**
     * f function: Expansion Permutation
     */
    private static int[] E = {
        32, 1, 2, 3, 4, 5
        , 4, 5, 6, 7, 8, 9
        , 8, 9, 10, 11, 12, 13
        , 12, 13, 14, 15, 16, 17
        , 16, 17, 18, 19, 20, 21
        , 20, 21, 22, 23, 24, 25
        , 24, 25, 26, 27, 28, 29
        , 28, 29, 30, 31, 32, 1
    };

    /**
     * f function: S-Box Substitution
     */
    private static final byte[][] S = { {
        14, 4,  13, 1,  2,  15, 11, 8,  3,  10, 6,  12, 5,  9,  0,  7,
        0,  15, 7,  4,  14, 2,  13, 1,  10, 6,  12, 11, 9,  5,  3,  8,
        4,  1,  14, 8,  13, 6,  2,  11, 15, 12, 9,  7,  3,  10, 5,  0,
        15, 12, 8,  2,  4,  9,  1,  7,  5,  11, 3,  14, 10, 0,  6,  13
    }, {
        15, 1,  8,  14, 6,  11, 3,  4,  9,  7,  2,  13, 12, 0,  5,  10,
        3,  13, 4,  7,  15, 2,  8,  14, 12, 0,  1,  10, 6,  9,  11, 5,
        0,  14, 7,  11, 10, 4,  13, 1,  5,  8,  12, 6,  9,  3,  2,  15,
        13, 8,  10, 1,  3,  15, 4,  2,  11, 6,  7,  12, 0,  5,  14, 9
    }, {
        10, 0,  9,  14, 6,  3,  15, 5,  1,  13, 12, 7,  11, 4,  2,  8,
        13, 7,  0,  9,  3,  4,  6,  10, 2,  8,  5,  14, 12, 11, 15, 1,
        13, 6,  4,  9,  8,  15, 3,  0,  11, 1,  2,  12, 5,  10, 14, 7,
        1,  10, 13, 0,  6,  9,  8,  7,  4,  15, 14, 3,  11, 5,  2,  12
    }, {
        7,  13, 14, 3,  0,  6,  9,  10, 1,  2,  8,  5,  11, 12, 4,  15,
        13, 8,  11, 5,  6,  15, 0,  3,  4,  7,  2,  12, 1,  10, 14, 9,
        10, 6,  9,  0,  12, 11, 7,  13, 15, 1,  3,  14, 5,  2,  8,  4,
        3,  15, 0,  6,  10, 1,  13, 8,  9,  4,  5,  11, 12, 7,  2,  14
    }, {
        2,  12, 4,  1,  7,  10, 11, 6,  8,  5,  3,  15, 13, 0,  14, 9,
        14, 11, 2,  12, 4,  7,  13, 1,  5,  0,  15, 10, 3,  9,  8,  6,
        4,  2,  1,  11, 10, 13, 7,  8,  15, 9,  12, 5,  6,  3,  0,  14,
        11, 8,  12, 7,  1,  14, 2,  13, 6,  15, 0,  9,  10, 4,  5,  3
    }, {
        12, 1,  10, 15, 9,  2,  6,  8,  0,  13, 3,  4,  14, 7,  5,  11,
        10, 15, 4,  2,  7,  12, 9,  5,  6,  1,  13, 14, 0,  11, 3,  8,
        9,  14, 15, 5,  2,  8,  12, 3,  7,  0,  4,  10, 1,  13, 11, 6,
        4,  3,  2,  12, 9,  5,  15, 10, 11, 14, 1,  7,  6,  0,  8,  13
    }, {
        4,  11, 2,  14, 15, 0,  8,  13, 3,  12, 9,  7,  5,  10, 6,  1,
        13, 0,  11, 7,  4,  9,  1,  10, 14, 3,  5,  12, 2,  15, 8,  6,
        1,  4,  11, 13, 12, 3,  7,  14, 10, 15, 6,  8,  0,  5,  9,  2,
        6,  11, 13, 8,  1,  4,  10, 7,  9,  5,  0,  15, 14, 2,  3,  12
    }, {
        13, 2,  8,  4,  6,  15, 11, 1,  10, 9,  3,  14, 5,  0,  12, 7,
        1,  15, 13, 8,  10, 3,  7,  4,  12, 5,  6,  11, 0,  14, 9,  2,
        7,  11, 4,  1,  9,  12, 14, 2,  0,  6,  10, 13, 15, 3,  5,  8,
        2,  1,  14, 7,  4,  10, 8,  13, 15, 12, 9,  0,  3,  5,  6,  11
    } };

    /**
     * f function: P-Box Substitution
     */
    private static int[] P = {
        16, 7, 20, 21,
        29, 12, 28, 17,
        1, 15, 23, 26,
        5, 18, 31, 10,
        2, 8, 24, 14,
        32, 27, 3, 9,
        19, 13, 30, 6,
        22, 11, 4, 25};

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

    private static void decrypt(StringBuilder keyStr, StringBuilder inputFile,
            StringBuilder outputFile) {
        try {
            PrintWriter writer = new PrintWriter(outputFile.toString(), "UTF-8");
            List<String> lines = Files.readAllLines(Paths.get(inputFile.toString()), Charset.defaultCharset());

            String IVStr = lines.get(0);
            BitSet lastCBC = HextoBitSet(IVStr);
            BitSet decrypted;

            for (int i = 1; i < lines.size(); i ++) {
                String line = lines.get(i);
                if(line.length() != 16) {
                    throw new IllegalArgumentException("Decrypt block size should be 64 bits.");
                }
                BitSet block = HextoBitSet(line);

                decrypted = DES_decrypt(block, keyStr);
                decrypted.xor(lastCBC);
                lastCBC = block;

                if(i == lines.size()-1) {
                    writer.print(Delete_Padding_PKCS5(decrypted));
                } else {
                    writer.print(BitSettoAscii(decrypted));
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * DES decryption
     * @param block One block of the cyphertext. It should be 64 bits as the requirement
     * @param keyStr Decryption key
     */
    private static BitSet DES_decrypt(BitSet block, StringBuilder keyStr) {
        BitSet[] keys = keyExpansion(keyStr, "d");

        permutation(block, DES.FP); // Initial permutation
        BitSet result = feistelNetwork(16, block, keys);
        permutation(result, DES.IP); // Final permutation

        return result;
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
            block.xor(lastCBC);

            // Encrypt
            permutation(block, DES.IP); // Initial permutation
            BitSet result = feistelNetwork(16, block, EncryptKeys);
            permutation(result, DES.FP); // Final permutation

            lastCBC = result;

            ciphertext.append(BitSettoHex(result)+"\n");
        }
        return ciphertext.toString();
    }


    private static void genDESkey(){
        BitSet key = new BitSet(64);
        SecureRandom generator = new SecureRandom();
        generator.setSeed(System.currentTimeMillis());
        do {
            for(int i = 0; i < 64; i ++) {
                key.set(i, generator.nextBoolean());
            }
        } while (isWeakKey(key));
        System.out.println(BitSettoHex(key));
        return;
    }

    private static boolean isWeakKey(BitSet key) {
        String[] weak_patterns = {
            "0p0p0p0p0p0p0p0p", "0p1P0p1P0p0P0p0P",
            "0pep0pep0pfp0pfp", "0pfP0pfP0pfP0pfP",
            "1P0p1P0p0P0p0P0p", "1P1P1P1P0P0P0P0P",
            "1Pep1Pep0Pfp0Pfp", "1PfP1PfP0PfP0PfP",
            "ep0pep0pfp0pfp0p", "ep1Pep1pfp0Pfp0P",
            "epepepepepepepep", "epfPepfPfpfPfpfP",
            "fP0pfP0pfP0pfP0p", "fP1PfP1PfP0PfP0P",
            "fPepfPepfPepfPep", "fPfPfPfPfPfPfPfP"};
        String keyStr = BitSettoHex(key).toLowerCase();
        boolean isSame = true;
        for(int i = 0; i < 16; i ++) {
            String pattern = weak_patterns[i];
            isSame = true;
            for(int j = 0; j < 16; j ++) {
                if(pattern.charAt(j) == 'p'){
                    if(keyStr.charAt(j) != '0' && keyStr.charAt(j) != '1') {
                        isSame = false;
                        break;
                    }
                } else if(pattern.charAt(j) == 'P') {
                    if(keyStr.charAt(j) != 'e' && keyStr.charAt(j) != 'f') {
                        isSame = false;
                        break;
                    }
                } else {
                    if(keyStr.charAt(j) != pattern.charAt(j)) {
                        isSame = false;
                        break;
                    }
                }
            }
            if(isSame == true) {
                break;
            }
        }
        return isSame;
    }

    /**
     * The f function used by feistel Network
     */
    private static BitSet DESf(BitSet block, BitSet key){
        // Expansion Permutation: 32 bits -> 48 bits
        BitSet ep = new BitSet(48);
        for(int i = 0; i < 48; i ++) {
            if(block.get(DES.E[i]-1) == true) {
                ep.set(i);
            }
        }

        // XOR with key: 48 bits
        ep.xor(key);

        // S-Box Substitution: 48 bits -> 32 bits
        BitSet sbs = SBoxSubstituion(ep);

        // P-Box Permutation: 32 bits
        BitSet ppp = new BitSet(32);
        for(int i = 0; i < 32; i ++) {
            if(sbs.get(DES.P[i]-1) == true) {
                ppp.set(i);
            }
        }
        return ppp;
    }

    private static BitSet SBoxSubstituion(BitSet input) {
        BitSet output = new BitSet(32);
        for(int i = 0; i < 8; i ++) {
            byte[] sbox = DES.S[i]; //S-Box for i-th segment
            int[] bits = {0, 0, 0, 0, 0, 0};
            for(int bits_i = 0; bits_i < 6; bits_i ++) {
                bits[bits_i] = (input.get(i*6+bits_i)) ? 1:0;
            }
            int row = bits[0]*2+bits[5];
            int col = bits[1]*8+bits[2]*4+bits[3]*2+bits[4];
            int s_value_10 = sbox[row*16+col];
            int index = 0;
            while (s_value_10 != 0) {
                if (s_value_10% 2 != 0) {
                    output.set(i*4+index);
                }
                ++index;
                s_value_10 = s_value_10 >>> 1;
            }
        }
        return output;
    }

    //feistelnetwork: turns plain(64 bits) into cipher(64 bits), should be called with rounds=16 in DES
    //keys should be 56 bits BitSet
    private static BitSet feistelNetwork(int rounds, BitSet plainBlock, BitSet keys[]){
        BitSet tmp1 = plainBlock.get(0,32);
        BitSet tmp2 = plainBlock.get(32,64);
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
    }

    /**
     * Generate key for each round based on encryption or decryption
     * @param keyStr The key string
     * @param type Encryption or Decryption. Values are 'e' or 'd'.
     * @return BitSet[] A list of keys for each round
     */
    private static BitSet[] keyExpansion(StringBuilder keyStr, String type) {
        //make sure keyStr is bitset of 64 size
        //BitSet keyBlock64 = StringtoBitSet(keyStr.toString());
        //maybe do sanity check of parity, not done yet
        BitSet keyBlock64 = HextoBitSet("c82cea9ed93dfb8f");
        BitSet[] keyBlock56 = key_64_to_2_28(keyBlock64);
        BitSet[] keys = new BitSet[16];
        for(int i = 0; i < 16; i ++){

            //round key generation
            LeftCircularShift(keyBlock56[0], rotations[i]);
            LeftCircularShift(keyBlock56[1], rotations[i]);
            BitSet roundKey = key_2_28_to_48(keyBlock56);
            //mode
            //printBitSet(roundKey,6);
            if(type.equals("d"))
                keys[i] = roundKey;
            else if(type.equals("e"))
                keys[15 - i] = roundKey;
            else
                System.out.println("wrong mode, plz use e or d");
        }
        return keys;
    }
    /**
     * left shift block for movingBits, for 28 bit block only
     */
    private static void LeftCircularShift(BitSet block, int movingBits){
        BitSet buffer = new BitSet(movingBits);
        buffer.clear();
        for(int i = 0; i < movingBits; i++){
            if(block.get(i) == true)
                buffer.set(i);
        }
        for(int i = movingBits; i < 28; i++){
            block.set(i-movingBits,block.get(i));
        }
        for(int i = 28 - movingBits; i < 28; i++){
            block.set(i,buffer.get(i-28+movingBits));
        }
    }

    /**
     * Print BitSet as 1s and 0s
     */
    private static void printBitSet(BitSet bset,int base){
        for(int i = 0; i < bset.size(); i ++) {
            if(i % base == 0) {
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
     * key extraction used in key expansion
     */
    private static BitSet[] key_64_to_2_28(BitSet keyBlock64){
        BitSet[] keyBlock56 = new BitSet[2];
        keyBlock56[0] = new BitSet(28);
        keyBlock56[1] = new BitSet(28);
        for(int i = 0; i < 4; i++){
            boolean parity = true;
            for(int j = 0; j < 7; j++){

                if(keyBlock64.get(PC1[i*7 + j] - 1) == true){
                    keyBlock56[0].set(i*7 + j);
                    parity = !parity;
                }
            }
        }
        for(int i = 4; i < 8; i++){
            boolean parity = false;
            for(int j = 0; j < 7; j++){
                if(keyBlock64.get(PC1[i*7 + j] - 1) == true){
                    keyBlock56[1].set(i*7 + j - 28);
                }
            }
        }

        return keyBlock56;
    }
    /**
     * key generation phase used in key expansion
     */
    private static BitSet key_2_28_to_48(BitSet[] keyBlock56){
        BitSet roundKey = new BitSet(56);
        for(int i = 0; i < 48; i++){
            if(PC2[i] > 28)
                roundKey.set(i,keyBlock56[1].get(PC2[i]-29));
            else roundKey.set(i,keyBlock56[0].get(PC2[i]-1));
        }
        return roundKey;

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

    private static String Delete_Padding_PKCS5(BitSet block) {
        byte[] b = block.toByteArray();
        int padding_length = b[b.length-1];
        int flag = 1;
        for(int i = 0; i < padding_length; i ++) {
            if(b[b.length-i-1] != padding_length) {
                flag = 0;
                break;
            }
        }
        if(flag == 0) {
            return new String(b);
        } else {
            byte [] subArray = Arrays.copyOfRange(b, 0, b.length-padding_length);
            return new String(subArray);
        }
    }

    /**
     * Transform a bitset to hex string
     */
    private static String BitSettoHex(BitSet block){
        int hexlength = block.size()/4;
        StringBuilder hex = new StringBuilder();
        for(int k = 0; k < block.toByteArray().length; k++){
            String result = String.format("%02X",block.toByteArray()[k]);
            hex.append(result);
        }
        int endZeros = (hexlength - hex.length())/2;
        while(endZeros > 0){
            hex.append("00");
            endZeros --;
        }
        return hex.toString();
    }

    /**
     * Transform a bitset to ascii string
     */
    private static String BitSettoAscii(BitSet block){
        byte[] b = block.toByteArray();
        String str = new String(b);
        return str;
    }

    /**
     * Transform Hex string to BitSet
     */
    private static BitSet HextoBitSet(String input){
        //choose input length carefully so that it fits in 64 bits
        BigInteger bigint = new BigInteger(input, 16);
        byte[] content = bigint.toByteArray();
        if(content.length > 8) {
            content = Arrays.copyOfRange(content, 1, content.length);
        }
        if(content.length < 8) {
            byte[] tmp = content;
            content = new byte[8];
            for(int i = 0; i < tmp.length; i ++)
                content[8-i-1] = tmp[tmp.length-i-1];
        }
        BitSet bits = BitSet.valueOf(content);
        return bits;
    }

    /**
     * Transforms ascii string to bitset
     */
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

    /**
     * Processes the Command Line Arguments.
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
