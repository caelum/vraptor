package br.com.caelum.vraptor.example.spring;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Fabio Kung
 */
@Resource
public class DogsController {

    @Autowired
    private Result result;

    @Path("/dogs")
    public void list() {
        result.include("dogs", Arrays.asList("lulu", "pluto"));
    }

}
