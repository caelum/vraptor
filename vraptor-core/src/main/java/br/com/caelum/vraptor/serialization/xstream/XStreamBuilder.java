package br.com.caelum.vraptor.serialization.xstream;

import com.thoughtworks.xstream.XStream;

/**
 *  Interface that defines needed methods to create a configured XStream instance
 *
 * @author Rafael Viana
 * @since 3.4.0
 */
public interface XStreamBuilder {
	
	public XStream xmlInstance();
	
	public XStream jsonInstance();
	
	public XStreamBuilder indented();
	
	public XStreamBuilder withoutRoot();
	
}