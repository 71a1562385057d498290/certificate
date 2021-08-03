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

package ro.kovari;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import ro.kovari.config.ConfigurationReader;
import ro.kovari.params.Arguments;
import ro.kovari.storage.FileStorage;
import ro.kovari.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;



public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static { initLogger(); }



    /**
     * Main entry point
     * @param arguments CLI arguments
     */
    public static void main(String[] arguments) {

        logger.info("                                             ");
        logger.info("               _   _  __ _           _       ");
        logger.info("  ___ ___ _ __| |_(_)/ _(_) ___ __ _| |_ ___ ");
        logger.info(" / __/ _ \\ '__| __| | |_| |/ __/ _` | __/ _ \\");
        logger.info("| (_|  __/ |  | |_| |  _| | (_| (_| | ||  __/");
        logger.info(" \\___\\___|_|   \\__|_|_| |_|\\___\\__,_|\\__\\___|");
        logger.info("                                             ");

        Arguments args = new Arguments();
        JCommander jc = JCommander.newBuilder().addObject(args).build();

        try {
            jc.parse(arguments);

        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        if (args.isParamHelp()) {
            jc.usage();
            System.exit(0);
        }

        //TODO Implement a proper storage
        File root = new File(ConfigurationReader.getBasedir(), Constants.FILE_DATA_DIR);
        FileStorage fileStorage = new FileStorage(root, args);

        CertClient client = CertClient.bootstrap(args);
        client.setStorage(fileStorage);
        client.requestNewCertificate();
    }



    /**
     * Initialize the logger by loading and applying
     * the logger configuration from a custom configuration file
     */
    private static void initLogger() {
        final File properties =
                new File(ConfigurationReader.getConfDir(), Constants.FILE_LOGGER_CONFIG);
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(properties));
        } catch (IOException e) {
            logger.warning(Constants.MESSAGE_LOGGER_LOADING_ERROR);
        }
    }
}
