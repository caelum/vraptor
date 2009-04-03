package br.com.caelum.vraptor.converter;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

@Convert(Long.class)
public class LongConverter implements Converter<Long> {

    public Long convert(String value) {
        return Long.valueOf(value);
    }

}
