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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.ResourceBundleDescription;

import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.util.FallbackResourceBundle;

/**
 * Hamcrest based validation support.
 *
 * Uses:
 * validator.checking(new Validations() {{
 * 		if (that(user, is(notNullValue())) { // that will return if the match was successful
 *	 		that(user.getAge() > 17, "user.age", "user.is.underage"); // boolean assertions
 * 			that(user.getRoles(), hasItem("ADMIN"), "user.roles", "user.is.not.admin"); // hamcrest assertions
 * 		}
 * }});
 *
 * You can use any hamcrest Matcher. Some helpful matchers can be found on org.hamcrest.Matchers.
 *
 * @author Guilherme Silveira
 * @author Lucas Cavalcanti
 */
public class Validations {

    private final List<Message> errors = new ArrayList<Message>();
	private ResourceBundle bundle;

    public Validations(ResourceBundle bundle) {
		this.bundle = bundle;
	}

    public Validations() {
    	this.bundle = new SafeResourceBundle(ResourceBundle.getBundle("messages"), true);
    }

    public <T> boolean that(T id, Matcher<? super T> matcher) {
        return that(id, matcher, "", null);
    }

    public <T> boolean that(T id, Matcher<? super T> matcher, String category) {
        return that(id, matcher, category, null);
    }

    public <T> boolean that(T actual, Matcher<? super T> matcher, String category, String reason, Object... messageParameters) {
        if (!matcher.matches(actual)) {
        	if (reason != null) {
        		errors.add(new I18nMessage(category, reason, messageParameters));
            } else {
                Description description = new ResourceBundleDescription();
                description.appendDescriptionOf(matcher);
                errors.add(new I18nMessage(description.toString(), category));
            }
            return false;
        }
        return true;
    }

    public boolean that(boolean assertion, String category, String reason, Object... messageParameters) {
        if (!assertion) {
        	errors.add(new I18nMessage(category, reason, messageParameters));
        }
        return assertion;
    }

    protected String i18n(String key) {
    	return bundle.getString(key);
    }

    /**
     * Returns the list of errors.
     */
    public List<Message> getErrors() {
    	for (Message message : errors) {
			if (message instanceof I18nMessage) {
				((I18nMessage) message).setBundle(bundle);
			}
		}
        return errors;
    }

    /**
     * Returns the list of errors, using given resource bundle.
     */
    public List<Message> getErrors(ResourceBundle bundle) {
    	if (isDefaultBundle(this.bundle)) {
    		this.bundle = new SafeResourceBundle(bundle);
    	} else {
    		this.bundle = new FallbackResourceBundle(this.bundle, bundle);
    	}
    	return getErrors();
    }

	private boolean isDefaultBundle(ResourceBundle bundle) {
		return bundle instanceof SafeResourceBundle && ((SafeResourceBundle) bundle).isDefault();
	}

    /**
     * Adds a list of errors to the error list.
     * @return
     */
    public Validations and(List<Message> errors) {
        this.errors.addAll(errors);
        return this;
    }

    /**
     * Adds a single error message to the error list.
     */
    public Validations and(Message error) {
        this.errors.add(error);
        return this;
    }

}
