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



package ro.kovari.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import ro.kovari.model.acme.Payload;



public class JsonMapper {

    /**
     * Serialize an {@link Object} to a JSON string
     * @param object the {@link Object} to be serialized
     * @return the JSON string representation of the {@link Object}
     */
    public static String serialize(Object object) {
        return new Gson().toJson(object, object.getClass());
    }



    /**
     * Serialize an {@link Object} to a JSON string.<br>
     * This method is intended to be used with {@link #toString()}
     * @param object the {@link Object} to be serialized
     * @return the JSON string representation of the {@link Object}
     */
    public static String serializeToString(Object object) {
        Gson builder = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        return builder.toJson(object, object.getClass());
    }



    public static <T extends Payload> T deserialize(String json, Class<T> type) throws JsonSyntaxException {
        return new Gson().fromJson(json, type);
    }



    public static JsonElement toJsonElement(Object object) {
        return new GsonBuilder().create().toJsonTree(object);
    }
}
