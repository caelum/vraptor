
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive byte converter.
 *
 * @author Cecilia Fernandes
 * @author Guilherme Silveira
 */
@Convert(byte.class)
@ApplicationScoped
public class PrimitiveByteConverter implements Converter<Byte> {

    public Byte convert(String value, Class<? extends Byte> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return (byte) 0;
        }
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
