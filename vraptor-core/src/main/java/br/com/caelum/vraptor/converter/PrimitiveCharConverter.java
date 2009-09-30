
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's primitive char converter.
 *
 * @author Cecilia Fernandes
 */
@Convert(char.class)
@ApplicationScoped
public class PrimitiveCharConverter implements Converter<Character> {

    public Character convert(String value, Class<? extends Character> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return '\u0000';
        }
        if (value.length() != 1) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_character"), value));
        }
        return value.charAt(0);
    }

}