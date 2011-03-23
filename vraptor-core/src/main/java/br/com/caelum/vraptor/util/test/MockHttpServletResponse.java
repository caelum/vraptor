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

package br.com.caelum.vraptor.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.ioc.Component;

/**
 *
 * A simple mock for HttpServletResponse.
 *
 * This mock keeps a contentType and a content  
 * thereby you could be able to retrieve the content as String.
 *
 * @author Vinícius Oliveira
 */
@Component
public class MockHttpServletResponse implements HttpServletResponse {
	
	private PrintWriter writer;
	private String contentType;
	private ByteArrayOutputStream content =  new ByteArrayOutputStream();
	
	public PrintWriter getWriter() {
		if (this.writer == null) {
			this.writer = new PrintWriter(content);
		}
		return writer;
	}
	
	public String getContentAsString() {
		return this.content.toString();
	}

	public String getContentType() {
		return this.contentType;
	}
	
	public void setContentType(String type) {
		this.contentType = type;
	}
	/**
	 * TODO Not implemented
	 */
	public String getCharacterEncoding() {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public void setCharacterEncoding(String charset) {
	}
	
	/**
	 * TODO Not implemented
	 */
	public void setContentLength(int len) {
	}
	/**
	 * TODO Not implemented
	 */
	public void setBufferSize(int size) {
	}
	
	/**
	 * TODO Not implemented
	 */
	public int getBufferSize() {
		return 0;
	}
	/**
	 * TODO Not implemented
	 */
	public void flushBuffer() throws IOException {
	}
	/**
	 * TODO Not implemented
	 */
	public void resetBuffer() {
	}
	/**
	 * TODO Not implemented
	 */
	public boolean isCommitted() {
		return false;
	}
	/**
	 * TODO Not implemented
	 */
	public void reset() {
	}
	/**
	 * TODO Not implemented
	 */
	public void setLocale(Locale loc) {
	}
	/**
	 * TODO Not implemented
	 */
	public Locale getLocale() {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public void addCookie(Cookie cookie) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public boolean containsHeader(String name) {
		return false;
	}
	/**
	 * TODO Not implemented
	 */
	public String encodeURL(String url) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public String encodeRedirectURL(String url) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public String encodeUrl(String url) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public String encodeRedirectUrl(String url) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public void sendError(int sc, String msg) throws IOException {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void sendError(int sc) throws IOException {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void sendRedirect(String location) throws IOException {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void setDateHeader(String name, long date) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void addDateHeader(String name, long date) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void setHeader(String name, String value) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void addHeader(String name, String value) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void setIntHeader(String name, int value) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void addIntHeader(String name, int value) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void setStatus(int sc) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public void setStatus(int sc, String sm) {
		
	}
	/**
	 * TODO Not implemented
	 */
	public int getStatus() {
		return 0;
	}
	/**
	 * TODO Not implemented
	 */
	public String getHeader(String name) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public Collection<String> getHeaders(String name) {
		return null;
	}
	/**
	 * TODO Not implemented
	 */
	public Collection<String> getHeaderNames() {
		return null;
	}

}