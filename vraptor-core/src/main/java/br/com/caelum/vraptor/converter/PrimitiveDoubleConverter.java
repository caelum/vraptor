
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive double converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(double.class)
@ApplicationScoped
public class PrimitiveDoubleConverter implements Converter<Double> {

    public Double convert(String value, Class<? extends Double> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return 0d;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_number"), value));
        }
    }

}
