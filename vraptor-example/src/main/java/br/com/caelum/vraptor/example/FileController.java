package br.com.caelum.vraptor.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.interceptor.download.FileDownload;

@Resource
public class FileController {

	public File image() {
		return new File(FileController.class.getResource("/filecontroller_test/baby_seal.jpg").getPath());
	}

	public InputStream txt() throws FileNotFoundException {
	 	return FileController.class.getResourceAsStream("/filecontroller_test/test.txt");
	}

	public FileDownload mp3() {
		return new FileDownload(new File(FileController.class.getResource("/filecontroller_test/1-13_13_Ghosts_II.mp3").getPath()), "audio/mpeg", "ghosts_13.mp3", true);
	}
}
