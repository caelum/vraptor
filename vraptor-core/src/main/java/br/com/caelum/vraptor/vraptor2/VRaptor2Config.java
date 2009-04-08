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
public class VRaptor2Config implements Config {

    private static final Logger logger = LoggerFactory.getLogger(VRaptor2Config.class);

    private final List<String> converters = new ArrayList<String>();

    private String viewPattern = "/$component/$logic.$result.jsp";

    public VRaptor2Config(ServletContext context) throws IOException {
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
                line = extract(line, "converter");
                logger.info("Vraptor 2 converter found - remember to migrate to vraptor3 : " + line);
                this.converters.add(line);
            } else if(line.contains("<regex-view-manager>")) {
                line = extract(line, "regex-view-manager");
                logger.info("Vraptor 2 regex-view-manager found - remember to migrate to vraptor3 : " + line);
                this.viewPattern = line;
            }
        }
        fileReader.close();
        reader.close();
    }

    private String extract(String line, String tag) {
        return line.substring(line.indexOf("<" + tag + ">") + tag.length()+2, line.lastIndexOf("</" + tag + ">"));
    }
    
    public String getViewPattern() {
        return viewPattern;
    }

    public List<String> getConverters() {
        return converters;
    }

}
