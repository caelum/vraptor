package br.com.caelum.vraptor.example.custom;

import java.math.BigDecimal;

import org.vraptor.LogicRequest;
import org.vraptor.converter.ConversionException;
import org.vraptor.converter.Converter;

public class BigDecimalConverter implements Converter{

    public Object convert(String value, Class<?> arg1, LogicRequest arg2) throws ConversionException {
        if(value==null) {
            return null;
        }
        return new BigDecimal("1" + value);
    }

    public Class<?>[] getSupportedTypes() {
        return new Class<?>[]{BigDecimal.class};
    }

}
