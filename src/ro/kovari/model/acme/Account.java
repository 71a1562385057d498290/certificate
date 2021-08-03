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



public class Account implements Payload, Statusable {

    @SerializedName(value = "status")
    private String status;

    @SerializedName(value = "contact")
    private List<String> contact;

    @SerializedName(value = "termsOfServiceAgreed")
    private boolean termsOfServiceAgreed;

    @SerializedName(value = "orders")
    private String orders;

    @SerializedName(value = "onlyReturnExisting")
    private boolean onlyReturnExisting;



    @Override
    public String getStatus() {
        return status;
    }



    public Account setTermsOfServiceAgreed(boolean termsOfServiceAgreed) {
        this.termsOfServiceAgreed = termsOfServiceAgreed;
        return this;
    }



    public boolean isTermsOfServiceAgreed() {
        return termsOfServiceAgreed;
    }



    public Account setOnlyReturnExisting(boolean onlyReturnExisting) {
        this.onlyReturnExisting = onlyReturnExisting;
        return this;
    }



    public boolean isOnlyReturnExisting() {
        return onlyReturnExisting;
    }



    public Account addContact(String contact) {
        if (Objects.isNull(this.contact)) this.contact = new ArrayList<>();

        this.contact.add(contact);
        return this;
    }



    public Account addContacts(List<String> contacts) {
        if (Objects.isNull(this.contact)) this.contact = new ArrayList<>();

        this.contact.addAll(contacts);
        return this;
    }



    public List<String> getContact() {
        return contact;
    }



    public String getOrdersURL() {
        return orders;
    }



    @Override
    public String toString() {
        return JsonMapper.serializeToString(this);
    }
}
