package br.com.caelum.vraptor.ioc.pico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import br.com.caelum.vraptor.ioc.pico.DefaultDirScanner;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.DefaultResource;


public class DefaultDirScannerTest {

	private File baseDir;
	private DefaultDirScanner scanner;

	@Before
	public void setup() {
		this.baseDir = new File(DefaultDirScannerTest.class.getResource("/")
				.getPath());
		this.scanner = new DefaultDirScanner();
	}

	@Test
	public void testAcceptsClassWithinFolders() {
		List<Resource> results = scanner.scan(baseDir);
		MatcherAssert.assertThat(results, Matchers.hasItem(new DefaultResource(MyResource.class)));
	}

	@Test
	public void testDoesntRegardNonAnnotatedTypesAsResources() {
		List<Resource> results = scanner.scan(baseDir);
		assertThat(results, not(hasItem(new DefaultResource(MyFakeResource.class))));
	}

	@br.com.caelum.vraptor.Resource
	public static class MyResource {
	}
	
	public static class MyFakeResource {
		
	}

}
