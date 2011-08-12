package br.com.caelum.vraptor.serialization.xstream;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Component used to scan all XStream converters
 *
 * @author Rafael Viana
 * @since 3.4.0
 */
@Component
public class XStreamConverters {

	private final List<Converter> converters;
	private final List<SingleValueConverter> singleValueConverters;
	
	private static final Logger logger = LoggerFactory.getLogger(XStreamConverters.class);
	
	public XStreamConverters(List<Converter> converters, List<SingleValueConverter> singleValueConverters)
	{
		this.converters = Objects.firstNonNull(converters, Lists.<Converter>newArrayList());
		this.singleValueConverters = Objects.firstNonNull(singleValueConverters, Lists.<SingleValueConverter>newArrayList());
	}
	
	/**
	 * Method used to register all the XStream converters scanned to a XStream instance
	 * @param xstream
	 */
	public void registerComponents(XStream xstream)
	{
		for(Converter converter : converters)
		{
			xstream.registerConverter(converter);
			logger.debug("registered Xstream converter for" + converter.getClass().getName());
		}
		
		for(SingleValueConverter converter : singleValueConverters)
		{
			xstream.registerConverter(converter);
			logger.debug("registered Xstream converter for" + converter.getClass().getName());
		}
	}
	
}