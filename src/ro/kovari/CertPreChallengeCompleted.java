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

import ro.kovari.jndi.DnsClient;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class CertPreChallengeCompleted {

    private static final Logger logger = Logger.getLogger(CertPreChallengeCompleted.class.getName());

    private final DnsClient dnsClient = new DnsClient();



    /**
     * Performs a pre-"challenge completed" validation.
     * That means that before a "challenge completed" notification is sent to the ACME server,
     * a check is made to see if all the responsible DNS servers return the same TXT value.<br><br>
     * Note: The check is stopped when all servers return the same TXT value or after 180 retries,
     * whichever comes first. Between each retry a 1 minute pause is made. That means that the max
     * wait time before giving up is 3h.<br><br>
     * Note: No exceptions are thrown by this method.
     *
     * @param domain the domain holding the TXT record to be checked
     * @param expectedTxt the expected TXT value
     * @return true if all DNS servers return the same expected TXT value, false otherwise
     */
    public boolean validate(String domain, String expectedTxt) {
        String currentDomain = domain;
        boolean inSync = false;

        List<String> cname = dnsClient.getCname(currentDomain);
        if (!cname.isEmpty()) {
            String currentDomainCName = cname.get(0);
            logger.info(String.format("CNAME found for %s: %s", currentDomain, currentDomainCName));
            currentDomain = currentDomainCName;
        }

        List<String> apex = dnsClient.getZoneApex(currentDomain);
        if (!apex.isEmpty()) {
            List<String> nameServers = dnsClient.getNameServers(apex.get(0));
            if (!nameServers.isEmpty()) {
                int minutes = 0;
                while (!inSync && minutes < 180) {
                    Collections.shuffle(nameServers);
                    try {
                        Thread.sleep(60000);
                        minutes++;
                        logger.info("Minutes passed: " + minutes);
                        inSync = nameServersInSync(nameServers, currentDomain, expectedTxt);
                    } catch (InterruptedException e) {
                        logger.log(Level.WARNING, "Sleep interrupted", e);
                    }
                }
            }
        }
        return inSync;
    }



    /**
     * Check if all the responsible DNS servers return the same TXT value.<br><br>
     * Note: If no or multiple TXT records are present the result is considered a mismatch.
     * In the case of multiple TXT records, that happens even if the correct record is present in
     * addition to the incorrect ones. Check stops on the first mismatch.
     *
     * @return true if all DNS servers return the same expected TXT value, false otherwise
     */
    private boolean nameServersInSync(List<String> nameServers, String domain, String expectedTxt) {
        boolean match = true;
        if (!nameServers.isEmpty()) {
            for (String ns : nameServers) {
                logger.info("Current NS: " + ns);
                List<String> actualTxt = dnsClient.getTxt(ns, domain);
                logger.info(String.format("Got TXT %s for %s from NS %s", actualTxt, domain, ns));

                if (!actualTxt.contains(expectedTxt)) {
                    match = false;
                    break;
                }
            }
        }
        logger.info("All NSs return the same TXT value: " + (match ? "yes" : "no"));
        return match;
    }
}
