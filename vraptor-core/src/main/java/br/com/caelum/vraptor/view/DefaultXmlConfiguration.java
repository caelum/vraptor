package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.ioc.Component;

/**
 * Basic xml configuration for xml serialization.
 * @author guilherme silveira
 * @since 3.0.2
 */
@Component
public class DefaultXmlConfiguration implements XmlConfiguration {

	public String nameFor(String name) {
		if(name.length()==1) {
			return name.toLowerCase();
		}
		StringBuilder content = new StringBuilder();
		content.append(Character.toLowerCase(name.charAt(0)));
		for(int i=1;i<name.length();i++) {
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				content.append("_");
				content.append(Character.toLowerCase(c));
			} else {
				content.append(c);
			}
		}
		return content.toString();
	}
}
