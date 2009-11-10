package br.com.caelum.vraptor.serialization;

import java.io.IOException;

import br.com.caelum.vraptor.View;

/**
 * Basic xml serialization support using XmlSerializer.
 * 
 * @author guilherme silveira
 * @version 3.0.3
 */
public interface XmlSerialization extends View {

	/**
	 * Serializes this object to the clients writer.
	 * @throws IOException
	 */
	public <T> XmlSerializer from(T object);

}
