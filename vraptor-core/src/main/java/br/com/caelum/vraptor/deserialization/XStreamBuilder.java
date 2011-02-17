package br.com.caelum.vraptor.deserialization;

import com.thoughtworks.xstream.XStream;

/**
 *  Interface that defines needed methods to create a configured XStream instance
 *
 * @author Rafael Viana
 */
public interface XStreamBuilder {
	
	public XStream xmlInstance();
	
	public XStream jsonInstance();
	
	public XStreamBuilder indented();
	
	public XStreamBuilder withoutRoot();
	
}