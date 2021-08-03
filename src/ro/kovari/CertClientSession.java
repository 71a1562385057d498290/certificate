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


package ro.kovari;

import ro.kovari.model.AccountIdentity;
import ro.kovari.model.DomainsIdentity;
import ro.kovari.model.acme.Directory;

import java.util.HashMap;
import java.util.Map;


/** Client session data container */
public final class CertClientSession {

    private final Map<String, Object> map = new HashMap<>();



    /**
     * Set a new session attribute value
     * @param name the name of the attribute
     * @param value the value to be set
     * @return the current {@link CertClientSession} instance
     */
    public CertClientSession setAttribute(String name, Object value) {
        map.put(name, value);
        return this;
    }



    /**
     * Get a session attribute value
     * @param name the name of the session attribute
     * @param clazz the {@link Class} instance of the expected return type
     * @return the session attribute value
     */
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(map.get(name));
    }



    public CertClientSession setIdentity(AccountIdentity identity) {
        setAttribute("identity", identity);
        return this;
    }



    public AccountIdentity getIdentity() {
        return getAttribute("identity", AccountIdentity.class);
    }



    public CertClientSession setEnvironment(CertEnvironment env) {
        setAttribute("environment", env);
        return this;
    }



    public CertEnvironment getEnvironment() {
        return getAttribute("environment", CertEnvironment.class);
    }



    public CertClientSession setAccountKey(AccountIdentity.JsonWebKey key) {
        setAttribute("jwk", key);
        return this;
    }



    public AccountIdentity.JsonWebKey getAccountKey() {
        return getAttribute("jwk", AccountIdentity.JsonWebKey.class);
    }



    public CertClientSession setAccountKeyID(String keyID) {
        setAttribute("kid", keyID);
        return this;
    }



    public String getAccountKeyID() {
        return getAttribute("kid", String.class);
    }



    public CertClientSession setDirectory(Directory directory) {
        setAttribute("directory", directory);
        return this;
    }



    public Directory getDirectory() {
        return getAttribute("directory", Directory.class);
    }



    public CertClientSession setDomainsIdentity(DomainsIdentity did) {
        setAttribute("domain.identity", did);
        return this;
    }



    public DomainsIdentity getDomainsIdentity() {
        return getAttribute("domain.identity", DomainsIdentity.class);
    }



    public CertClientSession setProvider(String provider) {
        setAttribute("dns.provider", provider);
        return this;
    }



    public String getProvider() {
        return getAttribute("dns.provider", String.class);
    }



    public CertClientSession setAutoModeEnabled(Boolean autoMode) {
        setAttribute("dns.auto.enabled", autoMode);
        return this;
    }



    public boolean getAutoModeEnabled() {
        return getAttribute("dns.auto.enabled", Boolean.class);
    }



    public CertClientSession setCertificateRequest(byte[] csr) {
        setAttribute("certificate.request", csr);
        return this;
    }



    public byte[] getCertificateRequest() {
        return getAttribute("certificate.request", byte[].class);
    }



    public CertClientSession setCertificate(byte[] certificate) {
        setAttribute("certificate", certificate);
        return this;
    }



    public byte[] getCertificate() {
        return getAttribute("certificate", byte[].class);
    }
}
