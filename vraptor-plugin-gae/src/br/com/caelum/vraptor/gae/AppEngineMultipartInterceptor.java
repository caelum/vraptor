package br.com.caelum.vraptor.gae;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.gmr.web.multipart.GFileItemFactory;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class AppEngineMultipartInterceptor extends MultipartInterceptor {

	public AppEngineMultipartInterceptor(HttpServletRequest request, MutableRequest parameters, MultipartConfig config)
			throws IOException {
		super(request, parameters, config);
	}

	@Override
	protected FileItemFactory createFactoryForDiskBasedFileItems(File temporaryDirectory) {
		return new GFileItemFactory();
	}
}
