package br.com.caelum.vraptor.validator;

public class If<T> {

    @SuppressWarnings("unchecked")
    private static final Then NOTHING = new Then(new Validations()) {
        public void then(Validations validations) {
            // does nothing
        }
    };
    
    private final T instance;
    private final Validations actual;

    public If(T instance, Validations actual) {
        this.instance = instance;
        this.actual = actual;
    }

    @SuppressWarnings("unchecked")
    public Then<T> isNotNull() {
        if (instance == null) {
            return NOTHING;
        }
        return new Then<T>(actual);
    }
    
}
