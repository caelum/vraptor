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
package br.com.caelum.vraptor.vraptor2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.vraptor.i18n.FixedMessage;
import org.vraptor.i18n.Message;
import org.vraptor.plugin.hibernate.HibernateLogicMethod;
import org.vraptor.plugin.hibernate.Validate;
import org.vraptor.plugin.hibernate.ValidatorLocator;
import org.vraptor.reflection.GettingException;
import org.vraptor.validator.BasicValidationErrors;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.FallbackResourceBundle;

/**
 * Support to vraptor2 hibernate validator plugin.<br>
 * Limited support to parameters only.
 *
 * @author Guilherme Silveira
 */
public class HibernateValidatorPluginInterceptor implements Interceptor {

    private final ValidationErrors errors;
    private final ParameterNameProvider provider;
    private final HttpServletRequest request;
    // sucks, cannot find a way to register without checking for hibernate
    // existence first...
    private final static ValidatorLocator locator = new ValidatorLocator();
    private final MethodInfo parameters;
    private final Localization localization;

    public HibernateValidatorPluginInterceptor(ValidationErrors errors, ParameterNameProvider provider,
            HttpServletRequest request, MethodInfo parameters, Localization localization) {
        this.errors = errors;
        this.provider = provider;
        this.request = request;
        this.parameters = parameters;
        this.localization = localization;
    }

    public boolean accepts(ResourceMethod method) {
        return method.getMethod().isAnnotationPresent(Validate.class);
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Validate validate = method.getMethod().getAnnotation(Validate.class);
        if (validate != null) {
            ResourceBundle validatorBundle = ResourceBundle.getBundle(
                    "org.hibernate.validator.resources.DefaultValidatorMessages", request.getLocale());
            FallbackResourceBundle bundle = new FallbackResourceBundle(localization.getBundle(), validatorBundle);
            String[] names = provider.parameterNamesFor(method.getMethod());
            for (String path : validate.params()) {
                try {
                    Object[] paramValues = parameters.getParameters();
                    Object object = paramFor(names, path, paramValues);
                    BasicValidationErrors newErrors = new BasicValidationErrors();
                    HibernateLogicMethod.validateParam(locator, request, bundle, newErrors, object, path);
                    for (org.vraptor.i18n.ValidationMessage msg : newErrors) {
                        if (msg instanceof FixedMessage) {
                            FixedMessage m = (FixedMessage) msg;
                            String content = bundle.getString(m.getKey());
                            errors.add(new FixedMessage(msg.getPath(), content, msg.getCategory()));
                        } else if (msg instanceof Message) {
                            Message m = (Message) msg;
                            String content = bundle.getString(m.getKey());
                            content = MessageFormat.format(content, new Object[]{m.getParameters()});
                            errors.add(new FixedMessage(msg.getPath(), content, msg.getCategory()));
                        } else {
                            throw new IllegalArgumentException("Unsupported validation message type: " + msg.getClass().getName());
                        }
                    }
                } catch (GettingException e) {
                    throw new InterceptionException(
                            "Unable to validate objects due to an exception during validation.", e);
                }
            }
        }
        stack.next(method, resourceInstance);
    }

    private Object paramFor(String[] names, String path, Object[] values) throws InterceptionException {
        String param = path;
        if (param.indexOf(".") != -1) {
            param = param.substring(param.indexOf("."));
        }
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(param) || names[i].equals(Info.capitalize(param))) {
                return values[i];
            }
        }
        throw new InterceptionException("Unable to find param for hibernate validator: '" + path + "'");
    }

}
