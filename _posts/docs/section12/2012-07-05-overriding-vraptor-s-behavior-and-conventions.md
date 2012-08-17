---
title: Overriding Vraptor's behavior and conventions
layout: page
language: en
section: 12
category: [en, docs]
---

Most of VRaptor behaviours and conventions can be customized, in a very easy way: it is just creating a component that implements an internal interface of VRaptor. When you do this, VRaptor will use your custom implementation instead of the default one.
If you want to find out which is the right interface for changing a behaviour, just ask it on the developers mailing list: caelum-vraptor-dev@googlegroups.com
This list is for both Portuguese and English discussions.
Below we'll see some customization examples:

<h3>Changing the default rendered view</h3>

If you need to change the default rendered view, or change the place where it'll be look for, you'll only need to create the following class:

{% highlight java %}
@Component
public class CustomPathResolver extends DefaultPathResolver {
    
    @Override
    protected String getPrefix() {
        return "/root/directory/";
    }
    
    @Override
    protected String getExtension() {
        return "ftl"; // or any other extension
    }

    @Override
    protected String extractControllerFromName(String baseName) {
        return //your convention here
               //ex.: If you want to redirect UserController to 'userResource' instead of 'user'
               //ex.2: If you override the convention for Controllers name to XXXResource
        //and still want to redirect to 'user' and not to 'userResource'
    }
}
{% endhighlight %}

If you need a more complex convention, just implement the PathResolver interface.

<h3>Changing default URI</h3>

The default URI for ClientsController.list() is /clients/list, i.e, controller_name/method_name. If you want to override this convention, you can create a class like:

{% highlight java %}
@Component
@ApplicationScoped
public class MyRoutesParser extends PathAnnotationRoutesParser {
    //delegate constructor
    protected String extractControllerNameFrom(Class<?> type) {
        return //your convention here
    }

     protected String defaultUriFor(String controllerName, String methodName) {
        return //your convention here
    }
}
{% endhighlight %}

If you need a more complex convention, just implement the RoutesParser interface.

<h3>Changing the application character encoding</h3>

For using an arbitrary character encoding on all your requests and responses, avoiding encoding inconsistencies, you can set this parameter on your web.xml.

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.encoding</param-name>
    <param-value>UTF-8</param-value>
</context-param>
{% endhighlight %}

This way all of your pages and form data will use the UTF-8.
