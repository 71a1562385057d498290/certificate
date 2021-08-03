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



package ro.kovari.model.acme;

import com.google.gson.annotations.SerializedName;
import ro.kovari.utils.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class Order implements Payload, Statusable {

    @SerializedName(value = "expires")
    private String expires;

    @SerializedName(value = "error")
    private ProblemDetail error;

    @SerializedName(value = "authorizations")
    private List<String> authorizations;

    @SerializedName(value = "status")
    private String status;

    @SerializedName(value = "finalize")
    private String finalize;

    @SerializedName(value = "identifiers")
    private List<Identifier> identifiers;

    @SerializedName(value = "certificate")
    private String certificate;
    private String url;



    public void setURL(String url) {
        this.url = url;
    }



    public String getURL() {
        return url;
    }



    @Override
    public String getStatus() {
        return status;
    }



    public List<String> getAuthorizations() {
        return authorizations;
    }



    public String getFinalize() {
        return finalize;
    }



    public String getCertificate() {
        return certificate;
    }



    public ProblemDetail getError() {
        return error;
    }



    public Order addIdentifier(String type, String value) {
        if (Objects.isNull(identifiers)) {
            identifiers = new ArrayList<>();
        }
        identifiers.add(new Identifier(type, value));
        return this;
    }



    public void addAllIdentifiers(List<Identifier> names) {
        if (Objects.isNull(identifiers)) {
            identifiers = new ArrayList<>();
        }
        identifiers.addAll(names);
    }



    public List<Identifier> getIdentifiers() {
        return identifiers;
    }



    @Override
    public String toString() {
        return JsonMapper.serializeToString(this);
    }
}
