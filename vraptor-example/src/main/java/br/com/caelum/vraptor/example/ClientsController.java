package br.com.caelum.vraptor.example;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.example.dao.Database;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

@Resource
public class ClientsController {

    private final Result result;

    private final Database database;

    private final Validator validator;

    public ClientsController(Result result, Database database, Validator validator) {
        this.result = result;
        this.database = database;
        this.validator = validator;
    }

    @Path("/clients")
    public void list() throws ServletException, IOException {
        result.include("clients", database.all());
        result.use(DefaultPageResult.jsp()).forward("ok");
    }

    @Path("/clients/add")
    public void add(final Client c) throws ServletException, IOException {
        validator.checking(new Validations() {
            {
                that(c.getAge(), is(greaterThan(10)));
            }
        });
        database.add(c);
        result.include("client", c);
        result.use(DefaultPageResult.jsp()).forward("ok");
    }
}
