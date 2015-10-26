# Secure Chat
An encrypted communication program using DES and RSA algorithms.

# How to run
Put `java-getopt-1.0.14.jar` into the repo's root directory.

## DES
* Compile: `javac -cp "java-getopt-1.0.14.jar" DES.java`
* See Help: `java -cp ".:java-getopt-1.0.14.jar" DES -h`
* Encrypt: `java -cp ".:java-getopt-1.0.14.jar" DES -e 5C102B6EC74730D3 -i test_plain.txt -o test_encrypted.txt`
* Decrypt `java -cp ".:java-getopt-1.0.14.jar" DES -d 5C102B6EC74730D3 -i test_encrypted.txt -o test_decrypted.txt`

## RSA

## CHAT

