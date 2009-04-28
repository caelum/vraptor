package br.com.caelum.vraptor.converter;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * Locale based calendar converter.
 * 
 * @author Guilherme Silveira
 */
@Convert(Calendar.class)
@RequestScoped
public class LocaleBasedCalendarConverter implements Converter<Calendar> {
    
    private final JstlWrapper jstlWrapper = new JstlWrapper();
    
    private static final Logger logger = LoggerFactory.getLogger(LocaleBasedCalendarConverter.class);

    private final VRaptorRequest request;
    
    public LocaleBasedCalendarConverter(VRaptorRequest request) {
        this.request = request;
    }

    public Calendar convert(String value, Class type, List<ValidationMessage> errors, ResourceBundle bundle) {
        if (value == null || value.equals("")) {
            return null;
        }
        Locale locale = jstlWrapper.findLocale(request);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        try {
            Date date = format.parse(value);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
			errors.add(new ValidationMessage(MessageFormat.format(bundle.getString("is_not_a_valid_date"), value), ""));
			return null;
        }
    }

}
