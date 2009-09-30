
package br.com.caelum.vraptor.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.hibernate.validator.ClassValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caches instances of hibernate validator instances.
 *
 * @author Guilherme Silveira
 */
class ValidatorLocator {

    private final Map<Key, ClassValidator<?>> cache = new HashMap<Key, ClassValidator<?>>();

    private static final Logger logger = LoggerFactory.getLogger(ValidatorLocator.class);

    /**
     * Returns the validator for a specific type.
     */
    @SuppressWarnings("unchecked")
    public <T> ClassValidator<T> getValidator(Class<T> type, ResourceBundle bundle) {
        Key key = new Key(type, bundle);
        if (!cache.containsKey(key)) {
            logger.debug("Creating hibernate validator locator for " + type.getName());
            cache.put(key, new ClassValidator<T>(type, bundle));
        }
        return (ClassValidator<T>) cache.get(key);
    }

    class Key {

        private final Class<?> type;

        private final ResourceBundle bundle;

        public Key(Class<?> type, ResourceBundle bundle) {
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
