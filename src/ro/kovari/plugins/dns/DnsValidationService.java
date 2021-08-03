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



package ro.kovari.plugins.dns;

import ro.kovari.plugins.dns.spi.DnsValidationProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;



public class DnsValidationService {

    private static final ServiceLoader<DnsValidationProvider> LOADER
            = ServiceLoader.load(DnsValidationProvider.class);



    /**
     * Lookup a service provider based on the specified provider name
     *
     * @param providerName the provider name
     * @return the service provider with the given name
     * @throws RuntimeException if no provider with the given name was found
     */
    public static DnsValidationProvider getDnsProvider(String providerName) {
        try {
            for (DnsValidationProvider provider : LOADER) {
                if (provider.getName().equals(providerName)) {
                    return provider;
                }
            }
        } catch (ServiceConfigurationError e) {
            e.printStackTrace();
        }
        throw new RuntimeException("No suitable provider found");
    }



    /**
     * Get a {@link List} containing the names of all the registered {@link DnsValidationProvider}s
     *
     * @return a {@link List} containing the names of all the registered {@link DnsValidationProvider}s
     */
    public static List<String> getRegisteredProviderNames() {
        List<String> providerNames = new ArrayList<>();

        try {
            for (DnsValidationProvider provider : LOADER) {
                providerNames.add(provider.getName());
            }
        } catch (ServiceConfigurationError e) {
            e.printStackTrace();
        }
        return providerNames;
    }
}
