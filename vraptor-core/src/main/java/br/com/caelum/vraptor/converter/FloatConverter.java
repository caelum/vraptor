
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Float converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Float.class)
@ApplicationScoped
public class FloatConverter implements Converter<Float> {

    public Float convert(String value, Class<? extends Float> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_number"), value));
        }
    }

}
