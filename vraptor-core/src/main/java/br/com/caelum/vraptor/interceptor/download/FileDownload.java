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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

/**
 * Reads bytes from a file into the result.
 *
 * @author filipesabella
 */
public class FileDownload implements Download {
	private final File file;
	private final String contentType;
	private final String fileName;
	private final boolean doDownload;
	private final Map<String, String> headers;

	public FileDownload(File file, String contentType, String fileName) {
		this(file, contentType, fileName, false);
	}

	public FileDownload(File file, String contentType, String fileName, boolean doDownload) {
		this(file, contentType, fileName, false, new HashMap<String, String>());
	}

	public FileDownload(File file, String contentType, String fileName, boolean doDownload, Map<String, String> headers) {
		this.file = file;
		this.contentType = contentType;
		this.fileName = fileName;
		this.doDownload = doDownload;
		this.headers = headers;
	}

	public void write(HttpServletResponse response) throws IOException {
		writeDetails(response);
		// too much memory may be used!
		new FileInputStream(file).getChannel().transferTo(0, file.length(),
				Channels.newChannel(response.getOutputStream()));
	}

	void writeDetails(HttpServletResponse response) {
		if (contentType != null) {
			String header = String.format("%s; filename=%s", doDownload ? "attachment" : "inline", fileName);
			response.setHeader("Content-disposition", header);
		}
		if (!headers.containsKey("Content-Length")) {
			headers.put("Content-Length", Long.toString(file.length()));
		}
		for (Entry<String, String> e : headers.entrySet()) {
			headers.put(e.getKey(), e.getValue());
		}
	}
}
