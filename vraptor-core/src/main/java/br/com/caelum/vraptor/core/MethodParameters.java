package br.com.caelum.vraptor.core;

/**
 * Represents all method parameters
 * 
 * @author Guilherme Silveira
 */
public interface MethodParameters {
    void set(Object[] parameters, String[] names);

    Object[] getValues();
}
