package br.com.caelum.vraptor.proxy;

import java.util.Properties;

/**
 * @author Fabio Kung
 */
public class TheClassWithManyConstructors {
    private Number number;
    private boolean numberConstructorCalled;

    public TheClassWithManyConstructors(String nullValue) {
        // constructor calling methods on dependencies
        nullValue.toString();
    }

    public TheClassWithManyConstructors(Properties nullValue) throws IllegalAccessException {
        throw new IllegalAccessException("constructor with errors");
    }

    public TheClassWithManyConstructors(Number number) {
        this.number = number;
        this.numberConstructorCalled = true;
    }

    public boolean wasNumberConstructorCalled() {
        return numberConstructorCalled;
    }

    public Number getNumber() {
        return number;
    }
}
