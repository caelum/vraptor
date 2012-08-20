---
title: View and Ajax
layout: page
section: 8
category: [en, docs]
---

<h3>Sharing objects with the view</h3>

To register objects so they can be accessed in your templates:

{% highlight java %}
@Resource
class ClientController {
    private final Result result;
    public ClientController(Result result) {
        this.result = result;
    }
    
    public void search(int id) {
        result.include("message", "Some message");
        result.include("client", new Client(id));
    }
}
{% endhighlight %}

The first object is accessed as "message", while the second as "client". You can register objects by invoking include with only one parameter if you wish:

{% highlight java %}
@Resource
class ClientController {
    private final Result result;
    public ClientController(Result result) {
        this.result = result;
    }
    
    public void search(int id) {
        result.include("Some message").include(new Client(id));
    }
}
{% endhighlight %}

In this case, the first key is "string" while the second is "client". Mix one argument and two arguments invocation to create simpler code for your application. One can change the behaviour of the key extracting process by creating your own TypeNameExtractor.

<h3>Custom PathResolver</h3>

By default, VRaptor tries to render your views following the convention:

{% highlight java %}
public class ClientsController {
    public void list() {
        //...
    }
}
{% endhighlight %}

The method listed above will render the view /WEB-INF/jsp/clients/list.jsp.
However, we don't always want it to behave that way, specially if we need to use some template engine like Freemarker or Velocity. In that case, we need to change the convention.
An easy way of changing that convention is extending the DefaultPathResolver class:

{% highlight java %}
@Component
public class FreemarkerPathResolver extends DefaultPathResolver {
    protected String getPrefix() {
        return "/WEB-INF/freemarker/";
    }
    
    protected String getExtension() {
        return "ftl";
    }
}
{% endhighlight %}

That way, the logic would try to render the view /WEB-INF/freemarker/clients/list.ftl. If that solution is not enough, you can implement the PathResolver interface and do whatever convention you wish. Don't forget to annotate your new classe with @Component.

<h3>View</h3>

If you want to change a specific logic's view, you can use the Result object:

{% highlight java %}
@Resource
public class ClientsController {
    
    private final Result result;
    
    public ClientsController(Result result) {
        this.result = result;
    }
    
    public void list() {}
    
    public void save(Client client) {
        //...
        this.result.use(Results.logic()).redirectTo(ClientsController.class).list();
    }
}
{% endhighlight %}

By default, there are these view implementations:

<ul>
<li>Results.logic(), redirects to any other logic in the application.</li>

<li>Results.page(), redirects directly to a page, that can be a jsp, an html, or any URI relative to the web application directory or the application context.</li>

<li>Results.http(), sends HTTP protocol informations, like status codes and headers.</li>

<li>Results.status(), sends status codes with more information.</li>

<li>Results.referer(), uses Referer header to redirect or forward.</li>

<li>Results.nothing(), simply returns the HTTP success code (HTTP 200 OK).</li>

<li>Results.xml(), uses xml serialization.</li>

<li>Results.json(), uses json serialization.</li>

<li>Results.representation(), serializes objects in a format set by the request (_format parameter or Accept header)</li>
</ul>

<h3>Result shortcuts</h3>

Some redirections are pretty common, so there are shortcuts on Result interface for them. The available shortcuts are:

<ul>
<li>result.forwardTo("/some/uri") ==> result.use(page()).forward("/some/uri");</li>

<li>result.redirectTo("/some/uri") ==> result.use(page()).redirect("/some/uri)</li>

<li>result.permanentlyRedirectTo("/some/uri") ==> result.use(status()).movedPermanentlyTo("/some/uri");</li>

<li>result.forwardTo(ClientController.class).list() ==> result.use(logic()).forwardTo(ClientController.class).list();</li>

<li>result.redirectTo(ClientController.class).list() ==> result.use(logic()).redirectTo(ClientController.class).list();</li>

<li>result.of(ClientController.class).list() ==> result.use(page()).of(ClientController.class).list();</li>

<li>result.permanentlyRedirectTo(Controller.class) ==> use(status()).movedPermanentlyTo(Controller.class);</li>

<li>result.notFound()	 ==> use(status()).notFound()</li>

<li>result.nothing()	 ==> use(nothing());</li>
</ul>

Furthermore, if one are redirecting to a method on the same controller, one can use:

<ul>
	<li>result.forwardTo(this).list() ==> result.use(logic()).forwardTo(this.getClass()).list();</li>

	<li>result.redirectTo(this).list() ==> result.use(logic()).redirectTo(this.getClass()).list();</li>

	<li>result.of(this).list() ==> result.use(page()).of(this.getClass()).list();</li>

	<li>result.permanentlyRedirectTo(this) ==> use(status()).movedPermanentlyTo(this.getClass());</li>
</ul>

<h3>Redirect and forward</h3>

In VRaptor3, you can either redirect or forward the user to another logic or page. The main difference between redirecting and forwarding is that the former happens at client side, while the latter happens at server side.
A good redirect use is the pattern 'redirect-after-post', for example, when you add a client and you want to return to the client listing page, but you want to avoid the user to accidentally resend all data by refreshing (F5) the page.
An example of forwarding is when you have some data validation that fails, usually you want the user to remain on the form with all the previously filled data.

<div class="nota">
<h4>Automatic Flash Scope</h4>

If you add objects on Result and redirects to another logic, these objects will be available on the next request.

{% highlight java %}
public void add(Client client) {
    dao.add(client);
    result.include("notice", "Client successfully added");
    result.redirectTo(ClientsController.class).list();
}
{% endhighlight %}

list.jsp:

{% highlight jsp %}
...
<div id="notice">
   <h3>${notice}</h3>
</div>
...
{% endhighlight %}
</div>

<h3>Accepts and the _format parameter</h3>

Many times you need to render different formats for the same logic. For example, we want to return a JSON object instead of an HTML page. In order to do that, we can define the request's Accepts header to accept the desired format, or we can pass a &#95;format paramenter in the request.
If the specified format is JSON, the default rendered view will be: /WEB-INF/jsp/{controller}/{logic}.json.jsp, which means, in general, the rendered view will be: /WEB-INF/jsp/{controller}/{logic}.{format}.jsp. If the format is HTML, then you won't need to specify it in the file name.
The &#95;format parameter has a higher priority over the Accepts header.

<h3>Ajax: building on the view</h3>

In order to return a JSON object to the view, your logic must make that object available somehow. Just like the following example, your /WEB-INF/jsp/clients/load.json.jsp:

{% highlight jsp %}
{ name: '${client.name}', id: '${client.id}' }
{% endhighlight %}

And in the controller:

{% highlight java %}
@Resource
public class ClientsController {
    
    private final Result result;
    private final ClientDao dao;
    
    public ClientsController(Result result, ClientDao dao) {
        this.result = result;
        this.dao = dao;
    }
    
    public void load(Client client) {
        result.include("client", dao.load(client));
    }
}
{% endhighlight %}

<h3>Ajax: Programatic version</h3>

If you want that VRaptor automatically serializes your objects into xml or json, you can use on your logic:

{% highlight java %}
import static br.com.caelum.vraptor.view.Results.*;
@Resource
public class ClientsController {
    
    private final Result result;
    private final ClientDao dao;
    
    public ClientsController(Result result, ClientDao dao) {
        this.result = result;
        this.dao = dao;
    }
    
    public void loadJson(Client client) {
        result.use(json()).from(client).serialize();
    }
    public void loadXml(Client client) {
        result.use(xml()).from(client).serialize();
    }
}
{% endhighlight %}

The results will be like:

{% highlight json %}
{"client": {
	"name": "John"
}}
{% endhighlight %}

{% highlight xml %}
<client>
	<name>John</name>
</client>
{% endhighlight %}

By default, only fields with primitive types will be serialized (String, numbers, enums, dates), if you want to include a field of a non-primitive type you must explicitly include it:

{% highlight java %}
result.use(json()).from(client).include("address").serialize();
{% endhighlight %}

will result in something like:

{% highlight json %}
{"client": {
	"name": "John",
	"address" {
		"street": "First Avenue"
	}
}}
{% endhighlight %}

You can also exclude primitive fields from serialization:

{% highlight java %}
result.use(json()).from(user).exclude("password").serialize();
{% endhighlight %}

will result in something like:

{% highlight json %}
{"user": {
	"name": "John",
	"login": "john"
}}
{% endhighlight %}

Moreover you can serialize recursively (be careful with cycles):

{% highlight java %}
result.use(json()).from(user).recursive().serialize();
result.use(xml()).from(user).recursive().serialize();
{% endhighlight %}

The default implementation is based on XStream, so you can configure the serialization with annotations and direct configuration on XStream. It is just creating a class like:

{% highlight java %}
@Component
public class CustomXMLSerialization extends XStreamXMLSerialization {
//or public class CustomJSONSerialization extends XStreamJSONSerialization {
    //delegate constructor
    
    @Override
    protected XStream getXStream() {
        XStream xStream = super.getXStream();
        //your xStream setup here
        return xStream;
    }
}
{% endhighlight %}

<h3>Serializing Collections</h3>

When serializing collections, vRaptor will wrap their elements with a "list" tag:

{% highlight java %}
List<Client> clients = ...;
result.use(json()).from(clients).serialize();
//or
result.use(xml()).from(clients).serialize();
{% endhighlight %}

will result in something like

{% highlight json %}
{"list": [
	{
		"name": "John"
	},
	{
		"name": "Sue"
	}
]}
{% endhighlight %}

or

{% highlight xml %}
<list>
	<client>
		<name>John</name>
	</client>
	<client>
		<name>Sue</name>
	</client>
</list>
{% endhighlight %}

You can customize the wrapper element via:

{% highlight java %}
List<Client> clients = ...;
result.use(json()).from(clients, "clients").serialize();
//or
result.use(xml()).from(clients, "clients").serialize();
{% endhighlight %}

will result in something like:

{% highlight json %}
{"clients": [
	{
		"name": "John"
	},
	{
		"name": "Sue"
	}
]}
{% endhighlight %}

or

{% highlight xml %}
<clients>
	<client>
		<name>John</name>
	</client>
	<client>
		<name>Sue</name>
	</client>
</clients>
{% endhighlight %}

Includes and excludes work the same as if you were serializing an element inside the collection. For instance, if you want to include the client's address:

{% highlight java %}
List<Cliente> clients = ...;
result.use(json()).from(clients).include("address").serialize();
{% endhighlight %}

results in:

{% highlight json %}
{"list": [
	{
		"name": "John",
		"address": {
			"street": "Vergueiro, 3185"
		}
	},
	{
		"name": "Sue",
		"address": {
			"street": "Vergueiro, 3185"
		}
	}
]}
{% endhighlight %}

<h3>Serializing JSON without root element</h3>

If you want to serialize an object to json without give it any names, you can use the withoutRoot method:

{% highlight java %}
result.use(json()).from(car).serialize(); //=> {'car': {'color': 'blue'}}
result.use(json()).withoutRoot().from(car).serialize(); //=> {'color': 'blue'}
{% endhighlight %}
