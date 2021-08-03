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

import ro.kovari.params.Arguments;

import java.io.File;



public final class FileStoredEnvironment {

    private final File parent;
    private final String environmentName;
    private final FileStoredAccount account;



    public FileStoredEnvironment(File parent, Arguments args) {
        this.parent = parent;
        this.environmentName = args.getEnvironment().name();
        this.account = new FileStoredAccount(new File(parent, this.environmentName), args);
    }



    public FileStoredAccount getFileStoredAccount() {
        return account;
    }



    protected void persist() {
        File environmentDir = new File(parent, environmentName);

        if (!environmentDir.exists()) {
            if (!environmentDir.mkdir()) {
                System.out.println("Could not persist environment");
                return;
            }
        }
        account.persist();
    }
}
