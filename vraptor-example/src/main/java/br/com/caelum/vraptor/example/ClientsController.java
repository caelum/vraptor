package br.com.caelum.vraptor.example;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.jsp.DefaultPageResult;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Resource
public class ClientsController {

    private static final List<Client> clients = new ArrayList<Client>();

    private final Result result;

    public ClientsController(Result result) {
        this.result = result;
    }

    @Path("/clients")
    public void list() throws ServletException, IOException {
        result.include("clients", clients);
        result.use(DefaultPageResult.jsp()).forward("ok");
    }

    @Path("/clients/add")
    public void add(Client c) throws ServletException, IOException {
        validator.checking(clientValidator(c));
        clients.add(c);
        result.include("client", c);
        result.use(DefaultPageResult.jsp()).forward("ok");
    }
}
