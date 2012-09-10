package br.com.caelum.vraptor.interceptor.download;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JFreeChartDownloadTest {

	private JFreeChart chart;
	private byte[] bytes;
	private @Mock HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;
	private int width;
	private int height;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		this.width = 400;
		this.height = 300;
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for (double i = 0.0; i < 10; i++) {
			dataset.addValue(i, "A",  new Double(i*0.7));
		}
		
		chart = ChartFactory.createLineChart("Chart Test", "Axis X", "Axis Y", dataset , PlotOrientation.HORIZONTAL, false, false, false);
		bytes = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
		
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
		JFreeChartDownload chartDownload = new JFreeChartDownload(chart, width, height);
		chartDownload.write(response);

		assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		JFreeChartDownload chartDownload = new JFreeChartDownload(chart, width, height);
		chartDownload.write(response);

		assertArrayEquals(bytes, outputStream.toByteArray());
		verify(response).setHeader("Content-type", "image/png");
	}
}
