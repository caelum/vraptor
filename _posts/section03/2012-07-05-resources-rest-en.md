---
title: Resources-Rest
layout: page
language: en
section: 3
---

<h3>What are Resources?</h3>

Resources are anything that can be accessed by our clients.
In a VRaptor-based Web application, a resource must be annotated with @Resource. If you annotate a class with @Resource, all its public methods become accessible through GET requests to specific URIs.
The following example shows a resource called ClienteController, which provides several operations over clients.
Creating the class below with all its methods instantly make the URIs <strong>/client/add</strong>, <strong>/client/list</strong>, <strong>/client/show</strong>, <strong>/client/remove</strong> and <strong>/client/update</strong> available, each one invoking the respective method.

{% highlight java %}
@Resource
public class ClientController {

  public void add(Client client) {
    ...
  }
  
  public List<Client> list() {
    return ...
  }
  
  public Client show(Client profile) {
    return ...
  }

  public void remove(Client client) {
    ...
  }

  public void update(Client client) {
    ...
  }
  
}
{% endhighlight %}

<h3>Method parameters</h3>

You can receive parameters on your controller methods, and if those parameters follow the java beans convention (getters and setters for class fields), you can use dots for browsing through the fields. For instance, on method:

{% highlight java %}
public void update(Client client) {
   //...
}
{% endhighlight %}

you can receive on the request parameters:

{% highlight jsp %}
client.id=3
client.name=John Doe
client.user.login=johndoe
{% endhighlight %}

and the respective fields will be set, browsing through getters and setters starting from client.
If an object field or a method parameter is a list (List<> or array), you can receive several request parameters, using square brackets and indexes:

{% highlight jsp %}
client.phones[0]=+55 11 5571-2751 #if it is a string list
client.relatives[0].id=1 #if it is an arbitrary object, you can continue to browse
client.relatives[3].id=1 #indexes don't need to be sequential
client.relatives[0].name=Mary Doe #using the same index, it will be set on same object
clients[1].id=23 #it works if you receive a client list as method parameter
{% endhighlight %}

<div class="nota">
<h4>Reflection on parameter names</h4>

Unfortunately Java can't reflect parameters names, these data don't stay in bytecode (unless you compile your code in debug mode, but that is optional). It causes that most frameworks that need this kind of information end up creating annotations, which makes a very ugly code (like JAX-WS, where its very common to find methods with signature like: void add(@WebParam(name="client") Client client)).
VRaptor uses the Paranamer framework (<a href="http://paranamer.codehaus.org">http://paranamer.codehaus.org</a>), which can get parameter names information through pre compilation or debug data, avoiding the creation of annotations for this purpose. Some VRaptor developers also participate in Paranamer development.
</div>

<h3>Scopes</h3>

Sometimes you want to share a component among all users, or through all requests from the same user or one instance for each user request.
To specify in which scope your component will live, use the annotations @ApplicationScoped, @SessionScoped and @RequestScoped.
If you don't specify a scope for your component, VRaptor assumes the request scope, meaning a fresh instance will be created for each request.

<h3>@Path</h3>

The @Path annotation allows you to specify custom access URIs to your controller methods. The basic usage of the annotation is to specify a fixed URI. The following example shows how to customize the access URI for a method that accepts POST requests only. The URI we want to specify is <strong>/client</strong>:

{% highlight java %}
@Resource
public class ClientController {

  @Path("/client")
  @Post 
  public void add(Client client) {
  }
    
}
{% endhighlight %}

If you place the @Path on ClientController, the specified value will be used as prefix for all URIs from this controller.

{% highlight java %}
@Resource
@Path("/clients")
public class ClientController {
    //URI: /clients/list
    public void list() {...}
    
    //URI: /clients/save
    @Path("save")
    public void add() {...}
    
    //URI: /clients/allClients
    @Path("/allClients")
    public void listAll() {...}
}
{% endhighlight %}

<h3>Http Methods</h3>

The best practice when using HTTP Methods is to specify a different methods, like GET, POST, PUT etc, for the same URI.
In order to accomplish that, we use annotations @Get, @Post, @Delete etc, which also allows us to configure a custom URI in the same way as @Path.
The following example changes the default URIs for ClienteController. Now we specify two different URIs 
for different HTTP methods:

{% highlight java %}
@Resource
public class ClientController {

  @Post("/client")
  public void add(Client client) {
  }
  
  @Path("/")
  public List<Client> list() {
    return ...
  }

  @Get("/client")
  public Client show(Client client) {
    return ...
  }

  @Delete("/client")
  public void remove(Client client) {
    ...
  }
  
  @Put("/client")
  public void update(Client client) {
    ...
  }
  
}
{% endhighlight %}

As you can see, we used HTTP methods + a specific URI to identify each method in our Java class.
We must be <strong>very careful</strong> when creating hyperlinks and HTML forms, because web browsers currently support only POST and GET methods.
For that reason, requests for methods DELETE, PUT etc should be created through JavaScript, or by adding an extra parameter called <strong>_method</strong>. The latter one only works on POST requests.
This parameter will overwrite the real HTTP method being invoked.
The following example creates a link to show one client's data:

{% highlight jsp %}
<a href="/client?client.id=5">show client 5</a>
{% endhighlight %}

Now an example on how to invoke the method to add a client:

{% highlight jsp%}
<form action="/client" method="post">
    <input name="client.name" />
    <input type="submit" />
</form>
{% endhighlight %}

Notice that if we want to remove a cliente using the DELETE HTTP method, we have to use the _method parameter, since browsers still don't support that kind of requests:

{% highlight jsp %}
<form action="/client" method="POST">
    <input name="client.id" value="5" type="hidden" />
    <button type="submit" name="_method" value="DELETE">remove client 5</button>
</form>
{% endhighlight %}

<h3>Path with variable injection</h3>

Sometimes we want the uri to include, for example, the unique identifier of my resource.
Suppose a client controller application where the client's unique identifier (primary key) is a number. We can map our URIs as /client/{client.id}, so we can visualize each client.
That is, if we access the URI /client/2, the <strong>show</strong> method will be invoked and the client.id parameter will be set to <strong>2</strong>. If the URI /client/1717 is accessed, the same method will be invoked with the <strong>1717</strong> value.
That way we can create unique URIs to identify different resources in our application. See the mentioned example:

{% highlight java %}
@Resource
public class ClientController {

  @Get
  @Path("/client/{client.id}")  
  public Cliente show(Client client) {
    return ...
  }
  
}
{% endhighlight %}

You can go further and set several parameters through the URI:

{% highlight java %}
@Resource
public class ClientController {

  @Get
  @Path("/client/{client.id}/show/{section}")  
  public Client show(Client client, String section) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Multiple paths for the same logic method</h3>

You can set more than one path for the same logic method:

{% highlight java %}
public class ClientController {

  @Get
  @Path({"/client/{client.id}/show/{section}", "/client/{client.id}/show/"})  
  public Client show(Client client, String section) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Paths with regular expressions</h3>

You can limit your parameter values using regular expressions using this idiom:

{% highlight java %}
@Path("/color/{color:[0-9A-Fa-f]{6}}")
public void setColor(String color) {
    //...
}
{% endhighlight %}

Everything that is after the colon is treated as a regex, and the URI will only match if the parameter matches the regex:

{% highlight jsp %}
/color/a0b3c4 => matches
/color/AABBCC => matches
/color/white => doesn't match
{% endhighlight %}

<h3>Paths with wildcards</h3>

You can also use the * wildcard as a selection method for your URI. The following example ignores anything that comes after the word photo/ :

{% highlight java %}
@Resource
public class ClientController {

  @Get
  @Path("/client/{client.id}/photo/*")  
  public File photo(Client client) {
    return ...
  }
  
}
{% endhighlight %}

And now a similar code, but used to download a specific photo from a client:

{% highlight java %}
@Resource
public class ClientController {

  @Get
  @Path("/client/{client.id}/photo/{photo.id}")  
  public File photo(Client client, Photo photo) {
    return ...
  }
  
}
{% endhighlight %}

Sometimes you want the parameter to include the / character. In that case, you should use the pattern {...*}:

{% highlight java %}
@Resource
public class ClientController {

  @Get
  @Path("/client/{client.id}/download/{path*}")  
  public File download(Client client, String path) {
    return ...
  }
  
}
{% endhighlight %}

<h3>Specifying priorities for your paths</h3>

It is possible for some URIs to be handled by more than one method in our class:

{% highlight java %}
@Resource
public class PostController {

  @Get
  @Path("/post/{post.author}")
  public void show(Post post) { ... }

  @Get
  @Path("/post/current")
  public void current() { ... }
}
{% endhighlight %}

The URI /post/current can be handled by both show and current methods. But I don't want to invoke the show method with that URI, what I want is VRaptor to test the current path first, avoiding the invocation of the show method.
In order to do that, we can define priorities for @Paths, so VRaptor will first test paths with higher priority, in other words, paths with lower priority values.

{% highlight java %}
@Resource
public class PostController {

  @Get
  @Path(value = "/post/{post.author}", priority = Path.LOW)
  public void show(Post post) { ... }

  @Get
  @Path(value = "/post/current", priority = Path.HIGH)
  public void current() { ... }
}
{% endhighlight %}

This way, the "/post/current" path will be tested before "/post/{post.author}" by VRaptor, solving our problem.

<h3>RoutesConfiguration</h3>

Finally, the most advanced way to configure access routes for your resources is using a <strong>RoutesConfiguration</strong>.
This component must be configured as application scoped and must implement the config method:

{% highlight java %}
@Component
@ApplicationScoped
public class CustomRoutes implements RoutesConfiguration {

    public void config(Router router) {
    }

}
{% endhighlight %}

Having access to a <strong>Router</strong>, you can define access routes to methods. And the best part is that the configuration is refactor-friendly, that is, if you change a method's name, the configuration reflects the change, but the uri stays the same:

{% highlight java %}
@Component
@ApplicationScoped
public class CustomRoutes implements RoutesConfiguration {

    public void config(Router router) {
        new Rules(router) {
            public void routes() {
                routeFor("/").is(ClientController.class).list();
                routeFor("/client/random").is(ClientController.class).random();
            }
        };
    }

}
{% endhighlight %}

You can also put parameters on the uri and they will be set directly on the method parameters. You can also add restrictions to these parameters:

{% highlight java %}
// show method receives a Client that has an id
routeFor("/client/{client.id}").is(ClientController.class).show(null);
// If I want to ensure that the parameter is a number:
routeFor("/client/{client.id}").withParameter("client.id").matching("\\d+")
            .is(ClientController.class).show(null);
{% endhighlight %}
