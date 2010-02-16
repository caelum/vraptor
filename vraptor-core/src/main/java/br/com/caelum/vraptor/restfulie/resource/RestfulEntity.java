package br.com.caelum.vraptor.restfulie.resource;

import java.util.Calendar;

/**
 * An interface that defines HTTP entities characteristics for a resource.<br/>
 * 
 * @author Lucas Cavalcanti 
 * @author Cecilia Fernandes
 */
public interface RestfulEntity extends Cacheable{

	String getEtag();

	Calendar getLastModified();
}
