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



package ro.kovari.storage;

import ro.kovari.crypto.Pem;
import ro.kovari.params.Arguments;

import java.io.File;
import java.security.KeyPair;
import java.util.Objects;
import java.util.logging.Logger;



public final class FileStoredDomain {

    private static final Logger logger = Logger.getLogger(FileStoredDomain.class.getName());

    private final File parent;
    private final String domainName;

    private KeyPair keyPair;
    private byte[] certificateRequest;
    private byte[] certificate;



    public FileStoredDomain(File parent, Arguments args) {
        this.parent = parent;
        this.domainName = args.getDomains().get(0).replace("*.", "");
    }



    public FileStoredDomain setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }



    public FileStoredDomain setCertificateRequest(byte[] csr) {
        this.certificateRequest = csr;
        return this;
    }



    public FileStoredDomain setCertificate(byte[] certificate) {
        this.certificate = certificate;
        return this;
    }



    protected void persist() {
        File domainDir = new File(parent, domainName);
        if (!domainDir.exists()) {
            if (!domainDir.mkdir()) {
                logger.info("Unable to create domain directory. Stopping persistence");
                return;
            }
        }

        if (Objects.nonNull(certificate)) {
            File certFile = new File(domainDir, domainName + ".cer");
            Pem.exportCertificate(certFile, certificate);
        }

        if (Objects.nonNull(certificateRequest)) {
            File csrFile = new File(domainDir, domainName + ".csr");
            Pem.exportCertificateRequest(csrFile, certificateRequest);
        }

        if (Objects.nonNull(keyPair)) {
            Pem.exportKeyPair(domainDir, keyPair);
        }
    }
}
