package br.com.caelum.vraptor.example.spring;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Fabio Kung
 */
@Resource
public class DogsController {
    private final Result result;

    public DogsController(Result result) {
        this.result = result;
    }

    @Path("/dogs")
    public void list() throws IOException, ServletException {
        result.include("dogs", Arrays.asList("lulu", "pluto"));
        // TODO argh, better exception handling
        result.use(DefaultPageResult.jsp()).forward("ok");
    }

}
