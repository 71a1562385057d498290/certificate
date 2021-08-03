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



package ro.kovari.model;

import ro.kovari.crypto.KeyTool;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;



/**
 * This is just a container for the domain's private and public
 * key
 */
public class DomainsIdentity {

    private static final int KEY_SIZE = 4096;

    private final KeyPair keyPair;
    private final List<String> domains;



    /**
     * Create a new {@link DomainsIdentity} object using a newly
     * generated {@link KeyPair}. The default key size has 4096 bits.
     * Currently no custom key size can be specified
     */
    public DomainsIdentity(List<String> domains) {
        this.domains = domains;
        keyPair = KeyTool.generateRSAKeyPair(KEY_SIZE);
    }



    /**
     * Get the {@link PublicKey} of this {@link DomainsIdentity}
     * @return the {@link PublicKey} of this {@link DomainsIdentity}
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }



    /**
     * Get the {@link PrivateKey} of this {@link DomainsIdentity}
     * @return the {@link PrivateKey} of this {@link DomainsIdentity}
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }



    /**
     * Get the {@link KeyPair} of this {@link DomainsIdentity}
     * @return the {@link KeyPair} of this {@link DomainsIdentity}
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }



    public List<String> getDomains() {
        return domains;
    }
}