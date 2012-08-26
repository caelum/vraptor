package br.com.caelum.vraptor.interceptor.download;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FileDownloadTest {

	private File file;
	private byte[] bytes;
	private @Mock HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.bytes = new byte[10000];
		for (int i = 0; i < 10000; i++) {
			// just to have something not trivial
			bytes[i] = (byte) (i % 100);
		}
		this.outputStream = new ByteArrayOutputStream();

		this.file = File.createTempFile("test", "vraptor");
		FileOutputStream fileStream = new FileOutputStream(file);
		fileStream.write(bytes);
		fileStream.close();

		this.socketStream = new ServletOutputStream() {
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
		};

	}

	@Test
	public void shouldFlushWholeFileToHttpResponse() throws IOException {
		FileDownload fd = new FileDownload(file, "type");
		
		when(response.getOutputStream()).thenReturn(socketStream);
		fd.write(response);

		assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		FileDownload fd = new FileDownload(file, "type", "x.txt", false);

		when(response.getOutputStream()).thenReturn(socketStream);
		fd.write(response);
		verify(response, times(1)).setHeader("Content-type", "type");

		assertArrayEquals(bytes, outputStream.toByteArray());
	}
}
