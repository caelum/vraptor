package br.com.caelum.vraptor.example;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.example.dao.Database;
import br.com.caelum.vraptor.example.dao.InMemoryDatabase;
import br.com.caelum.vraptor.ioc.pico.PicoProvider;

public class CustomProvider extends PicoProvider{
    
    @Override
    protected void registerComponents(ComponentRegistry container) {
        super.registerComponents(container);
        container.register(Database.class, InMemoryDatabase.class);
    }

}
