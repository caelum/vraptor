package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.interceptor.BasicUploadedFileInformation;
import org.vraptor.interceptor.UploadedFileInformation;

import br.com.caelum.vraptor.http.RequestParameters;

public class MultipartItemsProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MultipartItemsProcessor.class);
    private final List<FileItem> items;
    private final HttpServletRequest request;
    private final RequestParameters parameters;

    public MultipartItemsProcessor(List<FileItem> items, HttpServletRequest request, RequestParameters parameters) {
        this.items = items;
        this.request = request;
        this.parameters = parameters;
    }

    public void process() {
        for (FileItem item : items) {
            if (item.isFormField()) {
                parameters.set(item.getFieldName(), new String[] { item.getString() });
                continue;
            }
            if (!item.getName().trim().equals("")) {
                try {
                    File file = File.createTempFile("raptor.", ".upload");
                    file.deleteOnExit();
                    item.write(file);
                    UploadedFileInformation fileInformation = new BasicUploadedFileInformation(file, item.getName(),
                            item.getContentType());
                    request.setAttribute(item.getFieldName(), fileInformation);
                    LOG.info("Uploaded file: " + item.getFieldName() + " with " + fileInformation);
                } catch (Exception e) {
                    LOG.error("Nasty uploaded file " + item.getName(), e);
                }
            } else {
                LOG.info("A file field was empy: " + item.getFieldName());
            }
        }
    }
}