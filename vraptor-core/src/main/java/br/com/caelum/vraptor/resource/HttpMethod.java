
package br.com.caelum.vraptor.resource;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Trace;

public enum HttpMethod {
	// TODO: options?
	GET(Get.class), POST(Post.class), PUT(Put.class), DELETE(Delete.class), TRACE(Trace.class), HEAD(Head.class);

	private static final String METHOD_PARAMETER = "_method";
	private final Class<? extends Annotation> type;

	HttpMethod(Class<? extends Annotation> type) {
		this.type = type;
	}

	public Class<? extends Annotation> getAnnotation() {
		return type;
	}

	public static HttpMethod of(HttpServletRequest request) {
		String methodName = request.getParameter(METHOD_PARAMETER);
        if (methodName == null) {
            methodName = request.getMethod();
        }
        return valueOf(methodName.toUpperCase());
	}
}
