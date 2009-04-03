package br.com.caelum.vraptor.http.ognl;

import java.util.List;

public class Mouse{
    private String name;
    
    private List<String> eyeColors;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEyeColors(List<String> eyeColors) {
        this.eyeColors = eyeColors;
    }

    public List<String> getEyeColors() {
        return eyeColors;
    }
}
