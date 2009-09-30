
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Short converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Short.class)
@ApplicationScoped
public class ShortConverter implements Converter<Short> {

    public Short convert(String value, Class<? extends Short> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
        	throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
