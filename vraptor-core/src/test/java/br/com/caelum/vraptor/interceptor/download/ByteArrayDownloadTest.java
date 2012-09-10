package br.com.caelum.vraptor.interceptor.download;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ByteArrayDownloadTest {

	private byte[] bytes;
	private @Mock HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		bytes = new byte[] { (byte) 0 };
		this.outputStream = new ByteArrayOutputStream();

		this.socketStream = new ServletOutputStream() {
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
		};

		when(response.getOutputStream()).thenReturn(socketStream);
	}

	@Test
	public void shouldFlushWholeStreamToHttpResponse() throws IOException {
		ByteArrayDownload fd = new ByteArrayDownload(bytes, "type", "x.txt");
		fd.write(response);

		assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
	    ByteArrayDownload fd = new ByteArrayDownload(bytes, "type", "x.txt");
		fd.write(response);

		verify(response, times(1)).setHeader("Content-type", "type");
		assertArrayEquals(bytes, outputStream.toByteArray());
	}
}
