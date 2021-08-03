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



public class Constants {

    private Constants() { throw new AssertionError(); }



    public static final String HEADER_LOCATION = "location";
    public static final String HEADER_REPLAY_NONCE = "replay-nonce";

    public static final String DEFAULT_DNS_PROVIDER = "default";

    public static final String FILE_DATA_DIR = "data";
    public static final String FILE_CONFIG_DIR = "conf";
    public static final String FILE_LOGGER_CONFIG = "logging.properties";

    public static final String MESSAGE_LOGGER_LOADING_ERROR = "Error loading custom logger configuration";
    public static final String MESSAGE_DNS_COMPLETE_CHALLENGE = "Press ENTER to continue ...";
    public static final String WARNING_MAX_RETRIES_REACHED = "Max number of retries reached without validation. Consider increasing the number of retries.";

    public static final String EXCEPTION_CREATING_CSR = "Exception creating the Certificate Signing Request";
    public static final String EXCEPTION_PAUSING = "Exception while pausing";
    public static final String EXCEPTION_EXTERNAL_ACCOUNT_REQUIRED = "External account required but support not implemented";

    public static final int POLL_FINALIZE_MAX_RETRIES = 20;
    public static final int POLL_FINALIZE_DELAY = 3000;
    public static final int POLL_CHALLENGE_MAX_RETRIES = 20;
    public static final int POLL_CHALLENGE_DELAY = 3000;
}
