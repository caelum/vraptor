package br.com.caelum.vraptor.interceptor.multipart;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Test;

public class DefaultUploadedFileTest {

	private static final NullInputStream CONTENT = new NullInputStream(0);

    @Test
	public void usingUnixLikeSeparators() throws Exception {
		DefaultUploadedFile file = new DefaultUploadedFile(CONTENT, "/a/unix/path/file.txt", "text/plain");

		assertThat(file.getFileName(), is("file.txt"));
	}

	@Test
	public void usingWindowsLikeSeparators() throws Exception {
		DefaultUploadedFile file = new DefaultUploadedFile(CONTENT, "C:\\a\\windows\\path\\file.txt", "text/plain");

		assertThat(file.getFileName(), is("file.txt"));
	}
	
	public void usingOnlyFilename() {
        DefaultUploadedFile file = new DefaultUploadedFile(CONTENT, "file.txt", "text/plain");

        assertThat(file.getFileName(), is("file.txt"));
	}
}
