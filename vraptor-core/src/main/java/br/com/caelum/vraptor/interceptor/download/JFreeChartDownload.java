package br.com.caelum.vraptor.interceptor.download;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

/**
 * Reads bytes from a JFreeChart into the result.
 * 
 * @author David Paniz
 * 
 * @see InputStreamDownload
 * @see ByteArrayDownload
 */
public class JFreeChartDownload implements Download {

	private final JFreeChart chart;
	private final int width;
	private final int height;
	private final String fileName;

	public JFreeChartDownload(JFreeChart chart, int width, int height) {
		this(chart, width, height, "chart.png");
	}
	public JFreeChartDownload(JFreeChart chart, int width, int height, String fileName) {
		this.chart = chart;
		this.width = width;
		this.height = height;
		this.fileName = fileName;
	}

	@Override
	public void write(HttpServletResponse response) throws IOException {
		byte[] bs = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));

		new ByteArrayDownload(bs, "image/png", fileName, true).write(response);
	}

}
