
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive short converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(short.class)
@ApplicationScoped
public class PrimitiveShortConverter implements Converter<Short> {

    public Short convert(String value, Class<? extends Short> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return (short) 0;
        }
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
