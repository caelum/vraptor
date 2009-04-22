package br.com.caelum.vraptor.http;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@ApplicationScoped
public class EmptyElementsRemoval {

    private Set<Collection<?>> collections = new HashSet<Collection<?>>();

    public void removeExtraElements() {
        for (Collection<?> collection : collections) {
            for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
                Object object = (Object) iterator.next();
                if(object==null) {
                    iterator.remove();
                }
            }
        }
    }

    public void add(Collection<?> collection) {
        this.collections .add(collection);
    }

}
