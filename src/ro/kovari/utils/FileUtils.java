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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    /**
     * Save data to file
     * @param data a byte array with the data to be saved
     * @param file the file where data is to be saved
     */
    public static void save(File file, byte[] data) {
        try (FileOutputStream fs = new FileOutputStream(file)) {
            fs.write(data);
            fs.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static File getFirstUnIndexedDirectory(File dir) {
        for (int idx = 1; ; ) {
            File indexed = new File(dir.getPath() + "." + idx);
            if (!indexed.exists()) {
                return indexed;
            }
            idx++;
        }
    }



    public static void validateDirectory(File input) {
        if (!input.isDirectory()) {
            throw new RuntimeException("Not a valid directory");
        }
    }
}