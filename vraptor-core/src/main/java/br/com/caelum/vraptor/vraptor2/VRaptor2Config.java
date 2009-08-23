package br.com.caelum.vraptor.vraptor2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Loads some basic config from vraptor.xml.<br>
 * Only this basic configuration is automatically supported. This way we can
 * support vraptor2 most important features. We do not really parse the xml file
 * as it would be a little bit too much for the information that we require.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class VRaptor2Config implements Config {

    private static final Logger logger = LoggerFactory.getLogger(VRaptor2Config.class);

    private final List<String> converters = new ArrayList<String>();
    private final List<String> plugins = new ArrayList<String>();

    private final Map<String, String> results = new HashMap<String, String>();

    private String viewPattern = "/$component/$logic.$result.jsp";

    public VRaptor2Config(ServletContext context) throws IOException, ConfigException {
        File xml = new File(context.getRealPath("/WEB-INF/classes/vraptor.xml"));
        parseVRaptor(xml);
        File views = new File(context.getRealPath("/WEB-INF/classes/views.properties"));
        parseViews(views);
    }

    interface LineListener {
        void content(String line) throws ConfigException;
    }

    private void parseVRaptor(File file) throws IOException, ConfigException {
        if (!file.exists()) {
            return;
        }
        parse(file, new LineListener() {
            public void content(String line) throws ConfigException {
                if (line.contains("<converter>")) {
                    line = extract(line, "converter");
                    logger.info("Vraptor 2 converter found - remember to migrate to vraptor3 : " + line);
                    converters.add(line);
                } else if (line.contains("<regex-view-manager>")) {
                    line = extract(line, "regex-view-manager");
                    logger.info("Vraptor 2 regex-view-manager found - remember to migrate to vraptor3 : " + line);
                    viewPattern = line;
                } else if (line.contains("<plugin>")) {
                    line = extract(line, "plugin");
                    logger.info("Vraptor 2 plugin found - remember to migrate to vraptor3 : " + line);
                    plugins.add(line);
                }
            }
        });
    }

    private void parse(File file, LineListener listener) throws IOException, ConfigException {
        if (!file.exists()) {
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

    private void parseViews(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        logger.warn("Vraptor 2 views.properties found - remember to migrate to vraptor3");
        Properties p = new Properties();
        FileInputStream stream = new FileInputStream(file);
        p.load(stream);
        for (Object key : p.keySet()) {
            results.put((String) key, p.getProperty((String) key));
            logger.debug("Mapped: " + key + " to " + p.getProperty((String) key));
        }
        logger.warn("[MIGRATION] You still have to migrate " + p.size() + " forwards found at " + file.getAbsolutePath());
        stream.close();
    }

    private String extract(String line, String tag) throws ConfigException {
        int lastPosition = line.lastIndexOf("</" + tag + ">");
        if (lastPosition == -1) {
            throw new ConfigException("Valid vraptor.xml but not supported by vraptor3. You should put all " + tag
                    + " tags in separate lines (one line for an opening, content and closing tag)");
        }
        return line.substring(line.indexOf("<" + tag + ">") + tag.length() + 2, lastPosition);
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

    public boolean hasPlugin(String type) {
        return this.plugins.contains(type);
    }

}
