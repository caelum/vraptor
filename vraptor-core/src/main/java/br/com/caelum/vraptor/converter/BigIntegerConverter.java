package br.com.caelum.vraptor.converter;

import java.math.BigInteger;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's BigInteger converter. 
 * 
 * @author Cecilia Fernandes
 */
@Convert(BigInteger.class)
@ApplicationScoped
public class BigIntegerConverter implements Converter<BigInteger>{

	@Override
	public BigInteger convert(String value, Class<? extends BigInteger> type) {
		if (value == null || value.equals("")) {
			return null;
		}
		try {
			return new BigInteger(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Unable to convert '" + value + "'.");
		}
		
	}

}
