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
package br.com.caelum.vraptor.view;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class DefaultAcceptHeaderToFormatTest {

	private AcceptHeaderToFormat mimeTypeToFormat;

	@Before
	public void setup() {
		mimeTypeToFormat = new DefaultAcceptHeaderToFormat();
	}

	@Test
	public void testHtmlDefault() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("invalid"));
	}

	@Test
	public void testHtml() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("text/html"));
	}

	@Test
	public void testJson() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json"));
	}


	@Test
	public void testJsonWithQualifier() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json; q=0.4"));
	}

	@Test
	public void testAnything() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("*/*"));
	}

	@Test(expected = NullPointerException.class)
	public void testNull() {
		mimeTypeToFormat.getFormat(null);
	}

	@Test
	public void testJsonInAComplexAcceptHeader() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json, text/javascript, */*"));
	}

	@Test
	public void testPrecendenceInAComplexAcceptHeaderHtmlShouldPrevailWhenTied() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("application/json, text/html, */*"));
	}

	@Test
	public void testJsonInAComplexAcceptHeaderWithParameters() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json; q=0.7, text/javascript; q=0.1, */*"));
	}

	@Test
	public void testJsonInAComplexAcceptHeaderWithParametersNorOrdered() {
		Assert.assertEquals("javascript", mimeTypeToFormat.getFormat("application/json; q=0.1, text/javascript; q=0.7, */*"));
	}

}
