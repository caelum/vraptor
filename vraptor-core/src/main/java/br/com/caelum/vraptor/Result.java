package br.com.caelum.vraptor;

/**
 * A resource requisition result.
 * 
 * @author Guilherme Silveira
 */
public interface Result {

    void include(String key, Object value);

	<T extends View> T use(Class<T> view);

}
