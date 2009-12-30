package br.com.caelum.vraptor.blank.infra;

import java.io.File;

import br.com.caelum.vraptor.interceptor.multipart.MultipartConfig;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class AppEngineMultipartConfig implements MultipartConfig {

	public File getDirectory() {
		return null;
	}

	public long getSizeLimit() {
		return Integer.MAX_VALUE;
	}

}
