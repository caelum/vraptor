package br.com.caelum.vraptor.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.interceptor.download.FileDownload;

@Resource
public class FileController {

	public File image() {
		return new File(getResource("/filecontroller_test/baby_seal.jpg"));
	}

	public InputStream txt() throws FileNotFoundException {
	 	return FileController.class.getResourceAsStream("/filecontroller_test/test.txt");
	}

	public FileDownload mp3() {
		return new FileDownload(new File(getResource("1-13_13_Ghosts_II.mp3")), "audio/mpeg", "ghosts_13.mp3", true);
	}
	
	private String getResource(String fileName) {
		return FileController.class.getResource("/filecontroller_test/" + fileName).getPath();
	}
}
