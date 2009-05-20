package br.com.caelum.vraptor.converter;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * Locale based date converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Date.class)
@RequestScoped
public class LocaleBasedDateConverter implements Converter<Date> {

    private final JstlWrapper jstlWrapper = new JstlWrapper();

    private final RequestInfo request;

    public LocaleBasedDateConverter(RequestInfo request) {
        this.request = request;
    }

    public Date convert(String value, Class<? extends Date> type, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        Locale locale = jstlWrapper.findLocale(request);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        try {
            return format.parse(value);
        } catch (ParseException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_date"), value));
        }
    }

}
