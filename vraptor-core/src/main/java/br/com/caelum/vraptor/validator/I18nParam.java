/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import java.util.ResourceBundle;

/**
 * Represents a lazy i18n parameter
 * Use:
 * <code>
 *     I18nMessage message = new I18nMessage("category", "key", new I18nParam("lazy.param"));
 * </code>
 * @author Lucas Cavalcanti
 * @since 3.4.0
 */
public class I18nParam {
    private final String key;

    public I18nParam(String key) {
        this.key = key;
    }

    public String getKey(ResourceBundle bundle) {
        return bundle.getString(key);
    }

    @Override
    public String toString() {
        return String.format("i18n(%s)", key);
    }
}
