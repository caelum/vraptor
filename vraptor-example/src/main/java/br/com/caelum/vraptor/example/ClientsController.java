package br.com.caelum.vraptor.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.jsp.PageResult;

@Resource
public class ClientsController {

    private static final List<Client> clients = new ArrayList<Client>();
    static {
        clients.add(client("Guilherme"));
    }
    private static Client client(String name) {
        Client c = new Client();
        c.setName(name);
        return c;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private final Result result;

    public ClientsController(Result result) {
        this.result = result;
    }

    @Path("/clients")
    public void list() throws ServletException, IOException {
        result.include("clients", clients);
        result.use(PageResult.jsp()).forward("ok");
    }

    @Path("/clients/add")
    public void add(Client c) throws ServletException, IOException {
        result.include("client", c);
        result.use(PageResult.jsp()).forward("ok");
    }
}
