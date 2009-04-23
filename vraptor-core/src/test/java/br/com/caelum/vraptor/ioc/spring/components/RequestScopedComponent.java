package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * @author Fabio Kung
 */
@RequestScoped
public class RequestScopedComponent implements RequestScopedContract {
    public void operation() {
        System.out.println("operation done");
    }
}
