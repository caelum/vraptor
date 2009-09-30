/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

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
