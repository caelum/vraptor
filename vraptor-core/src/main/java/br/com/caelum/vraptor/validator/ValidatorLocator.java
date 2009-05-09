/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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

        private final Class type;

        private final ResourceBundle bundle;

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
