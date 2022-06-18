package experiment;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvWriter {
    public static String toCSV(double[][] results) {
        return "digits,Default,Karatsuba,Java\n".concat(Arrays.stream(results).sequential()
                .map(row -> Arrays.stream(row).mapToObj(i -> Double.toString(i)).collect(Collectors.joining(",")))
                .collect(Collectors.joining("\n")));
    }
}
