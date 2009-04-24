package br.com.caelum.vraptor.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.example.Client;

@Component
public class ClientsController {

    private static final List<Client> clients = new ArrayList<Client>();
    
    public List<Client> getClients() {
    	return clients;
    }
    
    public void list() throws ServletException, IOException {
        System.out.println("Listing");
    }

    public void add(Client client) throws ServletException, IOException {
        clients.add(client);
    }
}
