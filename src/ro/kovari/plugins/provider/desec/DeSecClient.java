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

package ro.kovari.plugins.provider.desec;

import com.google.gson.Gson;
import ro.kovari.http.base.*;
import ro.kovari.http.client.HttpClient;
import ro.kovari.http.client.HttpRequest;
import ro.kovari.http.client.HttpResponse;
import ro.kovari.jndi.DnsClient;
import ro.kovari.plugins.dns.spi.DnsValidationClient;
import ro.kovari.plugins.dns.spi.exception.DnsValidationClientException;
import ro.kovari.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;


/** {@link DnsValidationClient} implementation for deSEC.io */
public class DeSecClient implements DnsValidationClient {

    private static final Logger logger = Logger.getLogger(DeSecClient.class.getName());
    private final DeSecConfiguration config;

    private String deleteURL;
    private String deleteTxtRecord;



    public DeSecClient(Properties configProperties) {
        this.config = DeSecConfiguration.getConfigurationFromProperties(configProperties);
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
     * deSEC URL: https://desec.io/api/v1/domains/{name}/rrsets/
     *
     * @param fqdn the domain where the txt record is to be added
     * @param txt the TXT data to be added
     * @throws DnsValidationClientException if a zone apex can not be determined
     */
    @Override
    public boolean addTxtRecord(String fqdn, String txt) throws DnsValidationClientException {
        boolean result = false;
        DnsClient dnsClient = new DnsClient();
        String dnsRRSetType = DnsClient.DnsRecordType.TXT.name();

        String currentDomain = fqdn;
        String currentTxtRecord = StringUtils.addQuotationMarks(txt);

        List<String> cname = dnsClient.getCname(fqdn);
        if (!cname.isEmpty()) {
            String currentDomainCName = cname.get(0);
            logger.info(String.format("CNAME found for %s: %s", currentDomain, currentDomainCName));
            currentDomain = currentDomainCName;
        }

        String name = dnsClient.getZoneApexAsOptional(currentDomain)
                               .orElseThrow(() -> new DnsValidationClientException("No apex available"));
        logger.info("Found name: " + name);
        String subname = StringUtils.removeFinalDot(currentDomain.replaceAll(name, ""));
        logger.info("Found subname: " + subname);

        String url = String.format(config.getApiURL(), name);

        // if a previous RRSet already exists, we can get it from this URL.
        // the special '@' case needs to be used in order to access the records in the zone apex
        String existingRRSetURL = url + (subname.isEmpty() ? "@" : subname) + "/" + dnsRRSetType;

        HttpResponse response = get(existingRRSetURL);
        if (response.getStatusCode() == Status.HTTP_OK) { // Previous RRset
            logger.info("Previous RRset present: " + response.toString());

            DeSecRecordSet recordSet = deserialize(response.getContentAsString());
            List<String> allTxtRecords = recordSet.getRecords();
            allTxtRecords.add(currentTxtRecord);

            logger.info("Updating RRset with new TXT record: " + allTxtRecords);
            response = postOrPut(existingRRSetURL, Method.PUT, recordSet.toString());
            logger.info(response.toString());

        } else if (response.getStatusCode() == Status.HTTP_NOT_FOUND) { // No previous RRset
            logger.info("No previous RRset present.");
            logger.fine(response.toString());

            List<String> txtRecord = new ArrayList<>();
            txtRecord.add(currentTxtRecord);

            logger.info("Adding new TXT record: " + txtRecord);
            DeSecRecordSet recordSet = new DeSecRecordSet(subname, dnsRRSetType, txtRecord);
            response = postOrPut(url, Method.POST, recordSet.toString());
            logger.info(response.toString());
        }

        // check if the final response code is 200 or 201
        if (response.getStatusCode() == Status.HTTP_OK || response.getStatusCode() == Status.HTTP_CREATED) {
            deleteURL = existingRRSetURL;
            deleteTxtRecord = currentTxtRecord;
            result = true;

        } else {
            logger.warning(response.toString());
        }
        return result;
    }



    /** Delete a previously added TXT record */
    @Override
    public boolean deleteTxtRecord() {
        boolean result = false;
        if (Objects.isNull(deleteURL) || deleteURL.isEmpty()
                || Objects.isNull(deleteTxtRecord) || deleteTxtRecord.isEmpty()) {

            logger.info("No URL is defined for the DELETE operation. Nothing to do.");
            return false;
        }

        HttpResponse response = get(deleteURL);
        if (response.getStatusCode() == Status.HTTP_OK) { // RRset found

            DeSecRecordSet recordSet = deserialize(response.getContentAsString());
            List<String> allTxtRecords = recordSet.getRecords();
            allTxtRecords.remove(deleteTxtRecord);

            logger.info("Deleting TXT record: [" + deleteTxtRecord + "]");
            response = postOrPut(deleteURL, Method.PUT, recordSet.toString());
            logger.info(response.toString());

            if (response.getStatusCode() == Status.HTTP_OK ||
                    response.getStatusCode() == Status.HTTP_NO_CONTENT) {
                result = true;
            }
        }
        return result;
    }



    private HttpResponse get(String url) {
        HttpRequest request = HttpRequest
                .from(url)
                .using(Method.GET)
                .using(Header.newInstance()
                             .add("Authorization", config.getAuthHeaderField())
                             .add(Header.Field.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .build();
        return HttpClient.execute(request);
    }



    private HttpResponse postOrPut(String url, Method method, String content) {
        HttpRequest request = HttpRequest
                .from(url)
                .using(method)
                .using(Header.newInstance()
                             .add("Authorization", config.getAuthHeaderField())
                             .add(Header.Field.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .using(RequestBody.newInstance()
                                  .setContent(content))
                .build();
        return HttpClient.execute(request);
    }



    private DeSecRecordSet deserialize(String content) {
        return new Gson().fromJson(content, DeSecRecordSet.class);
    }
}
