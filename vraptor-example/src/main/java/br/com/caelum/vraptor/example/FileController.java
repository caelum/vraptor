package br.com.caelum.vraptor.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.interceptor.download.FileDownload;

@Resource
public class FileController {

	public File image() {
		return new File("/Users/filipesabella/Desktop/slides_img/integration.jpg");
	}

	public InputStream txt() throws FileNotFoundException {
		return new FileInputStream(new File("/Users/filipesabella/Desktop/temp_ondetrabalhar.txt"));
	}

	public FileDownload mp3() {
		return new FileDownload(new File("/Users/filipesabella/Desktop/slides_img/1-13 13 Ghosts II.mp3"), "audio/mpeg", "ghosts_13.mp3", true);
	}
}
