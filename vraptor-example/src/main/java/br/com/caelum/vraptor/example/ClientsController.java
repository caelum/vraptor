package br.com.caelum.vraptor.example;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

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
    public void list() {
        result.include("clients", database.all());
    }

    @Path("/clients/add")
    public void add(final Client client) throws ServletException, IOException {
        validator.checking(new Validations() {
            {
                that(client); shouldBe(notNullValue());
                that(client).getAge(); shouldBe(greaterThan(10));
            }
        });
        database.add(client);
        result.include("client", client);
        result.use(DefaultPageResult.page()).forward("ok");
    }
}
