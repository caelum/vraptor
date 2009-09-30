
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Byte converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Byte.class)
@ApplicationScoped
public class ByteConverter implements Converter<Byte> {

    public Byte convert(String value, Class<? extends Byte> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        try {
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
