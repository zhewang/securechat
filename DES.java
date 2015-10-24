import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import gnu.getopt.Getopt;
import java.util.BitSet;

public class DES {

    /*	public static void main(String[] args) {

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
    */
    /*public static void main(String[] args){
      BitSet plainBlock = StringtoBitSet("aaaaaaaa");//testing StringtoBitSet
      BitSettoHex(plainBlock);
      }*/

    public static void main(String[] args){
        BitSet plainBlock = StringtoBitSet("bbbbbbb");//generating 56 bits, no effect for now, waiting for DESf to be implemented
        BitSet[] keys = new BitSet[2];
        keys[0] = plainBlock; keys[1] = plainBlock;
        String cipher = feistelNetwork(1,"aaaaaaaa",keys);//testing feistelNetwork
        System.out.println(cipher);
    }
    private static void decrypt(StringBuilder keyStr, StringBuilder inputFile,
            StringBuilder outputFile) {
        try {
            PrintWriter writer = new PrintWriter(outputFile.toString(), "UTF-8");
            List<String> lines = Files.readAllLines(Paths.get(inputFile.toString()), Charset.defaultCharset());
            String IVStr = lines.get(0);
            lines.remove(0);
            String encryptedText;

            for (String line : lines) {
                encryptedText = DES_decrypt(IVStr, line);
                writer.print(encryptedText);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO: You need to write the DES encryption here.
     * @param line
     */
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
        StringBuilder ascii = new StringBuilder();
        for(int k = 0; k < block.toByteArray().length; k++){
            String result = String.format("%c",block.toByteArray()[k]);
            ascii.append(result);
        }
        return ascii.toString();
    }
    private static String DES_decrypt(String iVStr, String line) {
        int blockSize = 8;
        StringBuilder target = new StringBuilder(line.length());
        int batch = line.length() / blockSize;// is it necessary to use ceil or floor?
        BitSet fakeKey = StringtoBitSet("bbbbbbb");
        BitSet[] keys = new BitSet[2];
        keys[0] = fakeKey; keys[1] = fakeKey;


        for(int i = 0; i < batch; i++){
            String result = feistelNetwork(16,line.substring(i*blockSize,i*blockSize+blockSize-1),keys);
            target.append(result);
        }
        return target.toString();
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
    private static String feistelNetwork(int rounds, String plain, BitSet decrypt_keys[]){
        BitSet plainBlock = StringtoBitSet(plain);
        BitSet tmp1 = plainBlock.get(0,31);
        BitSet tmp2 = plainBlock.get(32,63);
        BitSet tmp3;
        int i = 0;
        while(i < rounds){

            tmp3 = DESf(tmp2,decrypt_keys[i]);
            tmp3.xor(tmp1);
            tmp1 = tmp2;

            tmp2 = tmp3;
            i++;
        }
        tmp3 = tmp1;
        tmp1 = tmp2;
        tmp2 = tmp3;
        System.out.println(BitSettoHex(tmp1));
        System.out.println(BitSettoHex(tmp2));
        StringBuilder cipher = new StringBuilder();
        cipher.append(BitSettoAscii(tmp1));
        cipher.append(BitSettoAscii(tmp2));
        return cipher.toString();
    }
    private static void encrypt(StringBuilder keyStr, StringBuilder inputFile,
            StringBuilder outputFile) {

        try {
            PrintWriter writer = new PrintWriter(outputFile.toString(), "UTF-8");

            String encryptedText;
            for (String line : Files.readAllLines(Paths.get(inputFile.toString()), Charset.defaultCharset())) {
                encryptedText = DES_encrypt(line);
                writer.print(encryptedText);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    /**
     * TODO: You need to write the DES encryption here.
     * @param line
     */
    private static String DES_encrypt(String line) {

        return null;
    }


    static void genDESkey(){
        System.out.println("New key goes here");
        return;
    }


    /**
     * This function Processes the Command Line Arguments.
     * -p for the port number you are using
     * -h for the host name of system
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
