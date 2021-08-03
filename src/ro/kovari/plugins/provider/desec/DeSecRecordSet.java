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

package ro.kovari.plugins.provider.desec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;



/** Class representing a RRset for the deSEC.io API */
final class DeSecRecordSet {

    // read-only
    private final String created = null;
    // read-only
    private final String domain = null;
    // read, write-once (upon RRset creation)
    private final String subname;
    // read-only
    private final String name = null;
    // read, write-once (upon RRset creation)
    private final String type;
    // read-write
    private final int ttl = 3600;
    // read-write
    private final List<String> records;
    // read-only
    private final String touched = null;



    DeSecRecordSet(String subname, String type, List<String> records) {
        this.subname = subname;
        this.type = type;
        this.records = records;
    }



    String getCreated() {
        return created;
    }



    String getDomain() {
        return domain;
    }



    String getSubname() {
        return subname;
    }



    String getName() {
        return name;
    }



    String getType() {
        return type;
    }



    int getTTL() {
        return ttl;
    }



    List<String> getRecords() {
        return records;
    }



    String getTouched() {
        return touched;
    }



    @Override
    public String toString() {
        Gson builder = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        return builder.toJson(this, DeSecRecordSet.class);
    }
}