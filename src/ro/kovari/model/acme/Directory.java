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



public class Directory implements Payload {

    @SerializedName(value = "newNonce")
    private String newNonce;

    @SerializedName(value = "newAccount")
    private String newAccount;

    @SerializedName(value = "newAuthz")
    private String newAuthz;

    @SerializedName(value = "newOrder")
    private String newOrder;

    @SerializedName(value = "revokeCert")
    private String revokeCert;

    @SerializedName(value = "keyChange")
    private String keyChange;

    @SerializedName(value = "meta")
    private Meta meta;



    public String getNewNonceLocation() {
        return newNonce;
    }



    public String getNewAccountLocation() {
        return newAccount;
    }



    public String getNewAuthzLocation() {
        return newAuthz;
    }



    public String getNewOrderLocation() {
        return newOrder;
    }



    public String getRevokeCertLocation() {
        return revokeCert;
    }



    public String getKeyChangeLocation() {
        return keyChange;
    }



    public Meta getMeta() {
        return meta;
    }



    @Override
    public String toString() {
        return JsonMapper.serializeToString(this);
    }
}
