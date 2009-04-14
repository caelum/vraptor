package br.com.caelum.vraptor.core;

public class MethodParameters {

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
