package br.com.caelum.vraptor.interceptor.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileDownloadTest {

	private File file;
	private byte[] bytes;
	private Mockery mockery;
	private HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception {
		this.mockery = new Mockery();
		this.response = mockery.mock(HttpServletResponse.class);

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
		FileDownload fd = new FileDownload(file, "type", "x.txt", false);

		mockery.checking(new Expectations() {
			{
				one(response).getOutputStream();
				will(returnValue(socketStream));

				ignoring(anything());
			}
		});

		fd.write(response);

		Assert.assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		FileDownload fd = new FileDownload(file, "type", "x.txt", false);

		mockery.checking(new Expectations() {
			{
				one(response).getOutputStream();
				will(returnValue(socketStream));

				one(response).setHeader("Content-type", "type");

				ignoring(anything());
			}
		});

		fd.write(response);

		Assert.assertArrayEquals(bytes, outputStream.toByteArray());
	}

}
