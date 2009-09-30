
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive int converter.
 *
 * @author Guilherme Silveira
 * @author Cecilia Fernandes
 */
@Convert(int.class)
@ApplicationScoped
public class PrimitiveIntConverter implements Converter<Integer> {

    public Integer convert(String value, Class<? extends Integer> type, ResourceBundle bundle) {
        if(value==null || value.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
        	throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
