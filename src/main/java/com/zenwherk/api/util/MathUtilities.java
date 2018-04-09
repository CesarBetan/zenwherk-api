package com.zenwherk.api.util;

import java.util.Random;

public class MathUtilities {
    public static String randomDNAString(int dnaLength) {
        Random rand = new Random();
        StringBuilder dna = new StringBuilder(dnaLength);

        for (int i = 0; i < dnaLength; i++) {
            dna.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".charAt(rand.nextInt(62)));
        }

        return dna.toString();
    }
}
