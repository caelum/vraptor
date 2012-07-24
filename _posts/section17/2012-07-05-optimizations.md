---
title: Optimizations
layout: page
language: en
section: 17
---

<h3>Commons Upload</h3>

If you don't upload files on your application, remove the commons upload jar from your classpath. It avoids the unnecessary load of the upload interceptor, and the request will run faster.

<h3>@Lazy annotation on Interceptors</h3>

If the accepts method from an interceptor doesn't depend on its internal state, you can annotate it with @Lazy:

{% highlight java %}
@Intercepts
@Lazy
public class MyLazyInterceptor implements Interceptor {
    public MyLazyInterceptor(Dependency dependency) {
        this.dependency = dependency;
    }
    public boolean accepts(ResourceMethod method) {
        // depends only on method
        return method.containsAnnotation(Abc.class);
    }
    public void intercepts(...) {
        //...
    }
}
{% endhighlight %}

This way, VRaptor only instantiates the interceptor if the accepts method returns true. In order to do that, VRaptor will create a non-functional instance of your interceptor (null for all dependencies) and invokes the accept method on it, avoiding unnecessary Container calls. Using internal state can (and will) result in NullPointerException.
Do not use @Lazy if the accepts method is trivial (just returns true).
