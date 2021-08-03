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
import java.util.stream.Collectors;



/**
 * Shadow/pseudo account representing a remote ACME account.
 * In practice this is just a container for the account's private, public key
 * and contacts
 */
public class AccountIdentity {

    private static final int KEY_SIZE = 2048;

    private final KeyPair keyPair;
    private final boolean externalIdentityProvided;
    private final List<String> contacts;



    /**
     * Create a new {@link AccountIdentity} object using a newly
     * generated {@link KeyPair}. The default key size has 2048 bits.
     * Currently no custom key size can be specified.
     */
    public AccountIdentity(List<String> contacts) {
        this.contacts = contacts;
        this.keyPair = KeyTool.generateRSAKeyPair(KEY_SIZE);
        this.externalIdentityProvided = false;
    }



    /**
     * Create a new {@link AccountIdentity} using an existing {@link KeyPair}.
     * Used when loading an existing {@link KeyPair} from an external source.
     * Ex: loading from disk.
     * @param pair the {@link KeyPair} to use
     */
    public AccountIdentity(List<String> contacts, KeyPair pair) {
        this.contacts = contacts;
        this.keyPair = pair;
        this.externalIdentityProvided = true;
    }



    /**
     * Get the {@link PublicKey} of this {@link AccountIdentity}
     * @return the {@link PublicKey} of this {@link AccountIdentity}
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }



    /**
     * Get the {@link PrivateKey} of this {@link AccountIdentity}
     * @return the {@link PrivateKey} of this {@link AccountIdentity}
     */
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }



    /**
     * Get the {@link KeyPair} of this {@link AccountIdentity}
     * @return the {@link KeyPair} of this {@link AccountIdentity}
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }



    public List<String> getContacts() {
        return contacts;
    }



    public List<String> getFormattedContacts() {
        return contacts.stream().map(str -> "mailto: " + str).collect(Collectors.toList());
    }



    /**
     * If an external, existing {@link KeyPair} was set for
     * this {@link AccountIdentity} this will return true
     * @return true, if an external, already existing {@link KeyPair} was set
     */
    public boolean isExternalIdentityProvided() {
        return externalIdentityProvided;
    }



    /**
     * Class representing a Json Web Key.
     * This is closely related to the {@link AccountIdentity}
     */
    public static class JsonWebKey {

        private final String e;
        private final String kty;
        private final String n;



        public JsonWebKey(String e, String kty, String n) {
            this.e = e;
            this.kty = kty;
            this.n = n;
        }



        public String getE() { return e; }



        public String getKty() { return kty; }



        public String getN() { return n; }
    }
}
