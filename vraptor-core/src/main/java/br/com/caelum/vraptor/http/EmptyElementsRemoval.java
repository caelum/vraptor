package br.com.caelum.vraptor.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EmptyElementsRemoval {

    private List<Collection<?>> collections = new ArrayList<Collection<?>>();

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
