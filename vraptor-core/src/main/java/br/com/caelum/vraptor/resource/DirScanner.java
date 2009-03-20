package br.com.caelum.vraptor.resource;

import java.io.File;
import java.util.List;

public interface DirScanner {

	List<Resource> scan(File classes);

}
