package br.com.caelum.vraptor.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * Locale based calendar converter.
 * 
 * @author Guilherme Silveira
 */
@Convert(Calendar.class)
public class LocaleBasedCalendarConverter implements Converter<Calendar> {
    
    private final JstlWrapper jstlWrapper = new JstlWrapper();

    private static final Logger logger = Logger.getLogger(LocaleBasedCalendarConverter.class);

    private final VRaptorRequest request;
    
    public LocaleBasedCalendarConverter(VRaptorRequest request) {
        this.request = request;
    }

    public Calendar convert(String value, Class<? extends Calendar> type) {
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
            // TODO validation?
            throw new IllegalArgumentException("Unable to convert '" + value + "'.");
        }
    }

}
