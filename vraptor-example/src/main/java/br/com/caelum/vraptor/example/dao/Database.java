package br.com.caelum.vraptor.example.dao;

import java.util.List;

import br.com.caelum.vraptor.example.Client;

public interface Database {

    List<Client> all();

    void add(Client c);

}
