package br.com.caelum.vraptor.restfulie.headers;

import java.util.Calendar;

import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;

public interface RestDefaults {

	Calendar getLastModifiedFor(HypermediaResource resource);

	String getEtagFor(HypermediaResource resource);

}
