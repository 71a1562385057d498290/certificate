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

package ro.kovari.plugins.provider.zoneee;



import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;



final class ZoneEEConfiguration {

    private final String apiURL;
    private final String apiKey;
    private final String encodedBasicAuth;
    private final String userId;



    private ZoneEEConfiguration(String apiURL, String apiKey, String userId) {
        this.apiURL = apiURL;
        this.apiKey = apiKey;
        this.userId = userId;
        this.encodedBasicAuth = "Basic " + encodeBasicAuth(this.userId, this.apiKey);
    }



    static ZoneEEConfiguration getConfigurationFromProperties(Properties properties) {
        String apiURL = properties.getProperty("API_URL");
        String apiKey = properties.getProperty("API_KEY");
        String userId = properties.getProperty("USER_ID");

        return new ZoneEEConfiguration(apiURL, apiKey, userId);
    }



    private String encodeBasicAuth(String username, String password) {
        byte[] bytes = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }



    String getApiURL() {
        return apiURL;
    }



    String getEncodedBasicAuth() {
        return encodedBasicAuth;
    }
}