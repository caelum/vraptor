
package br.com.caelum.vraptor;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * An enumeration backed by an itearator.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class IteratorToEnumerationAdapter<T> implements Enumeration<T> {

    private final Iterator<T> iterator;

    public IteratorToEnumerationAdapter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public T nextElement() {
        return iterator.next();
    }

}
