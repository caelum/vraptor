package br.com.caelum.vraptor.vraptor2.outject;

/**
 * Instances implementing this interface are responsible for outjecting values
 * from the business logic to your view system.<br>
 * This interface exists only for vraptor2 compatibility mode.
 * 
 * @author Guilherme Silveira
 */
public interface Outjecter {

    /**
     * Exports a value to the view system.
     */
    void include(String name, Object value);

}
