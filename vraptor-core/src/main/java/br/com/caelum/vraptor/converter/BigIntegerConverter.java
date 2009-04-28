package br.com.caelum.vraptor.converter;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * VRaptor's BigInteger converter. 
 * 
 * @author Cecilia Fernandes
 */
@Convert(BigInteger.class)
@ApplicationScoped
public class BigIntegerConverter implements Converter<BigInteger>{

	public BigInteger convert(String value, Class<? extends BigInteger> type, List<ValidationMessage> errors, ResourceBundle bundle) {
		if (value == null || value.equals("")) {
			return null;
		}
		try {
			return new BigInteger(value);
		} catch (NumberFormatException e) {
			errors.add(new ValidationMessage(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value), ""));
			return null;
		}
		
	}

}
