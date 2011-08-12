package br.com.caelum.vraptor.validator;

import java.util.ResourceBundle;

/**
 * Represents a lazy i18n parameter
 * Use:
 * <code>
 *     I18nMessage message = new I18nMessage("category", "key", new I18nParam("lazy.param"));
 * </code>
 * @author Lucas Cavalcanti
 * @since 3.4.0
 */
public class I18nParam {
    private final String key;

    public I18nParam(String key) {
        this.key = key;
    }

    public String getKey(ResourceBundle bundle) {
        return bundle.getString(key);
    }

    @Override
    public String toString() {
        return String.format("i18n(%s)", key);
    }
}
