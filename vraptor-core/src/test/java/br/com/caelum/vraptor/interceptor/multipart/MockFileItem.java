package br.com.caelum.vraptor.interceptor.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

public class MockFileItem
    implements FileItem {

    private static final long serialVersionUID = 5566658661323774136L;

    private String fieldName;
    private String contentType;
    private String name;
    private byte[] content;
    private boolean formField;

    public MockFileItem(String fieldName, String content) {
        this.fieldName = fieldName;
        this.content = content.getBytes();
        this.formField = true;
    }

    public MockFileItem(String fieldName, String name, byte[] content) {
        this.fieldName = fieldName;
        this.contentType = "application/octet-stream";
        this.name = name;
        this.content = content;
    }
    
    public MockFileItem(String fieldName, String contentType, String name, byte[] content) {
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.name = name;
        this.content = content;
    }

    public void delete() {

    }

    public byte[] get() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    public String getName() {
        return name;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    public long getSize() {
        return content == null ? 0 : content.length;
    }

    public String getString() {
        return new String(content);
    }

    public String getString(String charsetName) throws UnsupportedEncodingException {
    	try {
    		return new String(content, Charset.forName(charsetName));
    	}catch (Exception e) {
			throw new UnsupportedEncodingException();
		}
    }

    public boolean isFormField() {
        return formField;
    }

    public boolean isInMemory() {
        return false;
    }

    public void setFieldName(String arg0) {

    }

    public void setFormField(boolean arg0) {

    }

    public void write(File arg0)
        throws Exception {

    }
    
    public FileItemHeaders getHeaders() {
        return null;
    }
    
    public void setHeaders(FileItemHeaders arg0) {
        
    }
}
