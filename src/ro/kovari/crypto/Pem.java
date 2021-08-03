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

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import ro.kovari.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.logging.Logger;



public final class Pem {

    private static final Logger logger = Logger.getLogger(Pem.class.getName());



    public static void exportPrivateKey(File file, byte[] data) {
        logger.info(String.format("Exporting private key %s ...", file.getName()));
        writeTo(file, PemType.PRIVATE_KEY, data);
        logger.info("Done.");
    }



    public static void exportPublicKey(File file, byte[] data) {
        logger.info(String.format("Exporting public key %s ...", file.getName()));
        writeTo(file, PemType.PUBLIC_KEY, data);
        logger.info("Done.");
    }



    public static void exportKeyPair(File dir, KeyPair keyPair) {
        FileUtils.validateDirectory(dir);
        logger.info(String.format("Exporting key pair for %s ...", dir.getName()));
        File privateKeyFile = new File(dir, dir.getName() + ".key");
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        exportPrivateKey(privateKeyFile, privateKey);

        File publicKeyFile = new File(dir, dir.getName() + ".pem");
        byte[] publicKey = keyPair.getPublic().getEncoded();
        exportPublicKey(publicKeyFile, publicKey);
        logger.info("Done.");
    }



    public static void exportCertificateRequest(File file, byte[] data) {
        logger.info(String.format("Exporting certificate request %s ...", file.getName()));
        writeTo(file, PemType.CERTIFICATE_REQUEST, data);
        logger.info("Done.");
    }



    public static void exportCertificate(File file, byte[] data) {
        logger.info(String.format("Exporting certificate %s ...", file.getName()));
        FileUtils.save(file, data);
        logger.info("Done.");
    }



    private static void writeTo(File file, PemType type, byte[] data) {
        try (FileWriter fileWriter = new FileWriter(file);
             PemWriter pemWriter = new PemWriter(fileWriter)) {

            pemWriter.writeObject(new PemObject(type.getName(), data));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
