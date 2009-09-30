
package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * 
 * TODO: should expose not a directory, but a way to define memory or file usage
 * (commons upload has already a common interface to it).
 * 
 * @author Paulo Silveira
 * 
 */
@ApplicationScoped
public class DefaultMultipartConfig implements MultipartConfig {

	private final Logger logger = LoggerFactory.getLogger(DefaultMultipartConfig.class);

	public long getSizeLimit() {
		return 2 * 1024 * 1024;
	}

	public File getDirectory() {
		try {
			File tempFile = File.createTempFile("raptor.", ".upload");
			tempFile.delete();
			return tempFile.getParentFile();
		} catch (IOException e) {
			logger.warn("Unable to find temp directory", e);
			File tmp = new File(".tmp-multipart-upload");
			tmp.mkdirs();
			return tmp;
		}
	}

}
