package br.com.caelum.vraptor.interceptor.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JFreeChartDownloadTest {

	private JFreeChart chart;
	private byte[] bytes;
	private Mockery mockery;
	private HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;
	private int width;
	private int height;

	@Before
	public void setUp() throws Exception {
		this.width = 400;
		this.height = 300;
		
		this.mockery = new Mockery();
		this.response = mockery.mock(HttpServletResponse.class);

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for (double i = 0.0; i < 10; i++) {
			dataset.addValue(i, "A",  new Double(i*0.7));
		}
		
		this.chart = ChartFactory.createLineChart("Chart Test", "Axis X", "Axis Y", dataset , PlotOrientation.HORIZONTAL, false, false, false);
		bytes = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
		
		this.outputStream = new ByteArrayOutputStream();
		this.socketStream = new ServletOutputStream() {
			public void write(int b) throws IOException {
				outputStream.write(b);
			}
		};

	}

	@Test
	public void shouldFlushWholeStreamToHttpResponse() throws IOException {
		JFreeChartDownload chartDownload = new JFreeChartDownload(chart, width, height);

		mockery.checking(new Expectations() {
			{
				one(response).getOutputStream();
				will(returnValue(socketStream));

				ignoring(anything());
			}
		});

		chartDownload.write(response);

		Assert.assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		JFreeChartDownload chartDownload = new JFreeChartDownload(chart, width, height);

		mockery.checking(new Expectations() {
			{
				one(response).getOutputStream();
				will(returnValue(socketStream));

				one(response).setHeader("Content-type", "image/png");

				ignoring(anything());
			}
		});

		chartDownload.write(response);

		Assert.assertArrayEquals(bytes, outputStream.toByteArray());
	}

}
