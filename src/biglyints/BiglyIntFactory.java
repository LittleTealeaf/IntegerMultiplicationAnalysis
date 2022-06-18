package biglyints;

public enum BiglyIntFactory {
    A(BiglyIntA::new, "Regular"), B(BiglyIntB::new, "Karatsuba"), C(BiglyIntC::new, "Java BigInt");

    String name;
    Generator generator;

    BiglyIntFactory(Generator generator, String name) {
        this.name = name;
        this.generator = generator;
    }

    public BiglyInt create(String val) {
        return generator.generate(val);
    }

    interface Generator {
        BiglyInt generate(String val);
    }
}
