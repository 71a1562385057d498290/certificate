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



import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ro.kovari.http.base.*;
import ro.kovari.http.client.HttpClient;
import ro.kovari.http.client.HttpRequest;
import ro.kovari.http.client.HttpResponse;
import ro.kovari.jndi.DnsClient;
import ro.kovari.plugins.dns.spi.DnsValidationClient;
import ro.kovari.plugins.dns.spi.exception.DnsValidationClientException;

import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;



/**
 * {@link DnsValidationClient} implementation for Zone.ee.<br>
 * The API_URL has the following form:<br>
 * <pre>
 *  https://api.zone.eu/v2/{service_type}/{service_name}/{resource_name}
 * </pre>
 * Where:
 * <pre>
 *  {service_type}  is the literal 'dns'
 *  {service_name}  is the domain name to be used
 *  {resource_name} is the literal 'txt'
 * </pre>
 */
public final class ZoneEEClient implements DnsValidationClient {

    private static final Logger logger = Logger.getLogger(ZoneEEClient.class.getName());
    private final ZoneEEConfiguration config;

    private String deleteURL;



    public ZoneEEClient(Properties configProperties) {
        this.config = ZoneEEConfiguration.getConfigurationFromProperties(configProperties);
    }



    /**
     * Add a new TXT record to the specified <code>fqdn</code>.
     * By default the <code>fqdn</code> has the following form:
     * <pre>
     *      _acme-challenge.subdomain.domain.tld
     * </pre>
     * If the <code>fqdn</code> has a CNAME then the TXT record will be added
     * to the CNAME at the apex of its zone.<br>
     * For example, consider the following case:
     * <pre>
     *      1. _acme-challenge.subdomain.domain.tld. 3600 IN CNAME subdomain.other-domain.tld.
     *      2. the zone apex of <i>subdomain.other-domain.tld.</i> is <i>other-domain.tld.</i>
     *
     *      In this case the TXT record will be added to <i>subdomain.other-domain.tld.</i> at
     *      its apex: <i>other-domain.tld.</i>
     * </pre>
     *
     * @param fqdn the domain where the txt record is to be added
     * @param txt the TXT data to be added
     * @throws DnsValidationClientException if a zone apex can not be determined
     */
    @Override
    public boolean addTxtRecord(String fqdn, String txt) {
        boolean result = false;
        DnsClient dnsClient = new DnsClient();
        String currentDomain = fqdn;

        List<String> cname = dnsClient.getCname(fqdn);
        if (!cname.isEmpty()) {
            String currentDomainCName = cname.get(0);
            logger.info(String.format("CNAME found for %s: %s", currentDomain, currentDomainCName));
            currentDomain = currentDomainCName;
        }

        String serviceName = dnsClient.getZoneApexAsOptional(currentDomain)
                                      .orElseThrow(() -> new DnsValidationClientException("No apex available"));
        logger.info("Found service name: " + serviceName);

        String postURL = String.format(config.getApiURL(), serviceName, "");
        String json = new ZoneEERecord(currentDomain, txt).toString();
        logger.info("Adding DNS record: " + json);

        HttpResponse response = post(postURL, json);
        logger.info(response.toString());
        if (response.getStatus().getFamily().equals(Status.Family.SUCCESSFUL)) {
            JsonObject jsonObject = new JsonParser().parse(response.getContentAsString())
                                                    .getAsJsonArray()
                                                    .get(0)
                                                    .getAsJsonObject();
            String recordId = jsonObject.get("id").getAsString();
            deleteURL = String.format(config.getApiURL(), serviceName, recordId);
            result = true;
        }
        return result;
    }



    /** Delete a previously added TXT record */
    @Override
    public boolean deleteTxtRecord() {
        boolean result = false;
        if (!Objects.isNull(deleteURL)) {
            logger.info("Deleting TXT record: " + deleteURL);

            HttpResponse response = delete(deleteURL);
            logger.info(response.toString());
            if (response.getStatusCode() == Status.HTTP_NO_CONTENT) {
                result = true;
            }

        } else {
            logger.warning("No URL is defined for the DELETE operation. No record deleted.");
        }
        return result;
    }



    private HttpResponse post(String url, String content) {
        // remove the trailing slash resulted from the way the API_URL is specified
        // in the configuration file
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.POST)
                .using(Header.newInstance()
                             .add("Authorization", config.getEncodedBasicAuth())
                             .add(Header.Field.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .using(RequestBody.newInstance()
                                  .setContent(content))
                .build();

        return HttpClient.execute(request);
    }



    private HttpResponse delete(String url) {
        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.DELETE)
                .using(Header.newInstance()
                             .add("Authorization", config.getEncodedBasicAuth()))
                .build();

        return HttpClient.execute(request);
    }
}
