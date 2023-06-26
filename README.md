## certificate
An **ACMEv2** client implementation.  
Works with Let's Encrypt, Buypass, ZeroSSL or with any other ACMEv2 compliant Certificate Authority.  

The application assumes that you **<code>AGREE</code>** to the **<code>Terms of Service</code>** of the ACME environment you will be using.
Make sure you read them before using this application!  

Read more about the supported [Cryptographic Profiles](README-CRYPTO.md).  

##### Usage:
```
$ ./certificate request -e <ENVIRONMENT> -c <CONTACT> ... -d <DOMAIN> ... [-p <PROVIDER>] [-a]
```
```
$ ./certificate authorize -e <ENVIRONMENT> -c <ACCOUNT_NAME> -d <DOMAIN> ... [-p <PROVIDER>] [-a]
```
```
$ ./certificate revoke -e <ENVIRONMENT> -c <ACCOUNT_NAME> -d <CERTIFICATE_NAME>
```
```
$ ./certificate keychange -e <ENVIRONMENT> -c <ACCOUNT_NAME>
```
```
$ ./certificate deactivate -e <ENVIRONMENT> -c <ACCOUNT_NAME>
```

##### main options:
```
-h   Display the help information.
```

##### request command options:
```
-c   One or more, space separated, email addresses to be used as contact information.
     The first one specified will become the ACCOUNT_NAME. 
-d   One or more, space separated, domain names to be included in the requested certificate.
     The first one specified will become the CERTIFICATE_NAME. The certificate name is used 
     locally in file paths.
-e   One of the predefined ACME environments to be used. 
     Required if '--environment-url' is not specified.
-p   The DNS provider plugin to be used. The provider will attempt to create 
     and after the validation to remove the required TXT record.
-a   If specified, a pre-'challenge completed' validation will be performed before the
     final 'challenge completed' notification is sent to the Certificate Authority.
     Used together with the '-p' argument, it can fully automate the entire process of
     requesting a new certificate.
```
```
--eab-kid            The EAB key identifier, provided by your Certificate Authority.
--eab-mac-key        The base64url-encoded EAB MAC key, provided by your Certificate Authority.
--environment-url    Specify an environment URL to be used instead of a predefined environment.
                     If this parameter is specified the '-e' parameter must not be present.
```

```
--create-new         By default an existing account identity is used instead of creating a new one.
                     If this flag is set, a new account identity creation is forced even if an 
                     acconut identity already exist for the specified account. 
                     Consequently a new ACME account will be created.
                     The existing account identity will be indexed.
                   
--no-create-new      By default, if an existing account identity can not be imported,
                     an attempt is made to create a new account identity and consequently
                     a new ACME account. 
                     This behavior can be changed by specifying the --no-create-new flag, 
                     in which case the process will stop and the application will exit with an error.
                     
--only-account       Only create a new ACME account without requesting a new certificate.
```

```
--profile            The cryptographic profile to be used for the account keys. 
                     The following profiles are supported: RS256, ES256, ES384 and ES512.
                     
                     This is only meaningful when creating new identities.
                     For existing identities the profile is constructed from the identity's key.
              
--key-size           The account key size. Only used with the RSA cryptographic family. 
```

```
--cert-profile       The cryptographic profile to be used for the certificate keys. 
                     The following profiles are supported: RS256, ES256, ES384 and ES512.
                     
                     If not specified, the account identity's cryptographic profile is used.
              
--cert-key-size      The certificate key size. Only used with the RSA cryptographic family. 
```

##### revoke command options:
```
-c   The ACCOUNT_NAME, meaning the first email address specified when the account was requested.
-d   The CERTIFICATE_NAME, meaning the first domain specified when the certificate was requested.
-e   The ACME environment that issued the certificate. 
```
```
-s / --signer        The revocation request signer. Can be either 'account' or 'domain'.
                     account - the revocation request will be signed with the account key.
                     domain  - the revocation request will be signed with the domain/certificate key.
                     Defaults to 'account'.
                     
-r / --reason        The revocation reason code. See RFC 5280 for a more detailed description.
                     Defaults to 0.
```

##### Main limitations:

- Only the "dns-01" authorization mechanism is supported
- Certificate auto-renewal is not supported, meaning there is no certificate monitoring service that will automatically 
request a new certificate before its expiration. 
However, re-running the application manually, using the exact same arguments as when the certificate was initially requested, 
will request a new certificate. 

##### More details:

The account creation and the certificate request operations are merged into a single <code>request</code> command.  
However, this behavior can be overridden by specifying the <code>--only-account</code> CLI argument in which
case an ACME account is created without requesting a new certificate.

Different cryptographic profiles can be specified for the account keys and the domain/certificate keys, using the <code>--profile</code> and the <code>--cert-profile</code> CLI arguments.

Most of the ACME environments allow the creation of multiple accounts using the same email address.
However, locally we use the email address to uniquely identify an account within an environment.
The first email address specified when requesting a new account will become the <code>ACCOUNT_NAME</code> 

An <code>identity</code> is a pair formed from a list of items provided by the user and a cryptographic KeyPair.  
Throughout the application there are two types of identities used: account identities and domain identities.

An <code>account identity</code> is a pair formed from a list of email addresses and a cryptographic KeyPair.  
A <code>domain identity</code> is a pair formed from a list of domain names and a cryptographic KeyPair.

You can think of an <code>account identity</code> as being the local representation of a remote ACME account. 

Except for the environment definitions, all generated data is stored in the application's <code>data</code> directory.
The environment definitions are stored in the <code>environments.json</code> file under the application's <code>conf</code> directory.
   
By default, on each environment, we try to reuse an existing account identity instead of creating a new one.  
If, for whatever reason, the account identity can't be imported from the filesystem, a new account is created.

A new account creation can be manually forced by specifying the <code>--create-new</code> flag in the CLI.   
With the <code>--create-new</code> flag specified, a new account will be created even if another account already exists for the specified email address.

When creating a new account, if an account with the same email address already exists in the specified environment's local directory,
the already existing account will be <code>indexed</code>, meaning it will be renamed so that it's name will be of the form: <code>email-address#index</code>

For each type of operations, be it account related or domain related, the data is persisted locally only after the operation is successfully completed on the ACME server.

Currently, only the default <code>application/pem-certificate-chain</code> format is supported when downloading the certificate.  

##### Directory structure:

```
.
└── data
    ├── environment-1
    │   │    
    │   ├── account-1
    │   │   ├── account-1.key
    │   │   ├── account-1.pub
    │   │   │
    │   │   ├── domain-1
    │   │   │   ├── domain-1.cer
    │   │   │   ├── domain-1.csr
    │   │   │   ├── domain-1.key
    │   │   │   └── domain-1.pub
    │   │   │
    │   │   ...
    │   │   │
    │   │   └── domain-n
    │   │       ├── domain-n.cer
    │   │       ├── domain-n.csr
    │   │       ├── domain-n.key
    │   │       └── domain-n.pub
    │   │
    │   ...
    │   │
    │   └── account-n
    │       │
    │       ├── account-n.key
    │       ├── account-n.pub
    │       │
    │       ├── domain-1
    │       │   ├── domain-1.cer
    │       │   ├── domain-1.csr
    │       │   ├── domain-1.key
    │       │   └── domain-1.pub
    │       │
    │       ...
    │       │
    │       └── domain-n
    │           ├── domain-n.cer
    │           ├── domain-n.csr
    │           ├── domain-n.key
    │           └── domain-n.pub
    │
    ...
    │
    └── environment-n
```

##### Notes

- Buypass does not support certificate revocation using the certificate key! Only revocation using the account key is supported.
- Buypass supports the following cryptographic profiles: **RS256**, RS384, RS512, PS256, PS384, PS512, **ES256**.
- Letsencrypt supports the following cryptographic profiles: **RS256**, **ES256**, **ES384**, **ES512**.

Profiles marked in **bold** are also supported by our application.