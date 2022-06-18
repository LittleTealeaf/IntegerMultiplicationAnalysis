package experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import biglyints.BiglyInt;
import biglyints.BiglyIntFactory;

public class Experiment {

    private String[] values;

    public Experiment() {
        values = new String[10];
        for (int i = 0; i < values.length; i++) {
            values[i] = "";
        }
    }

    public void updateValues(int length) {
        Random random = new Random();
        IntStream.range(0, values.length).parallel().forEach(i -> {
            if(values[i].length() < length) {
                values[i] = values[i].concat(Stream.generate(() -> Integer.toString(random.nextInt(10))).limit(length - values[i].length()).collect(Collectors.joining()));
            }
        });
    }

    public double[][] runExperiment(int min, int max, IntUnaryOperator next) {
        final int combinations = values.length * values.length;

        return IntStream.iterate(min, i -> i < max, next).sequential().mapToObj(len -> {
            System.out.print(Integer.toString(len));
            updateValues(len);
            System.out.print(" - Created Digits");

            double[] results = Stream.of(BiglyIntFactory.values()).parallel().mapToDouble(factory -> {
                return IntStream.range(0, combinations).parallel().mapToLong(i -> {
                    return runInstance(values[(int) (i / values.length)], values[i % values.length], factory);
                }).average().getAsDouble();
            }).toArray();

            double[] vals = new double[results.length + 1];
            vals[0] = (double) len;

            for(int i = 0; i < results.length; i++) {
                vals[i+1] = results[i];
            }

            System.out.println(" - Data Recorded: " + Arrays.toString(vals));

            return vals;
        }).toArray(double[][]::new);
    }

    public static long runInstance(String numberA, String numberB, BiglyIntFactory factory) {
        BiglyInt a = factory.create(numberA);
        BiglyInt b = factory.create(numberB);
        long start = System.nanoTime();
        a.multiply(b);
        long end = System.nanoTime();
        return end - start;
    }

    public static void main(String[] args) {
        Experiment experiment = new Experiment();
        try {
            String results = CsvWriter.toCSV(experiment.runExperiment(2, 600000	, i -> (int) (i * 1.5)));
            FileWriter fileWriter = new FileWriter(new File("results.csv"));
            fileWriter.write(results);
            fileWriter.close();

        } catch(Exception e) {}
    }

    static interface StepFunction {
        int next(int x);
    }
}
