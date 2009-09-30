
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive float converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(float.class)
@ApplicationScoped
public class PrimitiveFloatConverter implements Converter<Float> {

    public Float convert(String value, Class<? extends Float> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return 0f;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_number"), value));
        }
    }

}
