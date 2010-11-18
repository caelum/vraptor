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
	public void shouldComplainIfThereIsNothingRegistered() {
		Assert.assertEquals("unknown", mimeTypeToFormat.getFormat("unknown"));
	}

	@Test
	public void shouldReturnHtmlWhenRequestingAnyContentType() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("*/*"));
	}

	@Test
	public void shouldReturnHtmlWhenAcceptsIsBlankContentType() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat(""));
	}

	@Test
	public void shouldReturnHtmlWhenRequestingUnknownAsFirstAndAnyContentType() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("unknow, */*"));
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
	public void testNull() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat(null));
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
	public void testPrecendenceInABizzarreMSIE8AcceptHeader() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash,  */*"));
	}

	@Test
	public void testPrecendenceInABizzarreMSIE8AcceptHeaderWithHtml() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, text/html, */*"));
	}

	@Test
	public void testPrecendenceInAComplexAcceptHeaderHtmlShouldPrevailWhenTied2() {
		Assert.assertEquals("html", mimeTypeToFormat.getFormat("text/html, application/json, */*"));
	}

	@Test
	public void testJsonInAComplexAcceptHeaderWithParameters() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json; q=0.7, application/xml; q=0.1, */*"));
	}

	@Test
	public void testXMLInAComplexAcceptHeaderWithParametersNotOrdered() {
		Assert.assertEquals("xml", mimeTypeToFormat.getFormat("application/json; q=0.1, application/xml; q=0.7, */*"));
	}

}
