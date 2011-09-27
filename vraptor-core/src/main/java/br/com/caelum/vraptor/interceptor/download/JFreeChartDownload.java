/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

	public void write(HttpServletResponse response) throws IOException {
		byte[] bs = ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));

		new ByteArrayDownload(bs, "image/png", fileName, true).write(response);
	}

}
