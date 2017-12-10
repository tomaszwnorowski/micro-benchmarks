package twn;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class IdentifierBenchmark {

    private static final int IDENTIFIER_SIZE = 32;
    private static final char[] ALPHA_NUM = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'm', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    @State(Scope.Benchmark)
    public static class RandomState {
        final Random random = new Random();
    }

    @State(Scope.Benchmark)
    public static class SecureRandomState {
        final SecureRandom secureRandom = new SecureRandom();
    }

    @Benchmark
    public String streamSharedSecureRandomIdentifier(SecureRandomState state) {
        return streamRandom(state.secureRandom);
    }

    @Benchmark
    public String streamNewSecureRandomIdentifier() {
        return streamRandom(new SecureRandom());
    }

    @Benchmark
    public String loopStringBuilderSharedSecureRandomIdentifier(SecureRandomState state) {
        return loopStringBuilderRandom(state.secureRandom);
    }

    @Benchmark
    public String loopStringBuilderNewSecureRandomIdentifier() {
        return loopStringBuilderRandom(new SecureRandom());
    }

    @Benchmark
    public String streamSharedRandomIdentifier(RandomState state) {
        return streamRandom(state.random);
    }

    @Benchmark
    public String streamThreadLocalRandomIdentifier() {
        return streamRandom(ThreadLocalRandom.current());
    }

    @Benchmark
    public String streamNewRandomIdentifier() {
        return streamRandom(new Random());
    }

    @Benchmark
    public String loopStringBuilderSharedRandomIdentifier(RandomState state) {
        return loopStringBuilderRandom(state.random);
    }

    @Benchmark
    public String loopStringBuilderThreadLocalRandomIdentifier() {
        return loopStringBuilderRandom(ThreadLocalRandom.current());
    }

    @Benchmark
    public String loopStringBuilderNewRandomIdentifier() {
        return loopStringBuilderRandom(new Random());
    }

    @Benchmark
    public String loopCharArraySharedRandomIdentifier(RandomState state) {
        return loopCharArrayRandom(state.random);
    }

    @Benchmark
    public String loopCharArrayThreadLocalRandomIdentifier() {
        return loopCharArrayRandom(ThreadLocalRandom.current());
    }

    @Benchmark
    public String loopCharArrayNewRandomIdentifier() {
        return loopCharArrayRandom(new Random());
    }

    private String streamRandom(Random random) {
        return random.ints(IDENTIFIER_SIZE, 0, ALPHA_NUM.length)
                .mapToObj(index -> ALPHA_NUM[index])
                .reduce(new StringBuilder(IDENTIFIER_SIZE), StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private String loopStringBuilderRandom(Random random) {
        final StringBuilder builder = new StringBuilder(IDENTIFIER_SIZE);
        for (int i = 0; i < IDENTIFIER_SIZE; i++) {
            builder.append(ALPHA_NUM[random.nextInt(ALPHA_NUM.length)]);
        }
        return builder.toString();
    }

    private String loopCharArrayRandom(Random random) {
        final char[] identifier = new char[IDENTIFIER_SIZE];
        for (int i = 0; i < IDENTIFIER_SIZE; i++) {
            identifier[i] = ALPHA_NUM[random.nextInt(ALPHA_NUM.length)];
        }
        return String.valueOf(identifier);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(IdentifierBenchmark.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(10)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
