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
	public void testJsonInAComplexAcceptHeader() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json, text/javascript, */*"));
	}
	
	@Test
	public void testPrecendenceInAComplexAcceptHeader() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json, text/html, */*"));
	}
	
	@Test
	public void testJsonInAComplexAcceptHeaderWithParameters() {
		Assert.assertEquals("json", mimeTypeToFormat.getFormat("application/json; q=0.7, text/javascript; q=0.1, */*"));
	}
}
