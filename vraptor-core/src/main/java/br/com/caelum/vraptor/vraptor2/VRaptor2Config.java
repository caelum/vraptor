package br.com.caelum.vraptor.vraptor2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    private final Map<String, String> results = new HashMap<String,String>();

    private String viewPattern = "/$component/$logic.$result.jsp";

    public VRaptor2Config(ServletContext context) throws IOException {
        File xml = new File(context.getRealPath("/WEB-INF/classes/vraptor.xml"));
        parseVRaptor(xml);
        File views = new File(context.getRealPath("/WEB-INF/classes/views.properties"));
        parseViews(views);
    }
    interface LineListener{
        void content(String line);
    }

    private void parseVRaptor(File file) throws FileNotFoundException, IOException {
        parse(file, new LineListener() {
            public void content(String line) {
                if (line.contains("<converter>")) {
                    line = extract(line, "converter");
                    logger.info("Vraptor 2 converter found - remember to migrate to vraptor3 : " + line);
                    converters.add(line);
                } else if(line.contains("<regex-view-manager>")) {
                    line = extract(line, "regex-view-manager");
                    logger.info("Vraptor 2 regex-view-manager found - remember to migrate to vraptor3 : " + line);
                    viewPattern = line;
                }
            }
        });
    }

    private void parse(File file, LineListener listener) throws IOException {
        if(!file.exists()) {
            return;
        }
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            listener.content(line);
        }
        fileReader.close();
        reader.close();
    }

    private void parseViews(File file) throws FileNotFoundException, IOException {
        if(!file.exists()) {
            return;
        }
        logger.warn("Vraptor 2 views.properties found - remember to migrate to vraptor3");
        Properties p = new Properties();
        FileInputStream stream = new FileInputStream(file);
        p.load(stream);
        for(Object key : p.keySet()) {
            results.put((String) key, p.getProperty((String) key));
        }
        stream.close();
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

    public String getForwardFor(String key) {
        return this.results.get(key);
    }

}
