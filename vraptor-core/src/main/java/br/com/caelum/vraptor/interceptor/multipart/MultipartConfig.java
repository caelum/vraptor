
package br.com.caelum.vraptor.interceptor.multipart;

import java.io.File;

/**
 * Basic multi part interceptor configuration.
 * @author guilherme silveira
 */
public interface MultipartConfig {

	long getSizeLimit();

	File getDirectory();

}
