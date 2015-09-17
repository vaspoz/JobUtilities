package Utils;

public class StringUtils {
    public static String asciiToString(byte[] ascii) {
        return asciiToString(ascii, 0, ascii.length);
    }

    public static String asciiToString(byte[] ascii, int offset, int length) {
        StringBuilder sb = new StringBuilder(ascii.length);
        for (int i = offset, n = length + offset; i < n; ++i) {
            if (ascii[i] < 0) continue;
            sb.append((char) ascii[i]);
        }
        return sb.toString();
    }
}
