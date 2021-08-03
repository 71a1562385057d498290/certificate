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



package ro.kovari.config;

import ro.kovari.Main;
import ro.kovari.utils.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ConfigurationReader {

    private static final Logger logger = Logger.getLogger(ConfigurationReader.class.getName());
    private static final File basedir;

    static { basedir = initBasedir(); }



    private static File initBasedir() {
        String path = Main.class.getProtectionDomain()
                                .getCodeSource()
                                .getLocation()
                                .getPath();

        try { // handle directory names with spaces
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Unsupported encoding exception!", e);
        }
        return path.endsWith(".jar") ? new File(path).getParentFile() : new File(path);
    }



    /**
     * Get the configuration basedir
     * @return the configuration basedir
     */
    public static File getBasedir() { return basedir; }



    public static File getConfDir() { return new File(basedir, Constants.FILE_CONFIG_DIR); }



    /**
     * Get the configuration properties from the specified configuration file
     * located in the configuration directory returned by {@link #getConfDir()}
     * @return The configuration {@link Properties}
     */
    public static Properties getProperties(String filename) throws ConfigurationException {
        Properties config = new Properties();
        try (FileReader reader = new FileReader(new File(getConfDir(), filename))) {
            config.load(reader);
        } catch (IOException e) {
            throw new ConfigurationException("Error loading configuration", e);
        }
        return config;
    }
}
