package net.veldor.intermittentfasting.utils;

public class MyFormatter {
    public static long countPercent(long first, long second) {
        long piece = (first + second) / 100;
        if(piece > 0){
            return first / piece;
        }
        return 100;
    }
}
