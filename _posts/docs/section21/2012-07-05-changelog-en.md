---
title: Changelog
layout: page
section: 21
categories: [en, docs]
---

<h3>3.3.1</h3>

<ul>
	<li>bugfix - fixed scannotation as mandatory on maven</li>
	<li>bugfix - fixed ConcurrentModificationException on interceptors ordering</li>
	<li>updating spring from 3.0.0 to 3.0.5</li>
	<li>bugfix - fixed @PostConstruct on @ApplicationScoped components when using Spring as DI container.</li>
	<li>better docs</li>
	<li>bugfix - redirect to @Path's with regexes</li>
	<li>bugfix - Hibernate and JPA Transaction interceptors now rollback when there are validation errors.</li>
</ul>

<h3>3.3.0</h3>

<div class="nota">
	<h4>Jar changes</h4>
	<ul>
		<li>change google-collect 1.0rc to guava-r07.</li>
		<li>scannotations 1.0.2 is now mandatory.</li>
	</ul>
</div>

<ul>
	<li><strong>better Spring integration</strong>: now Spring components can access VRaptor components and vice-versa</li>
	<li>guice: @PostConstruct and @PreDestroy working properly</li>
	<li>guice: all request and session scoped components are exported to the view in the same way as spring provider (class name as key)</li>
	<li><strong>interceptor ordering strategy changed</strong>: one can order the interceptors using @Intercepts annotation, specifying which interceptors must run before or after the annotated interceptor

		{% highlight java %}
@Intercepts(before=AnInterceptor.class, after=AnotherInterceptor.class)
		{% endhighlight %}

		So VRaptor will execute the interceptors in an ordering that respects before and after restrictions of all interceptors. Therefore the InterceptorSequence interface is deprecated.
	</li>
	<li>HTTP verb annotations now also can set paths:

		{% highlight java %}
@Get("/items/{id}"), @Post("/items"), etc
		{% endhighlight %}
	</li>
	<li>bugfix: @Transactional from Spring can be used in any class (within spring aop limitations)</li>
	<li>bugfix: upload of files with same name</li>
	<li>bugfix: web-fragments.xml on jboss 6</li>
	<li>bugfix: better support for arrays as parameters</li>
	<li>new Download implementation: ByteArrayDownload and JFreeChartDownload</li>
	<li>new jsonp view:

		{% highlight java %}
result.use(jsonp()).withCallback("theCallback").from(object).serialize();
		{% endhighlight %}

		which returns

		{% highlight java %}
theCallback({"object": {...} })
		{% endhighlight %}
	</li>
	<li>commons-io dependency removed</li>
	<li>PageResult methods renamed for consistency with other results.</li>
	<li>better upload logs</li>
	<li>refactor on converters: using Localization to get Locale and bundle </li>
	<li>Hibernate class removed. Use validator.validate(object) instead.</li>
	<li>JSON response with optional indentation.</li>
</ul>

<h3>3.2.0</h3>

<ul>
	<li>several performance tweaks: about 60% less request time.</li>
	<li><strong>internal compatibility break</strong>: InterceptorStack interface reorganized.</li>
	<li>better implementation of VRaptor internal interceptors accepts method.</li>
	<li>beta support to Google Guice, that can be used instead of Spring.</li>
	<li>Pico provider is not deprecated anymore.</li>
	<li>One can change the DI container without configuring it on web.xml.
		If VRaptor finds the Spring jars on classpath, Spring will be used; if PicoContainer jars are found it will be used; the same for Guice jars. One can find the container jars on lib/containers folder on vraptor zip.
	</li>
	<li><strong>internal compatibility break</strong>: interfaces <em>Converters</em>, <em>Router</em> and constructor of <em>PathAnnotationRoutesParser</em> class were changed. RouteBuilder	converted is now an interface => DefaultRouteBuilder is the implementation.
		Those who extend PathAnnotationRoutesParser must change the call to delegate constructor.
		Those who instantiate RouteBuilder directly must instantiate DefaultRouteBuilder.
	</li>
	<li>new annotation @Lazy. Use it on interceptors which accepts method doesn't depend on the interceptor internal state:

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

		In this case, MyLazyInterceptor will only be instantiated when accepts returns true.
		A non-functional instance of MyLazyInterceptor will be used to call the accepts method, so it should not depend on the interceptor's internal state.
		Do not use @Lazy if your accepts is trivial (always returns true).
	</li>
	<li><strong>slight backwards compatibility break</strong>: the default priority of @Path has changed to Integer.MAX_INTEGER/2. It was Integer.MAX_INTEGER - 1. Despite of this compatibility break, we believe that it won't affect working applications.
	</li>
	<li>@Path priority can be defined with constants:

		{% highlight java %}
@Path(value="/url", priority=Path.HIGHEST)
@Path(value="/url", priority=Path.HIGH)
@Path(value="/url", priority=Path.DEFAULT)
@Path(value="/url", priority=Path.LOW)
@Path(value="/url", priority=Path.LOWEST)
		{% endhighlight %}
	</li>
	<li>Servlet 3.0 upload support (by garcia-jj)</li>
	<li>new Exception handlers (by garcia-jj)

		{% highlight java %}
result.on(SomeException.class).forwardTo(Controller.class).method();
//if a SomeException occurs, the request will be redirected
		{% endhighlight %}
	</li>
	<li>new interface <em>TwoWayConverter</em> for bidirecional convertions.</li>
	<li>native support for OPTIONS requests</li>
	<li>fix: 405 instead of 500 on requests with unknown HTTP method.</li>
	<li>more Joda Time converters (by Rodolfo Liviero)</li>
	<li>improvings on Scala Blank Project (by Pedro Matiello)</li>
	<li>bugfix: null Accept Header fallback to html</li>
</ul>

<h3>3.1.3</h3>

<ul>
	<li>Scala Blank Project</li>
	<li>Better strategy on Flash scope</li>
	<li>starting support for javax.inject API. Naming logic parameters is now possible:</li>
	<li>bugfixes on new Validator</li>
	<li>bugfix: char as URI parameter</li>
	<li>bugfix: now VRaptor works with browsers that do not correctly send Accepts header.

		{% highlight java %}
public void logic(@Named("a_name") String anotherName) {...}
		{% endhighlight %}

		So the request parameter must be called 'a_name'.
	</li>
	<li>better support for GAE</li>
	<li>new method on http result:

		{% highlight java %}
result.use(http()).body(content);
		{% endhighlight %}

		content can be either a String, an InputStream or a Reader.
	</li>
	<li>more available methods for result.use(status())</li>
	<li>new method: result.use(representation()).from(object, alias)</li>
	<li>support for multiple selects:

		{% highlight java %}
public void logic(List<String> abc) {...}
		{% endhighlight %}

		{% highlight xml %}
<select name="abc[]" multiple="multiple">...</select>
		{% endhighlight %}
	</li>
	<li>auto 406 status when using result.use(representation())</li>
	<li>One can register now all optional vraptor components on packages configuration on web.xml:

		{% highlight xml %}
<context-param>
	<param-name>br.com.caelum.vraptor.packages</param-name>
	<param-value>
		br.com.caelum.vraptor.util.hibernate, // Session and SessionFactory
		br.com.caelum.vraptor.util.jpa, // EntityManager and EntityManagerFactory
		br.com.caelum.vraptor.converter.l10n, //Localized numeric Converters
		br.com.caelum.vraptor.http.iogi // Immutable parameters support
	</param-value>
</context-param>
		{% endhighlight %}
	</li>
	<li>rendering a null representation means returning a 404</li>
	<li>new class: JsonDeserializer</li>
	<li>MultipartInterceptor is now optional.</li>
	<li>bugfix: arrays of length == 1 are now supported as logic parameters</li>
	<li>Pico provider is deprecated</li>
	<li>Validations using the request bundle (and locale)</li>
	<li>ValidationMessage implements Serializable</li>
	<li>new method: result.use(status()).badRequest(errorList); serializes the given error list with:

		{% highlight java %}
result.use(representation()).from(errorList, "errors");
		{% endhighlight %}
	</li>
	<li>shortcuts on Validator:

		{% highlight java %}
validator.onErrorForwardTo(controller).logic();
validator.onErrorRedirectTo(controller).logic();
validator.onErrorUsePageOf(controller).logic();
		{% endhighlight %}

		where controller can be either a controller class or this, as in Result shortcuts.
		And the shortcut:

		{% highlight java %}
validator.onErrorSendBadRequest();
		{% endhighlight %}

		which returns the Bad Request (400) status codes and serializes the validation error list according to Accept request header (result.use(representation()))
	</li>
</ul>

<h3>3.1.2</h3>

<ul>
	<li>Blank project now also runs on netbeans 6.8</li>
	<li>Supports encoding for file uploads in Google App Engine</li>
	<li>bugfix: no more NullPointerExceptions on validator.onErrorUse(json())...</li>
	<li>Serializers now have the recursive method:

		{% highlight java %}
result.use(xml()).from(myObject).recursive().serialize();
		{% endhighlight %}

		It means that all object tree from myObject will be serialized.
	</li>
	<li>Message parameters on Validations now can be i18n'ed:

		{% highlight java %}
// age = Age
// greater_than = {0} should be greater than {1}

validator.checking(new Validations() { {
	that(age > 18, "age", "greater_than", i18n("age"), 10);
	//results on "Age should be greater than 18"
}});
		{% endhighlight %}
	</li>
	<li>Hibernate proxies are now nicely serialized (almost) like regular classes (thanks to Tomaz Lavieri)</li>
	<li>It is now possible to serialize to json without the root element (thanks to Tomaz Lavieri):

		{% highlight java %}
result.use(json()).from(car).serialize(); //=> {'car': {'color': 'blue'} }
result.use(json()).withoutRoot().from(car).serialize(); //=> {'color': 'blue'}
		{% endhighlight %}
	</li>
	<li>Google collections updated to version 1.0</li>
	<li>fixed bug with curly braces on regexes inside @Path's</li>
	<li>XStream annotations are now automatically read when you use VRaptor's default serialization</li>
	<li>when you upload a file bigger than the file size limit you get a validation error instead of a generic exception</li>
	<li>more shortcuts on Result interface:

		{% highlight java %}
redirectTo("a/uri")				=>  use(page()).redirect("a/uri")
notFound()						=>  use(status()).notFound()
nothing()						=>  use(nothing());
permanentlyRedirectTo(Controller.class)
		=> use(status()).movedPermanentlyTo(Controller.class);
permanentlyRedirectTo("a/uri") 	=> use(status()).movedPermanentlyTo("a/uri");
permanentlyRedirectTo(this)		=> use(status()).movedPermanentlyTo(this.getClass());
		{% endhighlight %}
	</li>
	<li>added a new method to <em>Validator</em> interface (thanks to Otávio Garcia)

		{% highlight java %}
validator.validate(object);
		{% endhighlight %}

		This method will validate the given object using Hibernate Validator 3, Java Validation API (JSR303), or any implementation of BeanValidation annotated with @Component
	</li>
	<li>new BigDecimal, Double and Float converters, that consider the current Locale to convert the values (thanks to Otávio Garcia).
		In order to use them you must add to your web.xml:

		{% highlight xml %}
<context-param>
	<param-name>br.com.caelum.vraptor.packages</param-name>
	<param-value>!!previous value!!,br.com.caelum.vraptor.converter.l10n</param-value>
</context-param>
		{% endhighlight %}
	</li>
</ul>

<h3>3.1.1</h3>

<ul>
	<li>VRaptor 3 was published on Maven central repository!

		{% highlight xml %}
<dependency>
	<groupId>br.com.caelum</groupId>
	<artifactId>vraptor</artifactId>
	<version>3.1.1</version>
</dependency>
		{% endhighlight %}
	</li>
	<li>new implementation for Outjector. Now when there are validation errors actual objects are replicated to the next request, not string parameters as before, preventing class cast exceptions on taglibs.</li>
	<li>bugfixes on VRaptor 2 compatibility</li>
</ul>

<h3>3.1.0</h3>

<ul>
	<li>it is now possible to serialize collections via result.use(xml()) or result.use(json()).</li>
	<li>new scope: @PrototypeScoped, that creates a new instance of annotated class whenever it is requested. </li>
	<li>new view: result.use(Results.representation()).from(object).serialize(); This view tries to discover the request format (through _format or Accept header) and then serialize the given object in this format. For now only xml and json are supported, but you can add a serializer for any format you like. If there is no format given, or it is unsupported the default jsp page will be shown.</li>
	<li>bugfix: Flash scope parameters are now set with arrays, so it will work on GAE</li>
	<li>bugfix: validation.onErrorUse(...) now works with all default Results</li>
	<li>bugfix: when returning a null Download/File/InputStream will not throw NullPointerException if any redirect has occurred (result.use(...)).</li>
	<li>bugfix: result.use(page()).redirect("...") now includes the contextPath if given url starts with /</li>
	<li>bugfix: one can create generic Controllers now:

		{% highlight java %}
public class ClientsController extends GenericController<Client> {

}
public class GenericController<T> {
	public T show(Long id) {...} // exported variable will be called t
	public void add(T obj) {...} // request parameters will be like obj.field
}
		{% endhighlight %}</li>

	<li>you can annotate your controller class with @Path, and all methods URIs will have the path specified as prefix.:

		{% highlight java %}
@Resource
@Path("/prefix")
public class MyController {
	//URI: /prefix/aMethod
	public void aMethod() {...}

	//URI: /prefix/relative
	@Path("relative")
	public void relativePath() {...}

	//URI: /prefix/absolute
	@Path("/absolute")
	public void absolutePath() {...}
}
		{% endhighlight %}
	</li>
	<li>@Path now supports regexes: <em>@Path("/abc/{abc:a+b+c+}")</em> will match URIs like:

		{% highlight jsp %}
/abc/abc
/abc/aaaaabbcccc
/abc/abbc
		{% endhighlight %}

		whenever the parameter matches the <em>a+b+c+</em> regex.
	</li>
	<li>New methods on <em>Result</em> interface as shortcuts for most common operations:
		<ul>
			<li>result.forwardTo("/some/uri") ==> result.use(page()).forward("/some/uri");</li>
			<li>result.forwardTo(ClientController.class).list() ==> result.use(logic()).forwardTo(ClientController.class).list();</li>
			<li>result.redirectTo(ClientController.class).list() ==> result.use(logic()).redirectTo(ClientController.class).list();</li>
			<li>result.of(ClientController.class).list() ==> result.use(page()).of(ClientController.class).list();</li>
		</ul>

		Furthermore, if one are redirecting to a method on the same controller, one can use:

	  	<ul>
		  	<li>result.forwardTo(this).list() ==> result.use(logic()).forwardTo(this.getClass()).list();</li>
			<li>result.redirectTo(this).list() ==> result.use(logic()).redirectTo(this.getClass()).list();</li>
			<li>result.of(this).list() ==> result.use(page()).of(this.getClass()).list();</li>
		</ul>
	</li>
	<li>VRaptor will scan for all resources and components inside WEB-INF/classes automatically</li>
	<li>support for servlets 3.0, so it is not necessary configure the filter anymore (webfragment feature)</li>
	<li>using latest spring version (3.0.0) and also hibernate (for examples and so on). google collections updated to final version</li>
	<li>blank project now working for wtp 3.5 and using vraptor 3.1 new features</li>
	<li>blank project much easier to import under wtp now. logging and other configs adjusted</li>
	<li>bugfix: mimetypes now work for webkit browsers, priorizing html when no order specified</li>
	<li>bugfix: in case of validation erros, the request parameters are outjected as Strings, not Maps as before.
		It prevents ClassCastExceptions when using taglibs, like fmt:formatNumber.
	</li>
</ul>

<h3>3.0.2</h3>

<ul>
	<li>servlet 2.4 containers are now supported</li>
	<li>bugfix: Results.referer() now implements View</li>
	<li>bugfix: content-type is now set when using File/InputStreamDownload</li>
	<li>removed java 6 api calls</li>
	<li>new providers, spring based: HibernateCustomProvider and JPACustomProvider. These providers register optional Hibernate or JPA components.</li>
	<li>bugfix: converters are not throwing exceptions when there is no ResourceBundle configured.</li>
	<li>bugfix: method return values are now included on result when forwarding.</li>
	<li>bugfix: request parameters are now kept when there is a validation error.</li>
	<li>bugfix: throwing exception when paranamer can't find parameters metadata, so you can recover for this problem.</li>
	<li>initial support to xml and json serialization:

		{% highlight java %}
result.use(Results.json()).from(myObject).include(...).exclude(...).serialize();
result.use(Results.xml()).from(myObject).include(...).exclude(...).serialize();
		{% endhighlight %}
	</li>
</ul>

<h3>3.0.1</h3>

<ul>
	<li>paranamer upgraded to version 1.5 (Update your jar!)</li>
	<li>jars split in optional and mandatory on vraptor-core</li>
	<li>dependencies are now explained on vraptor-core/libs/mandatory/dependencies.txt and vraptor-core/libs/optional/dependencies.txt</li>
	<li>you can set now your application character encoding on web.xml through the context-param br.com.caelum.vraptor.encoding</li>
	<li>new view: Referer view: result.use(Results.referer()).redirect();</li>
	<li>Flash scope:

		{% highlight java %}
result.include("aKey", anObject);
result.use(logic()).redirectTo(AController.class).aMethod();
		{% endhighlight %}

		objects included on Result will survive until next request when a redirect happens.
	</li>
	<li>@Path annotation supports multiple values (String -> String&#91;&#93;)</li>
	<li>Result.include returns this to enable a fluent interface (result.include(...).include(...))</li>
	<li>Better exception message when there is no such http method as requested</li>
	<li>FileDownload registers content-length</li>
	<li>Solving issue 117: exposing null when null returned (was exposing "ok")</li>
	<li>Solving issue 109: if you have a file <em>/path/index.jsp</em>, you can access it now through <em>/path/</em>, unless you have a controller that handles this URI.</li>
	<li>When there is a route that can handle the request URI, but doesn't allow the requested HTTP method, VRaptor will send a 405 -> Method Not Allowed HTTP status code, instead of 404.</li>
	<li>A big refactoring on Routes internal API.</li>
</ul>

<h3>3.0.0</h3>

<ul>
	<li>ValidationError renamed to ValidationException</li>
	<li>result.use(Results.http()) for setting headers and status codes of HTTP protocol</li>
	<li>bug fixes</li>
	<li>documentation</li>
	<li>new site</li>
</ul>

<h3>3.0.0-rc-1</h3>

<ul>
	<li>example application: mydvds</li>
	<li>new way to add options components into VRaptor:

		{% highlight java %}
public class CustomProvider extends SpringProvider {

	@Override
	protected void registerCustomComponents(ComponentRegistry registry) {
		registry.registry(OptionComponent.class, OptionComponent.class);
	}
}
		{% endhighlight %}
	</li>
	<li>Utils: HibernateTransactionInterceptor and JPATransactionInterceptor</li>
	<li>Full application example inside the docs</li>
	<li>English docs</li>
</ul>

<h3>3.0.0-beta-5</h3>

<ul>
	<li>New way to do validations:

		{% highlight java %}
public void visualiza(Client client) {
	validator.checking(new Validations() { {
		that(client.getId() != null, "id", "id.should.be.filled");
	}});
	validator.onErrorUse(page()).of(ClientsController.class).list();

	//continua o metodo
}
		{% endhighlight %}
	</li>
	<li>UploadedFile.getFile() now returns InputStream.</li>
	<li>EntityManagerCreator and EntityManagerFactoryCreator</li>
	<li>bugfixes</li>
</ul>

<h3>3.0.0-beta-4</h3>

<ul>
	<li>New result: result.use(page()).of(MyController.class).myLogic() renders the default view (/WEB-INF/jsp/meu/myLogica.jsp) without execting the logic.</li>
	<li>Mock classes for testing: MockResult e MockValidator, to make easier to unit test your logics. They ignores the fluent interface calls and keep the parameters included under the result and the validation errors.</li>
	<li>The URIs passed to result.use(page()).forward(uri) and result.use(page()).redirect(uri) can't be logic URIs. Use forwards or redirects from result.use(logic()) instead.</li>
	<li>Parameters passed to URI's now accepts pattern-matching:
		<ul>
			<li>Automatic: if we have the URI /clients/{client.id} and client.id is a Long, the {client.id} parameter will only match numbers, so, the URI /clients/42 matches, but the /clients/random doesn't matches. This works for all numeric types, booleans and enums. VRaptor will restrict the possible values.</li>
			<li>Manual: in your CustomRoutes you can do:

				{% highlight java %}
routeFor("/clients/{client.id}").withParameter("client.id")
								.matching("\\d{1,4}")
								.is(ClienteController.class).mostra(null);
				{% endhighlight %}

				which means you can restrict values for the parameters you want by regexes at the matching method.
			</li>
		</ul>
	</li>
	<li>Converters for joda-times's LocalDate and LocalTime comes by default.</li>
	<li>When Spring is the IoC provider, VRaptor tries to find your application's spring to use as a father container. This search is made by one of the following two ways:
		<ul>
			<li>WebApplicationContextUtils.getWebApplicationContext(servletContext), when you have Spring's listeners configured.</li>
			<li>applicationContext.xml inside the classpath</li>
		</ul>
		If it's not enough, you can implements the SpringLocator interface and enable the Spring's ApplicationContext used by your application.
	</li>
	<li>Utils:
		<ul>
			<li>SessionCreator and SessionFactoryCreator to create Hibernate's Session and SessionFactory to your registered components.</li>
			<li>EncodingInterceptor, to change you default encoding.</li>
		</ul>
	</li>
	<li>several bugfixes and docs improvements.</li>
</ul>

<h3>3.0.0-beta-3</h3>

<ul>
	<li>Spring becomes the default IoC provider</li>
	<li>the applicationContext.xml under the classpath is used as Spring initial configuration, if it exists.</li>
	<li>improved docs at http://vraptor.caelum.com.br/documentacao</li>
	<li>small bugfixes and optimizations</li>
</ul>
