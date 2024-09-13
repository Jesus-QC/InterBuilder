package android.util;

// I told you to trust me... we are in android.
public class Base64 {
    public static byte[] decode(String input, int _flags) {
        return java.util.Base64.getUrlDecoder().decode(input);
    }
}