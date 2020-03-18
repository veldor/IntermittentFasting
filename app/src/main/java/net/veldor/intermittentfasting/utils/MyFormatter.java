package net.veldor.intermittentfasting.utils;

import android.util.Log;

public class MyFormatter {
    public static long countPercent(long first, long second) {
        long piece = (first + second) / 100;
        return first / piece;
    }
}
