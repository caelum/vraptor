package br.com.caelum.vraptor.http.ognl;

import java.util.List;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import br.com.caelum.vraptor.core.Converters;

public final class AbstractOgnlTestSupport {

	public static void configOgnl(Converters converters) throws OgnlException {
		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());

		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor(converters));

		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

}