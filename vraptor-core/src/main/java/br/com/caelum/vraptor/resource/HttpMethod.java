package br.com.caelum.vraptor.resource;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Head;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Trace;

public enum HttpMethod {
    GET(Get.class), POST(Post.class), PUT(Put.class), DELETE(Delete.class), TRACE(Trace.class), HEAD(Head.class);

    private final Class<? extends Annotation> type;

    HttpMethod(Class<? extends Annotation> type) {
        this.type = type;
    }

    public Class<? extends Annotation> getAnnotation() {
        return type;
    }
}
