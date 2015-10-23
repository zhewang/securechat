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
	public static void main(String[] args){
		BitSet plainBlock = StringtoBits("aaaaaaaa");//testing StringtoBits
		BitSettoHex(plainBlock);
	}

	private static void BitSettoHex(BitSet block){
		for(int k = 0; k < block.toByteArray().length; k++)
			System.out.printf("%02X",block.toByteArray()[k]);
		System.out.println();
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
	private static String DES_decrypt(String iVStr, String line) {

		return null;
	}

	//StringtoBits: transforms ascii text to bits representation
	private static BitSet StringtoBits(String input){
		//choose input length carefully so that it fits in 64 bits
		byte[] bytes = input.getBytes();
		BitSet plainBlock = new BitSet(64);
		plainBlock.clear();
		int i = 0;
		for(byte b:bytes){
			System.out.println(b);
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
		return ans;
	}
	//feistelnetwork: turns plain(64 bits) into cipher(64 bits), should be called with rounds=16 in DES
	private static String feistelNetwork(int rounds, String plain, BitSet decrypt_keys[]){
		BitSet plainBlock = StringtoBits(plain);
		BitSet tmp1 = plainBlock.get(0,31);
		BitSet tmp2 = plainBlock.get(32,63);
		BitSet tmp3;
		int i = 0;
		while(i < rounds){
			tmp3 = DESf(tmp2,decrypt_keys[i]);
			tmp1 = tmp2;
			tmp1.xor(tmp3);
			tmp2 = tmp1;
		}
		tmp3 = tmp1;
		tmp1 = tmp2;
		tmp2 = tmp3;
		StringBuilder cipher = new StringBuilder();
		cipher.append(tmp1.toString());
		cipher.append(tmp2.toString());
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
