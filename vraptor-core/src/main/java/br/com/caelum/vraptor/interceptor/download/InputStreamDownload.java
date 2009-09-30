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

/**
 * Handles download by reading from a input stream byte by byte.
 * @author filipesabella
 */
public class InputStreamDownload implements Download {
	private InputStream input;
	private FileDetails details;

	public InputStreamDownload(InputStream input, String contentType, String fileName) {
		this(input, contentType, fileName, false);
	}
	public InputStreamDownload(InputStream input, String contentType, String fileName, boolean doDownload) {
		this.input = input;
		this.details = new FileDetails(contentType, fileName, doDownload);
	}

	public void write(HttpServletResponse response) throws IOException {
		details.write(response);

		OutputStream output = response.getOutputStream();
		int read = 0;
		while((read = input.read()) > -1) {
			output.write(read);
		}
	}
}
