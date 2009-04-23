package br.com.caelum.vraptor.example.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.example.Client;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class InMemoryDatabase implements Database {

    private final List<Client> clients = new ArrayList<Client>();
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);

    public void add(Client c) {
        clients.add(c);
    }

    public List<Client> all() {
        return clients;
    }
    
    @PostConstruct
    public void startup() {
        logger.info("Starting up the database... configuration should be done here");
    }

}
