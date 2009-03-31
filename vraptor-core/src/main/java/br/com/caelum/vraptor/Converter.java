package br.com.caelum.vraptor;

import java.util.List;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.http.ParametersProvider;

/**
 * Converts a string value to an object.
 * 
 * @author Guilherme Silveira
 */
public interface Converter {

    Object convert(List<String> keys, Converters converters, ParametersProvider provider);

}
