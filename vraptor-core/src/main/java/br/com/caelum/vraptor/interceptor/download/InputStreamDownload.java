/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.interceptor.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

/**
 * Handles download by reading from a input stream byte by byte.
 *
 * @author filipesabella
 * @author Paulo Silveira
 *
 * @see ByteArrayDownload
 * @see FileDownload
 */
public class InputStreamDownload implements Download {
	private final InputStream stream;
	private final String contentType;
	private final String fileName;
	private final boolean doDownload;
	private final long size;

	public InputStreamDownload(InputStream input, String contentType, String fileName) {
		this(input, contentType, fileName, false, 0);
	}

	public InputStreamDownload(InputStream input, String contentType, String fileName, boolean doDownload, long size) {
		this.stream = input;
		this.size = size;
		this.contentType = contentType;
		this.fileName = fileName;
		this.doDownload = doDownload;
	}

	public void write(HttpServletResponse response) throws IOException {
		writeDetails(response);

		OutputStream out = response.getOutputStream();
		ByteStreams.copy(stream, out);
		
		stream.close();
	}

	void writeDetails(HttpServletResponse response) {
		if (contentType != null) {
			String contentDisposition = String
					.format("%s; filename=%s", doDownload ? "attachment" : "inline", fileName);
			response.setHeader("Content-disposition", contentDisposition);
			response.setHeader("Content-type", contentType);
		}
		if (size > 0) {
			response.setHeader("Content-Length", Long.toString(size));
		}
	}

}
