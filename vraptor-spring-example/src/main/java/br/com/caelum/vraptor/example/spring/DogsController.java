package br.com.caelum.vraptor.example.spring;

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

    @Autowired
    private DogsRepository repository;

    @Path("/dogs")
    public void list() {
        result.include("dogs", repository.getDogs());
    }

}
