package br.com.caelum.vraptor.interceptor.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

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

    @Override
    public void delete() {

    }

    @Override
    public byte[] get() {
        return content;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public InputStream getInputStream()
        throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OutputStream getOutputStream()
        throws IOException {
        return null;
    }

    @Override
    public long getSize() {
        return content == null ? 0 : content.length;
    }

    @Override
    public String getString() {
        return new String(content);
    }

    @Override
    public String getString(String arg0)
        throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public boolean isFormField() {
        return formField;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public void setFieldName(String arg0) {

    }

    @Override
    public void setFormField(boolean arg0) {

    }

    @Override
    public void write(File arg0)
        throws Exception {

    }

}
