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

import ro.kovari.http.base.Header;
import ro.kovari.http.base.MediaType;
import ro.kovari.http.base.Method;
import ro.kovari.http.base.RequestBody;
import ro.kovari.http.client.HttpClient;
import ro.kovari.http.client.HttpRequest;
import ro.kovari.http.client.HttpResponse;

import java.util.Objects;
import java.util.logging.Logger;



public class CertHttpClient {

    private static final Logger logger = Logger.getLogger(CertHttpClient.class.getName());



    public static HttpResponse head(String url) {
        urlSanityCheck(url);
        logger.info("HEAD: " + url);
        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.HEAD)
                .build();
        return HttpClient.execute(request);
    }



    public static HttpResponse get(String url) {
        urlSanityCheck(url);
        logger.info("GET: " + url);
        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.GET)
                .build();
        return HttpClient.execute(request);
    }



    public static HttpResponse post(String url, String content) {
        urlSanityCheck(url);
        logger.info("POST: " + url);
        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.POST)
                .using(Header.newInstance()
                             .add(Header.Field.ACCEPT, MediaType.ANY)
                             .add(Header.Field.CONTENT_TYPE, MediaType.APPLICATION_JOSE_JSON))
                .using(RequestBody.newInstance()
                                  .setContent(content))
                .build();

        logger.fine(request.toString());
        HttpResponse response = HttpClient.execute(request);
        logger.fine(response.toString());

        return response;
    }



    private static void urlSanityCheck(String url) {
        if (Objects.isNull(url) || url.isEmpty()) throw new RuntimeException("Invalid URL");
    }
}
