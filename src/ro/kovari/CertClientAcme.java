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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ro.kovari.crypto.KeyTool;
import ro.kovari.http.base.Header;
import ro.kovari.http.base.Status;
import ro.kovari.http.client.HttpResponse;
import ro.kovari.model.AccountIdentity;
import ro.kovari.model.acme.*;
import ro.kovari.utils.*;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;



public final class CertClientAcme {

    private static final Logger logger = Logger.getLogger(CertClientAcme.class.getName());
    private final CertClientSession session;



    private CertClientAcme(final CertClientSession session) {
        this.session = session;
    }



    /**
     * Bootstrap the {@link CertClientAcme}: compute the JWK needed by the ACME operations
     * and get the Directory object containing the resources listed by the ACME server
     *
     * @param session the {@link CertClientSession} object to be used by this {@link CertClientAcme}
     * @return a new {@link CertClientAcme} object
     */
    public static CertClientAcme bootstrap(final CertClientSession session) {
        CertClientAcme acmeClient = new CertClientAcme(session);

        RSAPublicKey pk = (RSAPublicKey) session.getIdentity().getKeyPair().getPublic();
        byte[] pubExp = BigIntUtils.stripLeadingZeros(pk.getPublicExponent());
        byte[] pubMod = BigIntUtils.stripLeadingZeros(pk.getModulus());

        AccountIdentity.JsonWebKey jwk =
                new AccountIdentity.JsonWebKey(Encoder.base64(pubExp), "RSA", Encoder.base64(pubMod));
        session.setAccountKey(jwk);
        Directory directory = acmeClient.getDirectory(); // external https call performed
        if (directory.getMeta().isExternalAccountRequired()) {
            throw new UnsupportedOperationException(Constants.EXCEPTION_EXTERNAL_ACCOUNT_REQUIRED);
        }
        session.setDirectory(directory);
        return acmeClient;
    }



    /**
     * Get the {@link CertClientSession} object of this {@link CertClientAcme}
     *
     * @return the {@link CertClientSession} object of this {@link CertClientAcme}
     */
    public CertClientSession getSession() {
        return session;
    }



    /**
     * Get the {@link Directory} object containing
     * all the resources listed by the ACME server
     *
     * @return the {@link Directory} object
     */
    public Directory getDirectory() {
        HttpResponse response = getRequest(session.getEnvironment().getDirectoryURL());
        return JsonMapper.deserialize(response.getContentAsString(), Directory.class);
    }



    /**
     * Get a new reply-nonce from the ACME server
     *
     * @return the new reply-nonce
     */
    public String getNewNonce() {
        HttpResponse response = headRequest(session.getDirectory().getNewNonceLocation());
        return response.getHeaderFieldFirst(Constants.HEADER_REPLAY_NONCE);
    }



    /**
     * Create a new ACME account
     *
     * @return the newly created account
     */
    public Account createNewAccount() {
        logger.info("Creating new account ...");
        return createNewOrReturnExistingAccount(false);
    }



    /**
     * Get an {@link Account} corresponding to an existing ACME account
     *
     * @return an {@link Account} corresponding to an existing ACME account
     */
    public Account returnExistingAccount() {
        logger.info("Returning existing account ...");
        return createNewOrReturnExistingAccount(true);
    }



    /**
     * Create or return an existing ACME account
     *
     * @param returnExisting true in order to return an existing account, false otherwise
     * @return an {@link Account} corresponding to a new or an existing ACME account
     */
    private Account createNewOrReturnExistingAccount(boolean returnExisting) {
        Account account = new Account().setTermsOfServiceAgreed(true)
                                       .addContacts(session.getIdentity().getFormattedContacts())
                                       .setOnlyReturnExisting(returnExisting);

        HttpResponse httpResponse = postRequest(session.getDirectory().getNewAccountLocation(), account);
        account = JsonMapper.deserialize(httpResponse.getContentAsString(), Account.class);
        session.setAccountKeyID(httpResponse.getHeaderFieldFirst(Constants.HEADER_LOCATION));
        validateAcmeStatus(account);
        logger.info("Account created or returned: " + session.getAccountKeyID());

        return account;
    }



    /**
     * Get all the available orders for an account
     *
     * @param account the {@link Account}
     * @return the {@link Orders}
     */
    public Orders getAccountOrders(Account account) {
        String ordersURL = account.getOrdersURL();
        HttpResponse httpResponse = postAsGetRequest(ordersURL);
        validateHttpResponse(httpResponse);
        return JsonMapper.deserialize(httpResponse.getContentAsString(), Orders.class);
    }



    /**
     * Create a new ACME order
     *
     * @return the newly created {@link Order}
     */
    public Order createNewOrder() {
        logger.info("Creating new order ...");

        List<Identifier> identifiers = new ArrayList<>();
        List<String> domains = session.getDomainsIdentity().getDomains();
        domains.forEach(domain -> {
            identifiers.add(new Identifier("dns", domain));
        });

        Order order = new Order();
        order.addAllIdentifiers(identifiers);

        HttpResponse httpResponse = postRequest(session.getDirectory().getNewOrderLocation(), order);
        order = JsonMapper.deserialize(httpResponse.getContentAsString(), Order.class);
        order.setURL(httpResponse.getHeaderFieldFirst(Constants.HEADER_LOCATION));
        validateAcmeStatus(order);
        logger.info("Order created: " + order.getURL());
        return order;
    }



    /**
     * Get an ACME {@link Order} from the specified URL
     *
     * @param url the URL of the {@link Order}
     * @return the ACME {@link Order} from the specified URL
     */
    public Order getOrderFromURL(String url) {
        HttpResponse httpResponse = postAsGetRequest(url);
        Order order = JsonMapper.deserialize(httpResponse.getContentAsString(), Order.class);
        validateAcmeStatus(order);
        // URL is not a standard Order ACME field so we must re-include it manually
        // This is only a temp solution!
        order.setURL(url);

        return order;
    }



    /**
     * Get an {@link Authorization} from the specified <code>authorizationURL</code>
     *
     * @param authorizationURL the {@link Authorization} URL
     * @return the {@link Authorization} from the specified <code>authorizationURL</code>
     */
    public Authorization getAuthorization(String authorizationURL) {
        HttpResponse httpResponse = postAsGetRequest(authorizationURL);
        return JsonMapper.deserialize(httpResponse.getContentAsString(), Authorization.class);
    }



    /**
     * Get the <code>dns-01</code> {@link Challenge} for the specified {@link Authorization}.
     * Currently only <code>dns-01</code> challenges are supported
     *
     * @param authorization the {@link Authorization}
     * @return the <code>dns-01</code> {@link Challenge}
     * @throws UnsupportedOperationException if no <code>dns-01</code> {@link Challenge} is found
     */
    public Challenge getChallenge(Authorization authorization) {
        return authorization.getChallenges()
                            .stream()
                            .filter(ch -> ch.getType().equals("dns-01"))
                            .findFirst()
                            .orElseThrow(() -> new UnsupportedOperationException("Only DNS validation is supported!"));
    }



    /**
     * Notify the ACME server that the challenge was completed
     *
     * @param challengeURL the <code>challenge completed</code> notification URL
     */
    public void notifyChallengeCompleted(String challengeURL) {
        logger.info("Sending challenge completed notification ...");
        postRequest(challengeURL, new EmptyPayload());
    }



    public Challenge getChallengeFromURL(String challengeURL) {
        HttpResponse httpResponse = postAsGetRequest(challengeURL);
        return JsonMapper.deserialize(httpResponse.getContentAsString(), Challenge.class);
    }



    public String getChallengeKeyAuthorization(String token) {
        // key authorization = token from server + jwk thumbprint
        logger.fine("TOKEN: " + token);
        JsonElement jwk = JsonMapper.toJsonElement(session.getAccountKey());

        String thumbprint = Encoder.base64(KeyTool.sha256digest(jwk.toString().getBytes(StandardCharsets.UTF_8)));
        logger.fine("THUMBPRINT: " + thumbprint);

        // dns txt record = base64 encoded digest of the key authorization
        String keyAuthorization = token + '.' + thumbprint;
        logger.fine("KEY AUTHORIZATION: " + keyAuthorization);

        String txt = Encoder.base64(KeyTool.sha256digest(keyAuthorization.getBytes(StandardCharsets.UTF_8)));
        logger.info("DNS TXT RECORD: " + txt);

        return txt;
    }



    /**
     * Poll for the status of a previously completed {@link Challenge}.<br><br>
     * Note: because of the server's retry-mechanism the challenge can remain in the 'processing' state
     * and the corresponding authorization in the 'pending' state even when the ACME server has set the 'error' field
     * in the response.<br>
     * Currently there is no client-side support implemented for the retry-mechanism.
     *
     * @param challenge the previously completed {@link Challenge}
     * @param maxRetries the maximum number of retries
     * @throws CertClientException if the ACME server returns a validation error
     */
    public void pollChallengeStatus(Challenge challenge, int maxRetries) {
        boolean isChallengeValidated = false;
        for (int i = 0; i < maxRetries; i++) {
            logger.info("Round: " + (i + 1));

            Challenge ch = getChallengeFromURL(challenge.getURL());
            isChallengeValidated = ch.getStatus().equals(CertStatus.VALID.getName());
            ProblemDetail problemDetail = ch.getError();

            logger.info("Status: " + ch.getStatus());

            if (isChallengeValidated) break;
            if (Objects.nonNull(problemDetail)) throw new CertClientException(problemDetail.toString());

            try {
                Thread.sleep(Constants.POLL_CHALLENGE_DELAY);
            } catch (InterruptedException e) {
                logger.fine("Polling interrupted");
            }
        }
        if (!isChallengeValidated) {
            // Log a warning if 'maxRetries' reached without errors but status is not yet set to 'valid'
            logger.warning(Constants.WARNING_MAX_RETRIES_REACHED);
        }
    }



    /**
     * Finalize an ACME {@link Order} by submitting a <code>Certificate Signing Request</code>
     * to the finalization URL.
     *
     * @param order the {@link Order} to be finalized
     * @param csr the {@link CertSigningRequest} to be submitted to the finalization URL
     */
    public void finalizeOrder(Order order, CertSigningRequest csr) {
        String finalizeURL = order.getFinalize();
        HttpResponse httpResponse = postRequest(finalizeURL, csr);
        validateAcmeStatus(JsonMapper.deserialize(httpResponse.getContentAsString(), Order.class));
    }



    /**
     * Poll for the status of a previously finalized {@link Order}.
     *
     * @param order the previously finalized {@link Order}
     * @param maxRetries the maximum number of retries
     * @throws CertClientException if the ACME server returns a validation error
     */
    public void pollFinalizedOrderStatus(Order order, int maxRetries) {
        boolean isOrderValidated = false;
        for (int i = 0; i < maxRetries; i++) {
            logger.info("Round: " + (i + 1));

            Order ord = getOrderFromURL(order.getURL());
            isOrderValidated = ord.getStatus().equals(CertStatus.VALID.getName());
            ProblemDetail problemDetail = ord.getError();

            logger.info("Status: " + ord.getStatus());

            if (isOrderValidated) break;
            if (Objects.nonNull(problemDetail)) throw new CertClientException(problemDetail.toString());

            try {
                Thread.sleep(Constants.POLL_FINALIZE_DELAY);
            } catch (InterruptedException e) {
                logger.fine("Polling interrupted");
            }
        }
        if (!isOrderValidated) {
            // Log a warning if 'maxRetries' reached without errors but status is not yet set to 'valid'
            logger.warning(Constants.WARNING_MAX_RETRIES_REACHED);
        }
    }



    /**
     * Get the newly issued certificate for the specified {@link Order}
     *
     * @param order the {@link Order}
     * @return the newly issued certificate as a byte array
     */
    public byte[] getCertificate(Order order) {
        HttpResponse httpResponse = postAsGetRequest(order.getCertificate());
        String cert = httpResponse.getContentAsString();
        logger.info(System.lineSeparator() + StringUtils.lego1(cert, 64, " ... " + System.lineSeparator(), 26));
        return httpResponse.getContent();
    }



    private HttpResponse headRequest(String url) {
        HttpResponse response = CertHttpClient.head(url);
        return validateHttpResponse(response);
    }



    private HttpResponse getRequest(String url) {
        HttpResponse response = CertHttpClient.get(url);
        return validateHttpResponse(response);
    }



    private HttpResponse postAsGetRequest(String url) {
        HttpResponse httpResponse = CertHttpClient.post(url, createJsonWebSignature(url, ""));
        return validateHttpResponse(httpResponse);
    }



    private HttpResponse postRequest(String url, Payload payload) {
        String jsonPayload = JsonMapper.serialize(payload);
        HttpResponse httpResponse = CertHttpClient.post(url, createJsonWebSignature(url, jsonPayload));
        return validateHttpResponse(httpResponse);
    }



    /**
     * Create the JSON Web Signature for the specified URL and payload.<br>
     * Note: The way the JSON Web Signature is constructed will be changed in the future.
     *
     * @param payload the JSON payload as a {@link String}
     * @return the JSON Web Signature as a JSON {@link String}
     */
    private String createJsonWebSignature(String url, String payload) {
        logger.info("RAW PAYLOAD: " + payload);
        // create a new request object containing: payload, protected, signature
        JsonObject request = new JsonObject();
        // add the payload item
        String base64PayloadField = Encoder.base64(payload);
        request.addProperty("payload", base64PayloadField);
        // create the header item
        JsonObject header = new JsonObject();
        header.addProperty("alg", "RS256");

        if (url.equals(session.getDirectory().getNewAccountLocation())
                || url.equals(session.getDirectory().getRevokeCertLocation())) {
            // the "newAccount" and "revokeCert" operations require the JSON Web Key
            header.add("jwk", JsonMapper.toJsonElement(session.getAccountKey()));

        } else {
            // all the other operations require the Key ID
            header.addProperty("kid", session.getAccountKeyID());
        }
        header.addProperty("nonce", getNewNonce());
        header.addProperty("url", url);

        // add the protected item (protected header)
        String base64ProtectedField = Encoder.base64(header.toString());
        request.addProperty("protected", base64ProtectedField);
        // add the signature item
        String messageToSign = base64ProtectedField + "." + base64PayloadField;
        String signedAndEncoded = KeyTool.signAndEncode(messageToSign, session.getIdentity().getPrivateKey());
        request.addProperty("signature", signedAndEncoded);

        return request.toString();
    }



    private HttpResponse validateHttpResponse(HttpResponse response) {
        if (response.getStatus().getCode() == -1) {
            throw new CertClientException("Invalid response / Connection error");
        }

        if (response.getStatusFamily() == Status.Family.SUCCESSFUL) {
            return response;
        }

        String msg = response.getStatusMessage();
        String cty = response.getHeaderFieldFirst(Header.Field.CONTENT_TYPE);

        if (cty.contains(CertMediaType.APPLICATION_PROBLEM_JSON)) {
            msg = JsonMapper.deserialize(response.getBody().getContentAsString(), ProblemDetail.class).toString();
        }
        throw new CertClientException(msg);
    }



    private void validateAcmeStatus(Statusable obj) {
        if (obj.getStatus().equals(CertStatus.INVALID.getName())) {
            throw new CertClientException(obj.getStatus());
        }
    }
}