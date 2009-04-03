package br.com.caelum.vraptor.converter;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

@Convert(int.class)
public class PrimitiveIntConverter implements Converter {

    public Object convert(String value) {
        if(value==null) {
            // TODO validation??
            throw new IllegalArgumentException("Unable to convert null to primitive int");
        }
        return Integer.parseInt(value);
    }
    
}
