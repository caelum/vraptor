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
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles download by reading from a input stream byte by byte.
 *
 * @author filipesabella
 *
 * @deprecated You should use FileDownload which has an InputStream construtor.
 */
@Deprecated
public class InputStreamDownload implements Download {
	private final FileDownload download;

	public InputStreamDownload(InputStream input, String contentType, String fileName) {
		download = new FileDownload(input, 0, contentType, fileName, false, new HashMap<String, String>());
	}

	public InputStreamDownload(InputStream input, String contentType, String fileName, boolean doDownload) {
		download = new FileDownload(input, 0, contentType, fileName, doDownload, new HashMap<String, String>());
	}

	public void write(HttpServletResponse response) throws IOException {
		download.write(response);
	}
}
