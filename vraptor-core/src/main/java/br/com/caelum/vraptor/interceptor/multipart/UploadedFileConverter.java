package br.com.caelum.vraptor.interceptor.multipart;


import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

/**
 * A converter capable of setting UploadedFiles based on files parsed by the
 * multipart interceptor.
 *
 * @author Guilherme Silveira
 */
@Convert(UploadedFile.class)
public class UploadedFileConverter implements Converter<UploadedFile> {

    private final HttpServletRequest request;

    public UploadedFileConverter(HttpServletRequest request) {
        this.request = request;
    }

    public UploadedFile convert(String value, Class<? extends UploadedFile> type, ResourceBundle bundle) {
        return type.cast(request.getAttribute(value));
    }

}
