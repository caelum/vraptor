package br.com.caelum.vraptor.vraptor2;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.vraptor.converter.ConversionException;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.validator.ValidationMessage;

public class ConverterWrapper implements Converter {

    private final org.vraptor.converter.Converter converter;

    public ConverterWrapper(org.vraptor.converter.Converter converter) {
        this.converter = converter;
    }

    public Object convert(String value, Class type, List errors, ResourceBundle bundle) {
        try {
            return converter.convert(value, type, null);
        } catch (ConversionException e) {
        	// prints stack trace because its an internal problem that ocurred
        	e.printStackTrace();
			errors.add(new ValidationMessage(MessageFormat.format(bundle.getString("is_not_valid"), value), ""));
			return null;
        }
    }

}
