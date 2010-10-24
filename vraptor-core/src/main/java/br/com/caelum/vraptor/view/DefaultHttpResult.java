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
/**
 *
 */
package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

/**
 * Implementation that delegates to HttpServletResponse
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 */
public class DefaultHttpResult implements HttpResult {

	private final HttpServletResponse response;
	private final Status status;

	public DefaultHttpResult(HttpServletResponse response, Status status) {
		this.response = response;
		this.status = status;
	}

	public HttpResult addDateHeader(String name, long date) {
		response.addDateHeader(name, date);
		return this;
	}

	public HttpResult addHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}

	public HttpResult addIntHeader(String name, int value) {
		response.addIntHeader(name, value);
		return this;
	}

	public void sendError(int statusCode) {
		try {
			response.sendError(statusCode);
		} catch (IOException e) {
			throw new ResultException("Error while setting status code", e);
		}

	}
	public void sendError(int statusCode, String message) {
		try {
			response.sendError(statusCode, message);
		} catch (IOException e) {
			throw new ResultException("Error while setting status code", e);
		}

	}

	public void setStatusCode(int statusCode) {
		response.setStatus(statusCode);
	}


	public void movedPermanentlyTo(String uri) {
		this.status.movedPermanentlyTo(uri);
	}

	public <T> T movedPermanentlyTo(final Class<T> controller) {
		return this.status.movedPermanentlyTo(controller);
	}

	public HttpResult body(String body) {
		try {
			response.getWriter().print(body);
		} catch (IOException e) {
			throw new ResultException("Couldn't write to response body", e);
		}
		return this;
	}

	public HttpResult body(InputStream body) {
		try {
		    ByteStreams.copy(body, response.getOutputStream());
		} catch (IOException e) {
			throw new ResultException("Couldn't write to response body", e);
		}
		return this;
	}

	public HttpResult body(Reader body) {
		try {
		    CharStreams.copy(body, response.getWriter());
		} catch (IOException e) {
			throw new ResultException("Couldn't write to response body", e);
		}
		return this;
	}
}
