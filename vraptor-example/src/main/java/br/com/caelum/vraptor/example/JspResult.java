package br.com.caelum.vraptor.example;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class JspResult {
    
    private final Result result;

    public JspResult(Result result) {
        this.result = result;
    }
    
    public void forward(String result) {
    }

}
