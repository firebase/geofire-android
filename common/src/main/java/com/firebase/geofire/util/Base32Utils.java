/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firebase.geofire.util;

public final class Base32Utils {

    /* number of bits per base 32 character */
    public static final int BITS_PER_BASE32_CHAR = 5;

    private static final String BASE32_CHARS = "0123456789bcdefghjkmnpqrstuvwxyz";

    private Base32Utils() {
        throw new AssertionError("No instances.");
    }

    public static char valueToBase32Char(int value) {
        if (value < 0 || value >= BASE32_CHARS.length()) {
            throw new IllegalArgumentException("Not a valid base32 value: " + value);
        }
        return BASE32_CHARS.charAt(value);
    }

    public static int base32CharToValue(char base32Char) {
        int value = BASE32_CHARS.indexOf(base32Char);
        if (value == -1) {
            throw new IllegalArgumentException("Not a valid base32 char: " + base32Char);
        } else {
            return value;
        }
    }

    public static boolean isValidBase32String(String string) {
        return string.matches("^[" + BASE32_CHARS + "]*$");
    }
}
