package br.com.caelum.vraptor.ioc.spring;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class WebinfClassesPatternResolver extends
		PathMatchingResourcePatternResolver {

	private final String webinfClassesDirectory;

	public WebinfClassesPatternResolver(String webinfClassesDirectory) {
		if (webinfClassesDirectory == null) {
			throw new NullPointerException(
					"web inf class directory cant be null");
		}
		this.webinfClassesDirectory = webinfClassesDirectory;

	}

	protected Resource[] findAllClassPathResources(String location)
			throws IOException {
		return new Resource[] { new UrlResource("file:/"
				+ webinfClassesDirectory) };
	}

}
