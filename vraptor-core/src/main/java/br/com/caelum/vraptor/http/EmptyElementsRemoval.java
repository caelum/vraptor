package br.com.caelum.vraptor.http;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class EmptyElementsRemoval {

    private Set<Collection<?>> collections = new HashSet<Collection<?>>();
    private Map<SetValue, Object> arrays = new HashMap<SetValue, Object>();
    
    class SetValue {
        Method method;
        Object instance;
        public SetValue(Object instance, Method setter) {
            this.instance= instance;
            this.method = setter;
        }
        void set(Object newValue) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            method.invoke(instance, newValue);
            arrays.put(this, newValue);
        }
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof SetValue)) {
                return false;
            }
            SetValue other = (SetValue) obj;
            return method.equals(other.method) && instance.equals(other.instance);
        }
        @Override
        public int hashCode() {
            return method.hashCode();
        }
    }

    public void removeExtraElements() {
        for (Collection<?> collection : collections) {
            for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
                Object object = (Object) iterator.next();
                if (object == null) {
                    iterator.remove();
                }
            }
        }
        for (SetValue setter : arrays.keySet()) {
            Object array = arrays.get(setter);
            int length = Array.getLength(array);
            int total = length;
            for (int i = 0; i < length; i++) {
                if (Array.get(array, i) == null) {
                    total--;
                }
            }
            Object newArray = Array.newInstance(array.getClass().getComponentType(), total);
            int actual = 0;
            for (int i = 0; i < length; i++) {
                Object value = Array.get(array, i);
                if (value != null) {
                    Array.set(newArray, actual++, value);
                }
            }
            try {
                setter.set(newArray);
            } catch (IllegalArgumentException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e) {
                // TODO better
                throw new IllegalArgumentException(e);
            }
        }
    }

    public void add(Collection<?> collection) {
        this.collections.add(collection);
    }

    public void add(Object array, Method setter, Object instance) {
        this.arrays.put(new SetValue(instance, setter), array);
    }

}
