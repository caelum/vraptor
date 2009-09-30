
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * VRaptor's Character converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Character.class)
@ApplicationScoped
public class CharacterConverter implements Converter<Character> {

    public Character convert(String value, Class<? extends Character> type, ResourceBundle bundle) {
        if (value == null || value.length()==0) {
            return null;
        }
        if(value.length()!=1) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_character"), value));
        }
        return value.charAt(0);
    }

}
