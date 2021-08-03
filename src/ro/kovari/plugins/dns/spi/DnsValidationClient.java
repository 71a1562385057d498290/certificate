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



package ro.kovari.plugins.dns.spi;



import ro.kovari.plugins.dns.spi.exception.DnsValidationClientException;

public interface DnsValidationClient {

    /**
     * Add a new TXT record.<br><br>
     * Note: the <code>fqdn</code> already contains the '_acme-challenge' subdomain
     *
     * @param fqdn the domain name
     * @param txt the TXT data to be added
     * @return true on success, false otherwise
     */
    public boolean addTxtRecord(String fqdn, String txt) throws DnsValidationClientException;

    /**
     * Delete the previously added TXT record.
     * It is the plugin's job to keep track of the previously added TXT record in order to
     * be able to identify it and remove it.
     *
     * @return true on success, false otherwise
     */
    public boolean deleteTxtRecord();
}
