package br.com.caelum.vraptor.serialization;

import java.io.IOException;

import br.com.caelum.vraptor.View;

/**
 * Creates a serializer of a given format with out ROOT alias.
 *
 * @author Tomaz Lavieri
 * @version 3.1.2
 */
public interface NoRootSerialization extends View {
	/**
	 * Serializes this object to the clients writer with out ROOT alias.
	 * @throws IOException
	 */
	public <T> Serializer from(T object);
}
