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



import java.util.Objects;

public final class StringUtils {

    /**
     * Enclose the provided {@link String} between quotation marks
     *
     * @param string the {@link String} to be enclosed between quotation makes
     * @return the resulting {@link String}
     */
    public static String addQuotationMarks(String string) {
        return "\"" + string + "\"";
    }



    /**
     * If present, removes start and end quotation marks from the specified string
     *
     * @param string the string from which the quotation marks are to be removed
     * @return the specified string with the start and end quotation marks removed
     */
    public static String removeQuotationMarks(String string) {
        char quote = '"';
        if ((string.indexOf(quote) == 0) && (string.lastIndexOf(quote) == string.length() - 1)) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }



    /**
     * Removes the final dot, if any, from a {@link String}
     *
     * @param string the {@link String} from which the final dot will be removed
     * @return the resulting {@link String}
     */
    public static String removeFinalDot(String string) {
        if (Objects.isNull(string)) {
            throw new IllegalArgumentException("Input string can not be null");
        }

        // remove the final '.' from the results
        if (string.length() > 1 && string.endsWith(".")) {
            string = string.substring(0, string.lastIndexOf('.'));
        }
        return string;
    }



    /**
     * Given an <code>input</code> {@link String}, create a new {@link String}
     * containing the first <i>x</i> number of characters from the input string,
     * the specified <i>middle</i> string and the last <i>y</i> number of characters
     * from the input string.<br>
     *
     * @param input the input {@link String}
     * @param x how many characters, counting from the start of the input string, to include into the result
     * @param y how many characters, counting from the end of the input string, to include into the result
     * @param middle the middle part to include into the resulting {@link String}
     * @return the resulting {@link String}
     */
    public static String lego1(String input, int x, String middle, int y) {
        if (Objects.isNull(input)) {
            throw new IllegalArgumentException("Input string can not be null");
        }

        if (x < 0 || x > input.length() || y < 0 || y > input.length()) {
            throw new IllegalArgumentException("Invalid number of characters");
        }

        return input.substring(0, x) + middle + input.substring(input.length() - y, input.length());
    }
}