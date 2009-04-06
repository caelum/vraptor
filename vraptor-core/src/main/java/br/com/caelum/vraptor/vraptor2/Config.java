package br.com.caelum.vraptor.vraptor2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads some basic config from vraptor.xml. Only this basic configuration is
 * automatically supported.
 * 
 * @author Guilherme Silveira
 */
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private final List<String> converters = new ArrayList<String>();

    public Config(ServletContext context) throws IOException {
        File file = new File(context.getRealPath("/WEB-INF/classes/vraptor.xml"));
        if (file.exists()) {
            loadFile(file);
        }
    }

    private void loadFile(File file) throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            if (line.contains("<converter>")) {
                line = line.substring(line.indexOf("<converter>") + 11, line.lastIndexOf("</converter>"));
                logger.info("Vraptor 2 converter found - remember to migrate to vraptor3 : " + line);
                this.converters.add(line);
            }
        }
        fileReader.close();
        reader.close();
    }

    public List<String> getConverters() {
        return converters;
    }

}
