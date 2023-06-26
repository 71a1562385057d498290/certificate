
Supported Cryptographic Profiles
---
Specified through the **--profile** and **--cert-profile** CLI arguments.  
The following profile names can be used: **RS256, ES256, ES384, ES512**

By default, if no profile is provided through the CLI, the **RS256** profile is used. 

Each profile's default values are listed below.

RSA
---
<pre>
alg: <b>RS256</b>
family: RSA
keySize: 4096
signingAlg: SHA256withRSA
csrSigningAlg: SHA256withRSA
</pre>

ECC									                                                      
---
<pre>
alg: <b>ES256</b>
family: EC
curveName: P-256
signingAlg: SHA256withPLAIN-ECDSA
csrSigningAlg: SHA256withECDSA

---------------------------------

alg: <b>ES384</b>
family: EC
curveName: P-384
signingAlg: SHA384withPLAIN-ECDSA
csrSigningAlg: SHA384withECDSA

---------------------------------

alg: <b>ES512</b>
family: EC
curveName: P-521
signingAlg: SHA512withPLAIN-ECDSA
csrSigningAlg: SHA512withECDSA
</pre>

Notes								                                                      
---
<pre>
The 'alg' values are defined in RFC7518, section 3.1 (JWS):
https://datatracker.ietf.org/doc/html/rfc7518#section-3.1

If a cryptographic profile is not provided, <b>RS256</b> is assumed by default, 
with all the corresponding default values from the above table 
</pre>
  
**Notes from RFC7518, section 3.1 (JWS):**

<pre>
The table below is the set of "alg" (algorithm) Header Parameter values
defined by this specification for use with JWS, each of which is explained
in more detail in the following sections:

   +--------------+-------------------------------+--------------------+
   | "alg" Param  | Digital Signature or MAC      | Implementation     |
   | Value        | Algorithm                     | Requirements       |
   +--------------+-------------------------------+--------------------+
   | HS256        | HMAC using SHA-256            | Required           |
   | HS384        | HMAC using SHA-384            | Optional           |
   | HS512        | HMAC using SHA-512            | Optional           |
   <b>| RS256        | RSASSA-PKCS1-v1_5 using       | Recommended        |</b>
   <b>|              | SHA-256                       |                    |</b>
   <b>| RS384        | RSASSA-PKCS1-v1_5 using       | Optional           |</b>
   <b>|              | SHA-384                       |                    |</b>
   <b>| RS512        | RSASSA-PKCS1-v1_5 using       | Optional           |</b>
   <b>|              | SHA-512                       |                    |</b>
   <b>| ES256        | ECDSA using P-256 and SHA-256 | Recommended+       |</b>
   <b>| ES384        | ECDSA using P-384 and SHA-384 | Optional           |</b>
   <b>| ES512        | ECDSA using P-521 and SHA-512 | Optional           |</b>
   | PS256        | RSASSA-PSS using SHA-256 and  | Optional           |
   |              | MGF1 with SHA-256             |                    |
   | PS384        | RSASSA-PSS using SHA-384 and  | Optional           |
   |              | MGF1 with SHA-384             |                    |
   | PS512        | RSASSA-PSS using SHA-512 and  | Optional           |
   |              | MGF1 with SHA-512             |                    |
   | none         | No digital signature or MAC   | Optional           |
   |              | performed                     |                    |
   +--------------+-------------------------------+--------------------+

   The use of "+" in the Implementation Requirements column indicates
   that the requirement strength is likely to be increased in a future
   version of the specification.

   See Appendix A.1 for a table cross-referencing the JWS digital
   signature and MAC "alg" (algorithm) values defined in this
   specification with the equivalent identifiers used by other standards
   and software packages.
</pre>
