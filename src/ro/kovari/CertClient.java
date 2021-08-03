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

import org.bouncycastle.operator.OperatorCreationException;
import ro.kovari.config.ConfigurationException;
import ro.kovari.config.ConfigurationReader;
import ro.kovari.crypto.PKCS10CertRequest;
import ro.kovari.model.AccountIdentity;
import ro.kovari.model.DomainsIdentity;
import ro.kovari.model.acme.Authorization;
import ro.kovari.model.acme.CertSigningRequest;
import ro.kovari.model.acme.Challenge;
import ro.kovari.model.acme.Order;
import ro.kovari.params.Arguments;
import ro.kovari.plugins.dns.DnsValidationService;
import ro.kovari.plugins.dns.spi.DnsValidationClient;
import ro.kovari.plugins.dns.spi.DnsValidationProvider;
import ro.kovari.storage.FileStorage;
import ro.kovari.utils.Constants;
import ro.kovari.utils.Encoder;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public final class CertClient {

    private static final Logger logger = Logger.getLogger(CertClient.class.getName());

    private final CertClientSession session;
    private final CertClientAcme acmeClient;

    private FileStorage storage;



    /**
     * Bind together the client, the session and the ACME client
     *
     * @param session a {@link CertClientSession} instance used to exchange data
     * between the client and the ACME client
     * @param acmeClient a {@link CertClientAcme} instance representing
     * the ACME client
     */
    private CertClient(CertClientSession session, CertClientAcme acmeClient) {
        this.session = session;
        this.acmeClient = acmeClient;
    }



    /**
     * Get a new {@link CertClient} instance.<br>
     * Account and domain keys are created and the new {@link CertClientSession}
     * and {@link CertClientAcme} instances are initialized.
     *
     * @param arguments an {@link Arguments} instance representing the processed
     * and validated CLI arguments
     * @return a new {@link CertClient} instance
     */
    public static CertClient bootstrap(Arguments arguments) {
        logger.info("Generating account keys ...");
        AccountIdentity aIdentity = new AccountIdentity(arguments.getContacts());
        logger.info("Done.");

        logger.info("Generating domain keys ...");
        DomainsIdentity dIdentity = new DomainsIdentity(arguments.getDomains());
        logger.info("Done.");

        CertClientSession session = new CertClientSession()
                .setIdentity(aIdentity)
                .setDomainsIdentity(dIdentity)
                .setEnvironment(arguments.getEnvironment())
                .setProvider(arguments.getProvider())
                .setAutoModeEnabled(arguments.isAutoSet());

        CertClientAcme acmeClient = CertClientAcme.bootstrap(session);
        return new CertClient(session, acmeClient);
    }



    /**
     * Set the {@link FileStorage} for this {@link CertClient}
     *
     * @param storage the {@link FileStorage}
     */
    public void setStorage(FileStorage storage) {
        this.storage = storage;
    }



    /**
     * Returns true if this {@link CertClient} has a {@link FileStorage} attached.
     *
     * @return true if this {@link CertClient} has a {@link FileStorage} attached, false otherwise
     */
    private boolean hasStorageAttached() {
        return Objects.nonNull(storage);
    }



    /**
     * Request a new certificate from a previously configured CA.
     * The CA specified in the {@link CertEnvironment} argument is used
     */
    public void requestNewCertificate() {
        AccountIdentity aIdentity = session.getIdentity();
        boolean isExternalIdentity = aIdentity.isExternalIdentityProvided();
        if (isExternalIdentity) {
            acmeClient.returnExistingAccount();
        } else {
            acmeClient.createNewAccount();
        }

        Order order = acmeClient.createNewOrder();
        authorizeOrder(order);

        DomainsIdentity dIdentity = session.getDomainsIdentity();
        byte[] csrBytes;
        try {
            csrBytes = PKCS10CertRequest.createCSR(dIdentity);
            session.setCertificateRequest(csrBytes);

        } catch (IOException | OperatorCreationException e) {
            throw new RuntimeException(Constants.EXCEPTION_CREATING_CSR, e);
        }

        CertSigningRequest csr = new CertSigningRequest(Encoder.base64(csrBytes));
        acmeClient.finalizeOrder(order, csr);
        acmeClient.pollFinalizedOrderStatus(order, Constants.POLL_FINALIZE_MAX_RETRIES);

        order = acmeClient.getOrderFromURL(order.getURL());
        byte[] certificate = acmeClient.getCertificate(order);
        session.setCertificate(certificate);

        persistResults();
    }



    /** Persist all the data: account and domain keys, csr and certificate */
    private void persistResults() {
        if (hasStorageAttached()) {
            logger.info("Persisting data ...");
            storage.getFileStoredEnvironment().getFileStoredAccount()
                   .setKeyPair(session.getIdentity().getKeyPair());

            storage.getFileStoredEnvironment().getFileStoredAccount().getFileStoredDomain()
                   .setKeyPair(session.getDomainsIdentity().getKeyPair())
                   .setCertificateRequest(session.getCertificateRequest())
                   .setCertificate(session.getCertificate());

            storage.persist();
            logger.info("Done.");
        }
    }



    /**
     * Authorize the order by resolving the authorization challenges
     *
     * @param order the {@link Order} to authorize
     * @throws IllegalArgumentException in case the specified {@link Order} is null
     */
    private void authorizeOrder(Order order) throws IllegalArgumentException {
        if (Objects.isNull(order))
            throw new IllegalArgumentException();

        for (String authURL : order.getAuthorizations()) {
            logger.info("Resolving challenge:" + authURL);
            authorizeChallenge(authURL);
        }
    }



    /**
     * Resolve a challenge for the authorization specified by the <code>authURL</code>
     *
     * @param authURL the authorization URL
     */
    private void authorizeChallenge(String authURL) {
        Authorization auth = acmeClient.getAuthorization(authURL);
        String identifier = auth.getIdentifier().getValue();
        logger.info("Authorizing identifier: " + identifier);

        String fqdn = "_acme-challenge." + identifier;
        Challenge challenge = acmeClient.getChallenge(auth);
        String txt = acmeClient.getChallengeKeyAuthorization(challenge.getToken());

        String provider = session.getProvider();
        // when the provider is not specified the default provider is used.
        // if specified, the ProviderValidator makes sure that the specified provider
        // is valid and it is not the default provider.
        boolean isProviderValid = Objects.nonNull(provider) && !provider.isEmpty();
        provider = isProviderValid ? provider : Constants.DEFAULT_DNS_PROVIDER;

        //DnsValidationService dnsService = new DnsValidationService();
        DnsValidationProvider dnsProvider = DnsValidationService.getDnsProvider(provider);
        Properties providerConfigProperties = null;

        //TODO Do not try to load configuration for the default provider - when isProviderValid is false.
        //TODO Maybe load the provider and the provider configuration when creating the session.
        try {
            providerConfigProperties = ConfigurationReader.getProperties(dnsProvider.getName() + ".properties");
        } catch (ConfigurationException e) {
            logger.log(Level.FINE, "Configuration exception", e);
            logger.severe("Could not load configuration properties: " + e.getMessage());
        }
        DnsValidationClient dnsClient = dnsProvider.createClient(providerConfigProperties);

        boolean dnsRecordAdded = dnsClient.addTxtRecord(fqdn, txt);

        // auto mode is enabled only if a provider different than the default provider is used
        // and only if the provider managed to successfully create the specified TXT record.
        if (isProviderValid && session.getAutoModeEnabled() && dnsRecordAdded) {
            CertPreChallengeCompleted validator = new CertPreChallengeCompleted();
            boolean inSync = validator.validate(fqdn, txt);

        } else {
            pauseAuthorization(); // wait for user input
        }

        try {
            acmeClient.notifyChallengeCompleted(challenge.getURL());
            acmeClient.pollChallengeStatus(challenge, Constants.POLL_CHALLENGE_MAX_RETRIES);

        } catch (RuntimeException authorizationException) {
            logger.log(Level.SEVERE, "Authorization exception", authorizationException);
            throw authorizationException; // only caught for logging purposes; rethrow it

        } finally {
            if (dnsRecordAdded) {
                dnsClient.deleteTxtRecord();
            }
        }
    }



    /**
     * Pause the authorization procedure until ENTER is pressed,
     * giving time for the DNS propagation.
     */
    private void pauseAuthorization() {
        System.out.println(Constants.MESSAGE_DNS_COMPLETE_CHALLENGE);
        Scanner scanner = new Scanner(System.in);

        try {
            scanner.nextLine();
        } catch (IllegalStateException | NoSuchElementException e) {
            logger.warning(Constants.EXCEPTION_PAUSING);
        }
    }
}
