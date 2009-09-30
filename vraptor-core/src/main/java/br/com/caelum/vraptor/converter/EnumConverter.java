
package br.com.caelum.vraptor.converter;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Accepts either the ordinal value or name. Null and empty strings are treated
 * as null.
 *
 * @author Guilherme Silveira
 */
@Convert(Enum.class)
@ApplicationScoped
public class EnumConverter<T extends Enum<T>> implements Converter<T> {

	/**
	 * Enums are always final, so I can suppress this warning safely
	 */
	@SuppressWarnings("unchecked")
    public T convert(String value, Class<? extends T> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        if (Character.isDigit(value.charAt(0))) {
            return resolveByOrdinal(value, (Class<T>) type, bundle);
        } else {
            return resolveByName(value, (Class<T>) type, bundle);
        }
    }

    private T resolveByName(String value, Class<T> enumType, ResourceBundle bundle) {
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
        }
    }

    private T resolveByOrdinal(String value, Class<T> enumType, ResourceBundle bundle) {
        try {
            int ordinal = Integer.parseInt(value);
            if (ordinal >= enumType.getEnumConstants().length) {
    			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
            }
            return enumType.getEnumConstants()[ordinal];
        } catch (NumberFormatException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
        }
    }

}
