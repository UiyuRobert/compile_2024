package Middle.Optimize;

public class Util {
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    public static Integer getPowerOfTwoExponent(int n) {
        if (!isPowerOfTwo(n)) {
            // n 不是 2 的次方
            return null;
        }
        return Integer.numberOfTrailingZeros(n);
    }
}
