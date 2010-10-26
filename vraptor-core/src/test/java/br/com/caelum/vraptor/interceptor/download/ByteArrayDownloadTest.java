package br.com.caelum.vraptor.interceptor.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ByteArrayDownloadTest {

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

		this.socketStream = new ServletOutputStream() {
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
		};

	}

	@Test
	public void shouldFlushWholeStreamToHttpResponse() throws IOException {
		ByteArrayDownload fd = new ByteArrayDownload(bytes, "type", "x.txt");

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
	    ByteArrayDownload fd = new ByteArrayDownload(bytes, "type", "x.txt");

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
