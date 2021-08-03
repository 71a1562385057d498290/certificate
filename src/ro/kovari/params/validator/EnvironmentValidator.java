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



package ro.kovari.params.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import ro.kovari.CertEnvironment;

import java.util.Arrays;
import java.util.stream.Collectors;



public class EnvironmentValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (value == null || value.isEmpty()) throw new ParameterException("Invalid environment");

        try {
            CertEnvironment.valueOf(value.toUpperCase());

        } catch (IllegalArgumentException e) {
            String environments = Arrays.stream(CertEnvironment.values())
                                        .map(environment -> environment.toString())
                                        .collect(Collectors.joining(", "));
            throw new ParameterException("Invalid environment. Possible values for <environment> are: " + environments);
        }
    }
}
