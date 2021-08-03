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
import ro.kovari.utils.FileUtils;

import java.io.File;
import java.security.KeyPair;
import java.util.Objects;
import java.util.logging.Logger;



public final class FileStoredAccount {

    private static final Logger logger = Logger.getLogger(FileStoredAccount.class.getName());

    private final File parent;
    private final String accountName;
    private final FileStoredDomain domain;

    private KeyPair keyPair;



    public FileStoredAccount(File parent, Arguments args) {
        this.parent = parent;
        this.accountName = args.getContacts().get(0);
        this.domain = new FileStoredDomain(new File(parent, this.accountName), args);
    }



    public FileStoredDomain getFileStoredDomain() {
        return domain;
    }



    public FileStoredAccount setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
        return this;
    }



    protected void persist() {
        File accountDir = new File(parent, accountName);
        if (accountDir.exists()) {
            // for now, if an account with the same name already exists, rename it
            File dest = FileUtils.getFirstUnIndexedDirectory(accountDir);
            if (!accountDir.renameTo(dest)) {
                logger.info("Unable to rename account directory. Stopping persistence.");
                return;
            }
        }

        if (!accountDir.exists()) {
            // already existing directory was renamed; need to recreate the directory
            if (!accountDir.mkdir()) {
                logger.info("Unable to create account directory. Stopping persistence");
                return;
            }
        }

        if (Objects.nonNull(keyPair)) {
            Pem.exportKeyPair(accountDir, keyPair);
        }
        domain.persist();
    }
}
