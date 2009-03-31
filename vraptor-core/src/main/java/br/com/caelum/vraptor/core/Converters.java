package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.Converter;

/**
 * Represents a collection of converters.<br>
 * Note: This interface will probably change in the near future to allow
 * annotation support.
 * 
 * @author Guilherme Silveira
 */
public interface Converters {

    /**
     * Extracts a converter for this specific type.
     * 
     * @param type
     * @return
     */
    Converter to(Class<?> type);

}
