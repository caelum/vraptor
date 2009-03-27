package br.com.caelum.vraptor.example;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class ClientsController {

    private final Result result;

    private static final List<Client> clients = new ArrayList<Client>();
    static {
        clients.add(client("Guilherme"));

    }
    public ClientsController(Result result) {
        this.result = result;
    }

    private static Client client(String name) {
        Client c = new Client();
        c.setName(name);
        return c;
    }

    @Path("/clients")
    public void list() {
        result.include("clients", clients);
        result.use(JSPView.jsp()).forward();
    }
}
