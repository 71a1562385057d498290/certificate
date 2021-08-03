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



package ro.kovari.params;

import com.beust.jcommander.Parameter;
import ro.kovari.CertEnvironment;
import ro.kovari.params.validator.ContactValidator;
import ro.kovari.params.validator.DomainValidator;
import ro.kovari.params.validator.EnvironmentValidator;
import ro.kovari.params.validator.ProviderValidator;

import java.util.List;
import java.util.Objects;



public class Arguments {

    @Parameter(
            names = { "-c", "--contacts" },
            description = "the list of contacts",
            required = true,
            variableArity = true,
            validateValueWith = ContactValidator.class)
    private List<String> paramContact;

    @Parameter(
            names = { "-d", "--domains" },
            description = "the list of domain names",
            required = true,
            variableArity = true,
            validateValueWith = DomainValidator.class)
    private List<String> paramDomain;

    @Parameter(
            names = { "-e", "--environment" },
            description = "the ACME environment",
            required = true,
            arity = 1,
            validateWith = EnvironmentValidator.class)
    private String paramEnvironment;

    @Parameter(
            names = { "-p", "--provider" },
            description = "the DNS validation provider",
            required = false,
            arity = 1,
            validateWith = ProviderValidator.class)
    private String paramProvider;

    @Parameter(
            names = { "-auto" },
            description = "try to automatically perform the entire DNS validation")
    private boolean auto;

    @Parameter(
            names = { "-h", "--help" },
            description = "this help",
            help = true)
    private boolean paramHelp;



    public boolean isParamContactSet() {
        return Objects.nonNull(paramContact);
    }



    public boolean isParamDomainSet() {
        return Objects.nonNull(paramDomain) && !paramDomain.isEmpty();
    }



    public boolean isParamEnvironmentSet() {
        return Objects.nonNull(paramEnvironment);
    }



    public boolean isParamProviderSet() {
        return Objects.nonNull(paramProvider);
    }



    public boolean isAutoSet() {
        return auto;
    }



    public boolean isParamHelp() {
        return paramHelp;
    }



    public List<String> getDomains() {
        return paramDomain;
    }



    public CertEnvironment getEnvironment() {
        return CertEnvironment.valueOf(paramEnvironment.toUpperCase());
    }



    public String getProvider() {
        return paramProvider;
    }



    public List<String> getContacts() {
        return paramContact;
    }
}