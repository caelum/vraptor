package br.com.caelum.vraptor.converter;

import java.math.BigDecimal;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's BigDecimal converter. 
 * 
 * @author Cecilia Fernandes
 */
@Convert(BigDecimal.class)
@ApplicationScoped
public class BigDecimalConverter implements Converter<BigDecimal>{

	@Override
	public BigDecimal convert(String value, Class<? extends BigDecimal> type) {
		if (value == null || value.equals("")) {
			return null;
		}
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Unable to convert '" + value + "'.");
		}
		
	}

}
