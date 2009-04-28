package br.com.caelum.vraptor.example.spring;

import br.com.caelum.vraptor.In;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

import java.util.Arrays;

/**
 * @author Fabio Kung
 */
@Resource
public class DogsController {

    @In
    private Result result;

    @Path("/dogs")
    public void list() {
        result.include("dogs", Arrays.asList("lulu", "pluto"));
        result.use(Results.page()).forward("ok");
    }

}
