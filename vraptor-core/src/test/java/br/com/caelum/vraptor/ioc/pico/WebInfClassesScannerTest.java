package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.ioc.pico.WebInfClassesScanner;
import br.com.caelum.vraptor.ioc.pico.DirScanner;

public class WebInfClassesScannerTest {

	private Mockery mockery;

	@Before
	public void setup() {
		mockery = new Mockery();
	}

	@Test
	public void testStartsSearchingAtWebInfClasses() {
		final DirScanner scanner = mockery.mock(DirScanner.class);
		final ServletContext context = mockery.mock(ServletContext.class);
		final String myPath = "myPath";
		final ResourceRegistry registry = mockery.mock(ResourceRegistry.class);
		mockery.checking(new Expectations() {
			{
				one(context).getRealPath("");
				will(returnValue(myPath));
				one(scanner)
						.scan(new File(new File(myPath), "WEB-INF/classes"));
				will(returnValue(new ArrayList<Resource>()));
				// TODO i forgot how to do it without the cast :)
				one(registry)
						.register(
								(List<br.com.caelum.vraptor.resource.Resource>) with(a(List.class)));
			}
		});
		new WebInfClassesScanner(context, scanner, registry).loadAll();
		mockery.assertIsSatisfied();
	}

}
