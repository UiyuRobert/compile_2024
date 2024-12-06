package Middle.Optimize;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

    public static void writeCFG(String output) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("cfg.txt"));
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Write CFG Success !");
    }
}
