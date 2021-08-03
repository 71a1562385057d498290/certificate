/*
 * Copyright (c) Attila Kovari
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



package ro.kovari.crypto;

import ro.kovari.utils.Encoder;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class KeyTool {

    private static final String RSA = "RSA";
    private static final String SHA256withRSA = "SHA256withRSA";
    private static final String SHA256 = "SHA-256";



    /**
     * Generate a RSA key pair with a 4096 key size by default
     * @return the generated key pair
     */
    public static KeyPair generateRSAKeyPair() {
        return generateRSAKeyPair(4096);
    }



    /**
     * Generate a RSA key pair with the given key size
     * @return the generated key pair
     */
    public static KeyPair generateRSAKeyPair(int keySize) {
        KeyPair generatedKeys = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
            generator.initialize(keySize);
            generatedKeys = generator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedKeys;
    }



    /**
     * Sign a message with SHA256withRSA using the given private key
     * @param message the message
     * @param privateKey the private key
     */
    public static byte[] sign(String message, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signer = Signature.getInstance(SHA256withRSA);
        signer.initSign(privateKey);
        signer.update(message.getBytes(StandardCharsets.UTF_8));
        return signer.sign();
    }



    /**
     * Sign a message with SHA256withRSA using the given private key and
     * return a Base64 encoded result
     * @param message the message
     * @param privateKey the private key
     */
    public static String signAndEncode(String message, PrivateKey privateKey) {
        String signed = null;
        try {
            byte[] signedMessage = KeyTool.sign(message, privateKey);
            signed = Encoder.base64(signedMessage);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception signing message!");
        }
        return signed;
    }



    /**
     * Generate the SHA256 hash for the given message
     * @param message the message to digest
     * @return the hexadecimal representation of the generated hash
     */
    public static String sha256digestHex(String message) {
        return DatatypeConverter
                .printHexBinary(sha256digest(message.getBytes(StandardCharsets.UTF_8)));
    }



    /**
     * Generate SHA-256 digest
     * @param message the message to digest
     * @return the bytes[] of the digest
     */
    public static byte[] sha256digest(String message) {
        return sha256digest(message.getBytes(StandardCharsets.UTF_8));
    }



    /**
     * Generate a SHA-256 digest
     * @param message a byte array representing the message
     * @return a byte array with the message digest or an empty byte array in case of error
     */
    public static byte[] sha256digest(byte[] message) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA256);
            return digest.digest(message);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }



    /**
     * Create a {@link KeyPair} from the specified private and public keys
     * @param privateKey the {@link PrivateKey}
     * @param publicKey the {@link PublicKey}
     * @return a key pair from the specified keys
     */
    public static KeyPair getKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        return new KeyPair(publicKey, privateKey);
    }
}
