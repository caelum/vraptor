package br.com.caelum.vraptor.vraptor2.outject;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches all fields to export and uses them later to expose the json object.
 * 
 * @author Guilherme Silveira
 */
public class JsonOutjecter implements Outjecter {

    private final HashMap<String, Object> toExport;

    public JsonOutjecter() {
        this.toExport = new HashMap<String, Object>();
    }

    public void include(String name, Object value) {
        toExport.put(name,value);
    }

    public Map<String,Object> contents() {
        return toExport;
    }

}
