package br.com.caelum.vraptor.converter;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * VRaptor's BigDecimal converter. 
 * 
 * @author Cecilia Fernandes
 */
@Convert(BigDecimal.class)
@ApplicationScoped
public class BigDecimalConverter implements Converter<BigDecimal>{

	public BigDecimal convert(String value, Class<? extends BigDecimal> type, List<ValidationMessage> errors, ResourceBundle bundle) {
		if (value == null || value.equals("")) {
			return null;
		}
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException e) {
			errors.add(new ValidationMessage(MessageFormat.format(bundle.getString("is_not_a_valid_number"), value), ""));
			return null;
		}
		
	}

}
