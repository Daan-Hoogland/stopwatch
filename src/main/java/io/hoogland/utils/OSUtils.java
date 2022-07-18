package io.hoogland.utils;

public class OSUtils {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

}
