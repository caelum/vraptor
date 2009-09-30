
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Double converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Double.class)
@ApplicationScoped
public class DoubleConverter implements Converter<Double> {

    public Double convert(String value, Class<? extends Double> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_number"), value));
        }
    }

}
