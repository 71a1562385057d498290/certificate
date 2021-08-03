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

package ro.kovari.jndi;

import ro.kovari.utils.StringUtils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;



public class DnsClient {

    private static final Logger logger = Logger.getLogger(DnsClient.class.getName());

    private static final String DNS_CONTEXT_FACTORY = "com.sun.jndi.dns.DnsContextFactory";
    private static final String DNS_PSEUDO_URL = "dns://%s/%s";



    /**
     * Performs a DNS query at the specified <code>dnsPseudoURL</code>, searching
     * for the specified {@link DnsRecordType}s.<br>
     * The DNS pseudo-URL has the following format:
     * <pre>
     *     dns:[//host[:port]][/domain]
     * </pre>
     * For more details see
     * <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-dns.html#URL">
     * DNS pseudo URLs</a><br><br>
     * Note: on failure an empty {@link List} is returned.
     *
     * @param dnsPseudoURL the DNS pseudo URL
     * @param type the record types to return
     * @return a {@link List} containing the requested records
     */
    public List<String> performDnsLookup(String dnsPseudoURL, DnsRecordType type) {
        List<String> dnsLookupResult = new ArrayList<>();

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, DNS_CONTEXT_FACTORY);

            DirContext ctx = new InitialDirContext(env);
            Attributes attributes = ctx.getAttributes(dnsPseudoURL, new String[] { type.name() });

            NamingEnumeration attributesEnumeration = attributes.getAll();
            while (attributesEnumeration.hasMore()) {
                Attribute attribute = (Attribute) attributesEnumeration.next();

                NamingEnumeration attributeEnumeration = attribute.getAll();
                while (attributeEnumeration.hasMore()) {
                    Object current = attributeEnumeration.next();
                    if (current instanceof String) {
                        String value = (String) current;
                        value = StringUtils.removeFinalDot(value);
                        dnsLookupResult.add(value);
                    }
                }
            }
        } catch (NamingException e) {
            logger.info("Issue: " + e.getMessage() + " " + e.getRemainingName().toString());
            logger.log(Level.FINE, "A DNS lookup exception occurred", e);
        }
        return dnsLookupResult;
    }



    /**
     * Get a list containing the entire domain hierarchy, starting from the domain and going up.<br>
     * For example, for a domain <code>_acme-challenge.subdomain.domain.tld</code> the list will contain
     * the following entries:<br>
     * <pre>
     *      _acme-challenge.subdomain.domain.tld
     *      subdomain.domain.tld
     *      domain.tld
     * </pre>
     * The provided <code>domainName</code> is always the first item to be added to the list.
     *
     * @param domain the domain name
     * @return a {@link List} containing the computed domain hierarchy
     */
    public List<String> getDomainHierarchy(String domain) {
        List<String> domains = new ArrayList<>();

        if (Objects.isNull(domain) || domain.isEmpty()) {
            logger.warning("Domain must not be 'null' or empty");
            return domains;
        }
        if (domain.endsWith(".")) {
            logger.warning("Domain must not start or end with '.'");
            return domains;
        }
        domains.add(domain);

        String dn = domain;
        int eIdx, sIdx;
        while ((sIdx = dn.indexOf('.')) != -1 && (eIdx = dn.lastIndexOf('.')) != -1 && sIdx != eIdx) {
            dn = dn.substring(dn.indexOf('.') + 1);
            domains.add(dn);
        }
        return domains;
    }



    /**
     * Get the zone apex for the specified domain<br><br>
     * Note: The zone apex must contain a SOA record and one or more NS records.<br>
     * On failure an empty {@link List} is returned.
     *
     * @param domain the domain name for which the zone apex is to be found
     * @return a {@link List} containing the zone apex
     */
    public List<String> getZoneApex(String domain) {
        List<String> apex = new ArrayList<>();

        List<String> domainHierarchy = getDomainHierarchy(domain);
        logger.info("Searching SOA and NSs in hierarchy: " + domainHierarchy);

        if (!domainHierarchy.isEmpty()) {
            for (String currentDomain : domainHierarchy) {
                logger.info("Getting SOA for: " + currentDomain);
                List<String> soa = getSoa(currentDomain);

                if (!soa.isEmpty()) {
                    logger.info("Getting NSs for: " + currentDomain);
                    List<String> nameServers = getNameServers(currentDomain);

                    if (!nameServers.isEmpty()) {
                        apex.add(currentDomain);
                        logger.info(String.format("Found zone apex at: %s", currentDomain));
                        break;
                    }
                }
            }
        }
        return apex;
    }



    /**
     * Get the zone apex for the specified domain and return the result as an {@link Optional}<br><br>
     * Note: The zone apex must contain a SOA record and one or more NS records.<br>
     * On failure an empty {@link Optional} is returned.
     *
     * @param domain the domain name for which the zone apex is to be found
     * @return an {@link Optional} containing the zone apex
     */
    public Optional<String> getZoneApexAsOptional(String domain) {
        Optional<String> apex = Optional.empty();

        List<String> apexList = getZoneApex(domain);
        if (!apexList.isEmpty()) {
            apex = Optional.of(apexList.get(0));
        }
        return apex;
    }



    /**
     * Get the zone apex for the CNAME of the specified domain.
     * If the specified domain has no CNAME then the apex of
     * the specified domain is returned.<br><br>
     * Note: The zone apex must contain a SOA record and one or more NS records.<br>
     * An empty {@link List} is returned in case of failure.
     *
     * @param domain the domain name
     * @return a {@link List} containing the zone apex
     */
    public List<String> getCnameZoneApex(String domain) {
        String currentDomain = domain;
        List<String> apex = new ArrayList<>();

        List<String> cname = getCname(currentDomain);
        if (!cname.isEmpty()) {
            String currentDomainCName = cname.get(0);
            logger.info(String.format("CNAME found for %s: %s", currentDomain, currentDomainCName));
            currentDomain = currentDomainCName;
        }
        return getZoneApex(currentDomain);
    }



    /**
     * Using the system's default DNS settings, query for the NS records of a domain.
     *
     * @param domain the domain name
     * @return a {@link List} containing the NS records or an empty list in case of failure
     */
    public List<String> getNameServers(String domain) {
        return performDnsLookup(String.format(DNS_PSEUDO_URL, "", domain), DnsRecordType.NS);
    }



    /**
     * Using the system's default DNS settings, query for the CNAME records of a domain.
     *
     * @param domain the domain name
     * @return a {@link List} containing the CNAME records or an empty list in case of failure
     */
    public List<String> getCname(String domain) {
        logger.info("Get CNAME for: " + domain);
        return performDnsLookup(String.format(DNS_PSEUDO_URL, "", domain), DnsRecordType.CNAME);
    }



    /**
     * Using the system's default DNS settings, query for the TXT records of a domain.<br><br>
     * Note: If present, start and end quotation marks are removed from the result.
     *
     * @param domain the domain name
     * @return a {@link List} containing the TXT records or an empty list in case of failure
     */
    public List<String> getTxt(String domain) {
        return performDnsLookup(String.format(DNS_PSEUDO_URL, "", domain), DnsRecordType.TXT)
                .stream()
                .map(StringUtils::removeQuotationMarks)
                .collect(Collectors.toList());
    }



    /**
     * Using the specified NS, query for the TXT records of a domain<br><br>
     * Note: If present, start and end quotation marks are removed from the result.
     *
     * @param nameServer the NS to be queried
     * @param domain the domain name
     * @return a {@link List} containing the TXT records or an empty list in case of failure
     */
    public List<String> getTxt(String nameServer, String domain) {
        return performDnsLookup(String.format(DNS_PSEUDO_URL, nameServer, domain), DnsRecordType.TXT)
                .stream()
                .map(StringUtils::removeQuotationMarks)
                .collect(Collectors.toList());
    }



    /**
     * Using the system's default DNS settings, query for the SOA record of a domain
     *
     * @param domain the domain name
     * @return a {@link List} containing the SOA record or an empty list in case of failure
     */
    public List<String> getSoa(String domain) {
        return performDnsLookup(String.format(DNS_PSEUDO_URL, "", domain), DnsRecordType.SOA);
    }



    /**
     * Using the specified NS, query for the SOA record of a domain
     *
     * @param domain the domain name
     * @return a {@link List} containing the SOA record or an empty list in case of failure
     */
    public List<String> getSoa(String ns, String domain) {
        return performDnsLookup(String.format(DNS_PSEUDO_URL, ns, domain), DnsRecordType.SOA);
    }



    /** An enumeration of DNS record types */
    public enum DnsRecordType {
        A, NS, CNAME, SOA, PTR, MX, TXT, HINFO, AAAA, NAPTR, SRV;
    }
}