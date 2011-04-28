package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.annotation.ValidationException;

import com.google.common.collect.Maps;

@RequestScoped
public class OgnlFacade {

	private static final Logger logger = LoggerFactory.getLogger(OgnlFacade.class);

	private final Converters converters;
	private final EmptyElementsRemoval removal;
	private final Map<Object, OgnlContext> contexts = Maps.newHashMap();

	public OgnlFacade(Converters converters, EmptyElementsRemoval removal) {
		this.converters = converters;
		this.removal = removal;
		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor(converters));
		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

	public Object createOgnlContextFor(Type type, Object root, ResourceBundle bundle) {

		OgnlContext context = createOgnlContext(root);

		context.setTraceEvaluations(true);
		context.put("rootType", type);
		context.put("removal", removal);
		context.put("nullHandler", nullHandler());

		Ognl.setTypeConverter(context, new VRaptorConvertersAdapter(converters, bundle));

		contexts.put(context.getRoot(), context);
		return context.getRoot();
	}

	protected OgnlContext createOgnlContext(Object root) {
		return (OgnlContext) Ognl.createDefaultContext(root);
	}

	protected NullHandler nullHandler() {
		return new GenericNullHandler(removal);
	}

	public Object setValue(Object root, String key, String[] values) {
		try {
			OgnlContext ctx = contexts.get(root);
			Ognl.setValue(key, ctx, ctx.getRoot(), values.length == 1 ? values[0] : values);
			contexts.put(ctx.getRoot(), ctx);
			return ctx.getRoot();
		} catch (MethodFailedException e) { // setter threw an exception

			Throwable cause = e.getCause();
			if (cause.getClass().isAnnotationPresent(ValidationException.class)) {
				throw new ConversionError(cause.getLocalizedMessage());
			} else {
				throw new InvalidParameterException("unable to parse expression '" + key + "'", e);
			}

		} catch (NoSuchPropertyException ex) {
			// TODO optimization: be able to ignore or not
			logger.debug("cant find property for expression {} ignoring", key);
			logger.trace("Reason:", ex);
		} catch (OgnlException e) {
			// TODO it fails when parameter name is not a valid java
			// identifier... ignoring by now
			logger.debug("unable to parse expression '{}'. Ignoring.", key);
			logger.trace("Reason:", e);
		}
		return root;
	}



}
