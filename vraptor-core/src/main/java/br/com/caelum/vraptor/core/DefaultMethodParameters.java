package br.com.caelum.vraptor.core;

/**
 * Default implementation of method parameters.
 * 
 * @author Guilherme Silveira
 */
public class DefaultMethodParameters implements MethodParameters {

    private Object[] parameters;
    private String[] names;

    public void set(Object[] parameters, String[] names) {
        this.parameters = parameters;
        this.names = names;
    }

    public Object[] getValues() {
        return parameters;
    }

}
