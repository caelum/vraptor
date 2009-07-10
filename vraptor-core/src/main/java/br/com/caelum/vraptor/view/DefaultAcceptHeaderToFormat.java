package br.com.caelum.vraptor.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * The default AcceptHeaderToFormat implementation
 * 
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
@ApplicationScoped
public class DefaultAcceptHeaderToFormat implements AcceptHeaderToFormat {

	private static final String DEFAULT_FORMAT = "html";
	private final Map<String, String> map;

	public DefaultAcceptHeaderToFormat() {
		map = new ConcurrentHashMap<String, String>();
		map.put("text/html", "html");
		map.put("application/json", "json");
		// TODO add more mime types
	}
	
	public String getFormat(String acceptHeader) {
		String[] mimeTypes = acceptHeader.split("(;[^,]*)?,\\s*");

		for (String mimeType : mimeTypes) {
			String format = map.get(mimeType);
			if (format != null)
				return format;
		}
		
		return DEFAULT_FORMAT;
	}
}
