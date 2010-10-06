package br.com.caelum.vraptor.interceptor.multipart;

import static br.com.caelum.vraptor.interceptor.multipart.Servlet3MultipartInterceptor.CONTENT_DISPOSITION_KEY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.Part;

import com.google.common.collect.Maps;

/**
 * A Vraptor implementation of {@link Part}.
 *
 * @author Otávio Scherer Garcia
 */
public class VraptorPart
    implements Part {

    private String name;
    private String contentType;
    private byte[] content;
    private Map<String, Collection<String>> headers = Maps.newHashMap();

    public VraptorPart(String name, String contentType, String filename, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
        headers.put(CONTENT_DISPOSITION_KEY, Arrays.asList("form-data; name=\"" + filename + "\""));
    }

    public VraptorPart(String name, String content) {
        this.name = name;
        this.content = content.getBytes();
    }

    public String getContentType() {
        return contentType;
    }

    public String getHeader(String name) {
        Collection<String> values = getHeaders(name);
        return values.isEmpty() ? null : values.iterator().next();
    }

    public Collection<String> getHeaderNames() {
        return Collections.unmodifiableCollection(headers.keySet());
    }

    public Collection<String> getHeaders(String name) {
        Collection<String> values = headers.get(name);
        if (values == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(values);
    }

    public InputStream getInputStream()
        throws IOException {
        return new ByteArrayInputStream(content);
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return content == null ? 0 : content.length;
    }

    public void write(String filename)
        throws IOException {
        throw new UnsupportedOperationException();
    }

    public void delete()
        throws IOException {
        throw new UnsupportedOperationException();
    }

}