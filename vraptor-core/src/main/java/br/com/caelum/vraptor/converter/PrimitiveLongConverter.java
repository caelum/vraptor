
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive long converter.
 *
 * @author Cecilia Fernandes
 * @author Guilherme Silveira
 */
@Convert(long.class)
@ApplicationScoped
public class PrimitiveLongConverter implements Converter<Long> {

    public Long convert(String value, Class<? extends Long> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
