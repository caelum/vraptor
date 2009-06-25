package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * An interceptor which handles multipart requests.<br>
 * Provided parameters are injected through RequestParameters.set and uploaded
 * files are made available through
 * 
 * @author Guilherme Silveira
 */
@RequestScoped
public class MultipartInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory
			.getLogger(MultipartInterceptor.class);

	private final long sizeLimit;

	private final HttpServletRequest request;

	private final MutableRequest parameters;

	private final MultipartConfig config;

	public MultipartInterceptor(HttpServletRequest request,
			MutableRequest parameters, MultipartConfig config)
			throws IOException {
		this.request = request;
		this.parameters = parameters;
		this.sizeLimit = config.getSizeLimit();
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) throws InterceptionException {
		// TODO ugly, just for now until next release
		if (!accepts(method)) {
			stack.next(method, instance);
			return;
		}

		logger.debug("Trying to parse multipart request.");

		File temporaryDirectory = config.getDirectory();
		DiskFileItemFactory factory = createFactoryForDiskBasedFileItems(temporaryDirectory);

		ServletFileUpload fileUploadHandler = new ServletFileUpload(factory);

		fileUploadHandler.setSizeMax(sizeLimit);

		List<FileItem> fileItems;
		try {
			fileItems = fileUploadHandler.parseRequest(request);
		} catch (FileUploadException e) {
			logger
					.warn(
							"There was some problem parsing this multipart request, "
									+ "or someone is not sending a RFC1867 compatible multipart request.",
							e);
			stack.next(method, instance);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger
					.debug("Found ["
							+ fileItems.size()
							+ "] attributes in the multipart form submission. Parsing them.");
		}

		new MultipartItemsProcessor(fileItems, request, parameters).process();

		stack.next(method, instance);

		// TODO should we delete the temporary files afterwards or onExit as
		// done by
		// now?maybe also a config in .properties

	}

	private static DiskFileItemFactory createFactoryForDiskBasedFileItems(
			File temporaryDirectory) {
		DiskFileItemFactory factory = new DiskFileItemFactory(4096 * 16,
				temporaryDirectory);
		logger.debug("Using repository [" + factory.getRepository()
				+ "] for file upload");
		return factory;
	}

	public boolean accepts(ResourceMethod method) {
		return FileUploadBase.isMultipartContent(new ServletRequestContext(
				request));
	}

}
