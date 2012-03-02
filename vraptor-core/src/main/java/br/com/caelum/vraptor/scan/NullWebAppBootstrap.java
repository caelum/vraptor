package br.com.caelum.vraptor.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;

public class NullWebAppBootstrap implements WebAppBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(NullWebAppBootstrap.class);

	public void configure(ComponentRegistry registry) {
		logger.info("Classpath scanning is disabled.");
	}

}
