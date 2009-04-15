package br.com.caelum.vraptor.test;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Fabio Kung
 */
class IteratorToEnumerationAdapter implements Enumeration {
    private Iterator iterator;

    IteratorToEnumerationAdapter(Iterator iterator) {
        this.iterator = iterator;
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Object nextElement() {
        return iterator.next();
    }
}
