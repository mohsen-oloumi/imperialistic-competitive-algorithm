import java.util.List;

public class Helper {
    public static double RandomFromRange(Range range) {
        return Math.random() * (Math.abs(range.start) + Math.abs(range.stop)) + range.start;
    }

    public static double F(double x, double y) {
        return 10 * 2 + Math.pow(x, 2) + Math.pow(y, 2) - 10 * Math.cos(2 * Math.PI * x) - 10 * Math.cos(2 * Math.PI * y);
    }

    public static double GetRandomFromArrayWithProbabilites(List<Long> results, List<Double> weights) {
        double num = Math.random();
        double s = 0;
        int lastIndex = weights.size() - 1;
        for (int i = 0; i < lastIndex; ++i) {
            s += weights.get(i);
            if (num < s) {
                return results.get(i);
            }
        }
        return results.get(lastIndex);
    }

    static double Relaxation(double x, Range domain) {
        if (x < domain.start) {
            return domain.start;
        }
        if (x > domain.stop) {
            return domain.stop;
        }
        return x;
    }
}
