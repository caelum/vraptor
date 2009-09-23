package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.ValidationMessage;

@RequestScoped
public class IogiParametersProvider implements ParametersProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(IogiParametersProvider.class);
	private final ParameterNameProvider nameProvider;
	private final HttpServletRequest servletRequest;
	private final Instantiator<Object> instantiator;

	public IogiParametersProvider(ParameterNameProvider provider, HttpServletRequest parameters, Instantiator<Object> instantiator) {
		this.nameProvider = provider;
		this.servletRequest = parameters;
		this.instantiator = instantiator;
		LOGGER.info("IogiParametersProvider is up");
	}
	
	@Override
	public Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {
		Method javaMethod = method.getMethod();
		
		Parameters parameters = parseParameters(servletRequest);
		List<Target<Object>> targets = createTargets(javaMethod);

		List<Object> arguments = instantiateParameters(parameters, targets, errors);
		
		return arguments.toArray();
	}

	private List<Object> instantiateParameters(Parameters parameters, List<Target<Object>> targets, List<Message> errors) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("getParametersFor() called with parameters " + parameters + " and targets " +
					targets + ".");
		
		List<Object> arguments = new ArrayList<Object>();
		for (Target<Object> target : targets) {
			Object newObject = instantiateOrAddError(parameters, errors, target);
			arguments.add(newObject);
		}
		return arguments;
	}

	private Object instantiateOrAddError(Parameters parameters, List<Message> errors, Target<Object> target) {
		Object newObject = null;
		try {
			newObject  = instantiator.instantiate(target, parameters);
		} catch (ConversionError ex) {
			errors.add(new ValidationMessage(ex.getMessage(), target.getName()));
		} catch (Exception e) { // setter threw an exception
			if (e.getClass().isAnnotationPresent(ValidationException.class)) {
				errors.add(new ValidationMessage(e.getLocalizedMessage(), target.getName()));
			} else {
				throw new InvalidParameterException("Exception when trying to instantiate " + target, e);
			}

		}
		return newObject;
	}

	private List<Target<Object>> createTargets(Method javaMethod) {
		List<Target<Object>> targets = new ArrayList<Target<Object>>(); 
		
		Type[] parameterTypes = javaMethod.getGenericParameterTypes();
		String[] parameterNames = nameProvider.parameterNamesFor(javaMethod);
		for (int i = 0; i < methodArity(javaMethod); i++) {
			Target<Object> newTarget = new Target<Object>(parameterTypes[i], parameterNames[i]);
			targets.add(newTarget);
		}
		
		return targets;
	}

	private int methodArity(Method method) {
		return method.getGenericParameterTypes().length;
	}

	private Parameters parseParameters(HttpServletRequest request) {
		List<Parameter> parameterList = new ArrayList<Parameter>();
		
		Enumeration<?> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String parameterName = (String) enumeration.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			for (String value : parameterValues) {
				Parameter newParameter = new Parameter(parameterName, value);
				parameterList.add(newParameter);
			}
		}
		
		return new Parameters(parameterList);
	}

}
