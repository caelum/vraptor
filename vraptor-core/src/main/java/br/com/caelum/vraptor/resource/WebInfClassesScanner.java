package br.com.caelum.vraptor.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

public class WebInfClassesScanner implements ResourceLocator {

	private final File classes;

	private final List<Resource> resources;

	private final DirScanner scanner;

	public WebInfClassesScanner(ServletContext context, DirScanner scanner) {
		String path = context.getRealPath("");
		this.classes = new File(path, "WEB-INF/classes");
		this.resources = new ArrayList<Resource>();
		this.scanner = scanner;
	}

	public void loadAll() {
		// TODO Auto-generated method stub
		System.out.println("Starting looking for " + classes.getAbsolutePath());
		// TODO this should be in a start/config method... tried with pico but was unable... urgh!
		this.resources.addAll(scanner.scan(classes));
		System.out.println("Resources found: " + resources);
	}

}
