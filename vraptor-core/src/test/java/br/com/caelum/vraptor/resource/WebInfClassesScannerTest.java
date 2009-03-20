package br.com.caelum.vraptor.resource;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Resource;

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
		mockery.checking(new Expectations() {
			{
				one(context).getRealPath("");
				will(returnValue(myPath));
				one(scanner)
						.scan(new File(new File(myPath), "WEB-INF/classes"));
				will(returnValue(new ArrayList<Resource>()));
			}
		});
		new WebInfClassesScanner(context, scanner).loadAll();
		mockery.assertIsSatisfied();
	}

}
