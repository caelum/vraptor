package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.resource.Resource;

import java.io.File;
import java.util.List;

public interface DirScanner {

	List<Resource> scan(File classes);

}
