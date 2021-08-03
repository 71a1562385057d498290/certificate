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



package ro.kovari.plugins.provider;

import ro.kovari.plugins.dns.spi.DnsValidationClient;

import java.io.PrintWriter;


public class DefaultClient implements DnsValidationClient {

    private final PrintWriter out = new PrintWriter(System.out, true);



    @Override
    public boolean addTxtRecord(String domain, String txt) {
        out.println("Please updated your DNS records with the following data: ");

        out.println("\tDOMAIN: " + domain);
        out.println("\tTXT record: " + txt + System.lineSeparator());
        return false; // no dns record is created by this plugin
    }



    @Override
    public boolean deleteTxtRecord() {
        out.println("Nothing to clean up!");
        return false; // no dns record is deleted by this plugin
    }
}
