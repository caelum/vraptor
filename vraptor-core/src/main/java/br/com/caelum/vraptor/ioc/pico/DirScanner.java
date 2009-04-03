package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.util.List;

import br.com.caelum.vraptor.resource.Resource;

public interface DirScanner {

	List<Resource> scan(File classes);

}
