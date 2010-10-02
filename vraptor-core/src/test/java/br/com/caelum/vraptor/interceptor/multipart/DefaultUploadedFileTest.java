package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Test;

public class DefaultUploadedFileTest {

	@Test
	public void usingUnixLikeSeparators() throws Exception {
		DefaultUploadedFile file = new DefaultUploadedFile(new NullInputStream(0), "/a/unix/path/file.txt", "text/plain");

		assertThat(file.getFileName(), is("file.txt"));
	}

	@Test
	public void usingWindowsLikeSeparators() throws Exception {
		DefaultUploadedFile file = new DefaultUploadedFile(new NullInputStream(0), "C:\\a\\windows\\path\\file.txt", "text/plain");

		assertThat(file.getFileName(), is("file.txt"));
	}
	
	public void usingOnlyFilename() {
        DefaultUploadedFile file = new DefaultUploadedFile(new NullInputStream(0), "file.txt", "text/plain");

        assertThat(file.getFileName(), is("file.txt"));
	}
}
