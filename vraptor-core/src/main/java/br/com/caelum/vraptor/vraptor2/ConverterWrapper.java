
package br.com.caelum.vraptor.vraptor2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.vraptor.converter.ConversionException;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;


/**
 * Wraps a vraptor2 converter in a vraptor3 converter.
 * 
 * @author guilherme silveira
 */
@SuppressWarnings("unchecked")
public class ConverterWrapper implements Converter {

	private final org.vraptor.converter.Converter converter;

	public ConverterWrapper(org.vraptor.converter.Converter converter) {
		this.converter = converter;
	}

	public Object convert(String value, Class type, ResourceBundle bundle) {
		try {
			return converter.convert(value, type, null);
		} catch (ConversionException e) {
			// prints stack trace because its an internal problem that ocurred
			e.printStackTrace();
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_valid"), value));
		}
	}

}
