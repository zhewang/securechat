# Secure Chat
An encrypted communication program using DES and RSA algorithms.

# How to run
Put `java-getopt-1.0.14.jar` (Download [here](https://github.com/arenn/java-getopt)) into the repo's root directory.

## DES
* Compile: `javac -cp "java-getopt-1.0.14.jar" DES.java`
* See Help: `java -cp ".:java-getopt-1.0.14.jar" DES -h`
* Encrypt: `java -cp ".:java-getopt-1.0.14.jar" DES -e 5C102B6EC74730D3 -i test_plain.txt -o test_encrypted.txt`
* Decrypt `java -cp ".:java-getopt-1.0.14.jar" DES -d 5C102B6EC74730D3 -i test_encrypted.txt -o test_decrypted.txt`

## RSA
* Compile: `javac -cp "java-getopt-1.0.14.jar" RSA.java`
* See Help: `java -cp ".:java-getopt-1.0.14.jar" RSA -h`
* Key Generation: `java -cp ".:java-getopt-1.0.14.jar" RSA -k -b <key_length>`
* Encrypt: `java -cp ".:java-getopt-1.0.14.jar"  -e <public_key> -n <modulus> -i <plaintext_value>`
* Decrypt: `java -cp ".:java-getopt-1.0.14.jar"  -d <private_key> -n <modulus> -i <ciphertext_value>`

## CHAT
* Compile: `javac -cp "java-getopt-1.0.14.jar" CHAT.java RSAlib.java DESlib.java`
* See Help: `java -cp ".:java-getopt-1.0.14.jar" CHAT -h`
* Alice side(must start first): `java -cp ".:java-getopt-1.0.14.jar" CHAT --alice -b <public_key_bob> -n <bob_modulus> -a <private_key_alice> -m <alice_modulus> -p <port> -i <ip_address>`
* Bob side: `java -cp ".:java-getopt-1.0.14.jar" CHAT --bob -b <private_key_bob> -n <bob_modulus> -a <public_key_alice> -m <alice_modulus> -p <port> -i <ip_address>`



## Group Members
* [Zhe Wang](https://github.com/zhewang), [Yang Liu](https://github.com/YangLiuAZ)
* https://github.com/zhewang/securechat

