package br.com.caelum.vraptor.interceptor.multipart;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

@Convert(UploadedFile.class)
public class UploadedFileConverter implements Converter<UploadedFile>{

    public UploadedFile convert(String value, Class<? extends UploadedFile> type) {
        return null;
    }

}
