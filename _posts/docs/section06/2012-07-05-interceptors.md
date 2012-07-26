---
title: Interceptors
layout: page
language: en
section: 6
category: docs
---

<h3>Why intercept?</h3>

Interceptors are implemented in order to execute tasks before and/or after the execution of a business logic, being data validation, database connection and transaction control, logging and data cryptography/compression the most common use cases.

<h3>How to intercept?</h3>

In VRaptor 3 we adopted an approach in which the interceptor defines who will be intercepted. This is closer to the intercepting style used in systems based on AOP (Aspect Oriented Programming) than the one that was implemented in VRaptor's previous version.
Therefore, to intercept a request, just implement the <strong>Interceptor</strong> interface and annotate the class with <strong>@Intercepts</strong>.
Just like any other component, you can specify the interceptor's scope using the scope annotations.

{% highlight java %}
public interface Interceptor {

    void intercept(InterceptorStack stack, ResourceMethod method, 
                    Object resourceInstance) throws InterceptionException;

    boolean accepts(ResourceMethod method);

}
{% endhighlight %}

<h3>Simple example</h3>

The following class shows an example of how to intercept all requests using session scope and simply print the invocation to default output.
Remember that the interceptor is a component just like any other, so it can receive its dependencies in the constructor through Dependency Injection.

{% highlight java %}
@Intercepts
@SessionScoped
public class Log implements Interceptor {

    private final HttpServletRequest request;

    public Log(HttpServletRequest request) {
        this.request = request;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, 
                        Object resourceInstance) throws InterceptionException {
        System.out.println("Intercepting " + request.getRequestURI());
        stack.next(method, resourceInstance);
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
{% endhighlight %}

<h3>Example using Hibernate</h3>

Probably one of the most common uses of an Interceptor is to implement the Open Session In View pattern, which provides a database connection whenever a request is made to your application. And in the end of that request, the connection is disposed. This is specially useful to avoid exceptions like LazyInitializationException when rendering JSPs.
Here is a simple example that starts a database transaction in every request, and when the logic execution ends and the page is rendered, it commits the transaction and closes the database connection.

{% highlight java %}
@RequestScoped
@Intercepts
public class DatabaseInterceptor implements br.com.caelum.vraptor.Interceptor {

    private final Database controller;
    private final Result result;
    private final HttpServletRequest request;

    public DatabaseInterceptor(Database controller, Result result, HttpServletRequest request) {
        this.controller = controller;
        this.result = result;
        this.request = request;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, 
                            Object instance) throws InterceptionException {
        result.include("contextPath", request.getContextPath());
        try {
            controller.beginTransaction();
            stack.next(method, instance);
            controller.commit();
        } finally {
            if (controller.hasTransaction()) {
                controller.rollback();
            }
            controller.close();
        }
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

}
{% endhighlight %}

This way, to use the available connection in your Resource, the following code would apply:

{% highlight java %}
@Resource
public class EmployeeController {
    
    public EmployeeController(Result result, Database controller) {
        this.result = result;
        this.controller = controller;
    }
    
    @Post
    @Path("/employee")
    public void add(Employee employee) {
        controller.getEmployeeDao().add(employee);
        ...
    }
}
{% endhighlight %}

<h3>How to ensure ordering: after and before</h3>

If you need to ensure order of the execution of a set of interceptors, you can use the after and before attributes from @Intercepts. Suppose that FirstInterceptor must run before SecondInterceptor, so you can configure it either by:

{% highlight java %}
@Intercepts(before=SecondInterceptor.class)
public class FirstInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

or

{% highlight java %}
@Intercepts(after=FirstInterceptor.class)
public class SecondInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

You can specify more than one interceptor:

{% highlight java %}
@Intercepts(after={FirstInterceptor.class, SecondInterceptor.class}, 
            before={ForthInterceptor.class, FifthInterceptor.class})
public class ThirdInterceptor implements Interceptor {
    ...
}
{% endhighlight %}

VRaptor will throw an exception if there is a cycle on the ordering, so be careful.

<h3>Interacting with VRaptor's interceptors</h3>

VRaptor has its own sequence of interceptors, and you can define the ordering of your interceptors based on VRaptor's.
Here are the main VRaptor interceptors and what they produce:

<ul>
<li><strong>ResourceLookupInterceptor</strong> - the first interceptor. Finds the right ResourceMethod, which will be passed as argument on accepts() and intercept() methods. It is the default after.</li>

<li><strong>ParametersInstantiatorInterceptor</strong> - instantiates the method parameters based on request. The parameters will be available via MethodInfo#getParameters().</li>

<li><strong>InstantiateInterceptor</strong> - instantiates the controller, which will be passed on the last parameter of intercept() method.</li>

<li><strong>ExecuteMethodInterceptor</strong> - executes the ResourceMethod. Return value will be available via MethodInfo#getResult(). It is the default before.</li>

<li><strong>OutjectResult</strong> - outjects the ResourceMethod's return value to the view.</li>

<li><strong>ForwardToDefaultViewInterceptor</strong> - forwards to default jsp if no other view was used.</li>
</ul>

If you need to execute an Interceptor after ExecuteMethodInterceptor, you <strong>must</strong> set the before attribute, in order to avoid cycles. ForwardToDefaultViewInterceptor is a good value since no other interceptor can run after it:

{% highlight java %}
@Intercepts(after=ExecuteMethodInterceptor.class, 
            before=ForwardToDefaultViewInterceptor.class)
{% endhighlight %}
