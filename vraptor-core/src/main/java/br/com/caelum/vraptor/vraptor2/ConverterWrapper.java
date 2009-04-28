package br.com.caelum.vraptor.vraptor2;

import java.util.ResourceBundle;

import org.vraptor.converter.ConversionException;

import br.com.caelum.vraptor.Converter;

public class ConverterWrapper implements Converter {

    private final org.vraptor.converter.Converter converter;

    public ConverterWrapper(org.vraptor.converter.Converter converter) {
        this.converter = converter;
    }

    public Object convert(String value, Class type, List<ValidationMessage> errors, ResourceBundle bundle) {
        try {
            return converter.convert(value, type, null);
        } catch (ConversionException e) {
            throw new IllegalArgumentException("Unable to convert using vraptor2 converter " + converter.getClass().getName() + " for value " + value);
        }
    }

}
