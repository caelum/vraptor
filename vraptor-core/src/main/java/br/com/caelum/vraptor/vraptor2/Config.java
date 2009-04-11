package br.com.caelum.vraptor.vraptor2;

import java.util.List;

public interface Config {

    public String getViewPattern();

    public String getForwardFor(String key);

    public List<String> getConverters();

}
