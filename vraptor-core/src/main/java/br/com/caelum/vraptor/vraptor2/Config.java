package br.com.caelum.vraptor.vraptor2;

import java.util.List;

/**
 * VRaptor2 config file representation.
 * @author Guilherme Silveira
 */
public interface Config {

    public String getViewPattern();

    public String getForwardFor(String key);

    public List<String> getConverters();

    public boolean hasPlugin(String type);

}
