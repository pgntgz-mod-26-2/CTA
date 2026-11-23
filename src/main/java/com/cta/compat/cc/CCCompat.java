package com.cta.compat.cc;

public class CCCompat {
    private static final boolean ccLoaded = false;

    public static void init() {
        // CC: Tweaked integration disabled on MC 26.2 base port.
    }

    public static boolean isLoaded() {
        return ccLoaded;
    }
}
