package br.com.caelum.vraptor.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.validator.ClassValidator;

public class ValidatorLocator {

    private Map<Key, ClassValidator<?>> cache = new HashMap<Key, ClassValidator<?>>();

    @SuppressWarnings("unchecked")
    public <T> ClassValidator<T> getValidator(Class<T> type, ResourceBundle bundle) {
        Key key = new Key(type, bundle);
        if (!cache.containsKey(key)) {
            cache.put(key, new ClassValidator<T>(type, bundle));
        }
        return (ClassValidator<T>) cache.get(key);
    }

    class Key {

        private Class type;

        private ResourceBundle bundle;

        public Key(Class type, ResourceBundle bundle) {
            this.type = type;
            this.bundle = bundle;
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key k = (Key) obj;
            return k.type.equals(type) && ((k.bundle == null && bundle == null) || (k.bundle.equals(bundle)));
        }

    }

}
