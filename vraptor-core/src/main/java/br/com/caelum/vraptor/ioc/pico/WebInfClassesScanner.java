package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.pico.DirScanner;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.resource.Resource;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

public class WebInfClassesScanner implements ResourceLocator {

	private final File classes;

	private final DirScanner scanner;

	private final ResourceRegistry registry;

	public WebInfClassesScanner(ServletContext context, DirScanner scanner, ResourceRegistry registry) {
		this.registry = registry;
		String path = context.getRealPath("");
		this.classes = new File(path, "WEB-INF/classes");
		this.scanner = scanner;
	}

	public void loadAll() {
		// TODO Auto-generated method stub
		System.out.println("Starting looking for " + classes.getAbsolutePath());
		// TODO this should be in a start/config method... tried with pico but was unable... urgh!
		List<Resource> results = scanner.scan(classes);
		System.out.println("Resources found: " + results);
		this.registry.register(results);
	}

}
