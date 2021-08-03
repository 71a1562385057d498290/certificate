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



package ro.kovari.crypto;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import ro.kovari.model.DomainsIdentity;

import java.io.IOException;
import java.net.IDN;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;



public class PKCS10CertRequest {

    /**
     * Create a new certificate signing request using the BouncyCastle library
     * @param dIdentity the {@link DomainsIdentity}
     * @return the newly created {@link PKCS10CertificationRequest}
     * @throws OperatorCreationException in case of {@link OperatorCreationException}
     * @throws IOException               in case of {@link IOException}
     */
    public static byte[] createCSR(DomainsIdentity dIdentity)
            throws IOException, OperatorCreationException {

        List<String> fqdn = dIdentity.getDomains();
        if (fqdn.size() < 1) {
            throw new IllegalArgumentException("At least one domain must be specified!");
        }

        KeyPair keyPair = dIdentity.getKeyPair();
        X500Name subject = new X500NameBuilder(X500Name.getDefaultStyle())
                .addRDN(BCStyle.CN, IDN.toASCII(fqdn.get(0)))  // first FQDN is the subject name
                .build();

        List<GeneralName> generalNames = new ArrayList<>();
        for (String subjectAltName : fqdn) {
            generalNames.add(new GeneralName(GeneralName.dNSName, subjectAltName));
        }

        // add the subjectAlternativeName extension
        ExtensionsGenerator extGenerator = new ExtensionsGenerator();
        extGenerator.addExtension(
                Extension.subjectAlternativeName,
                false,
                new GeneralNames(generalNames.toArray(new GeneralName[0]))
        );

        PKCS10CertificationRequestBuilder requestBuilder =
                new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGenerator.generate());

        // sign the CSR with our private key
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());

        return requestBuilder.build(signer).getEncoded();
    }
}
