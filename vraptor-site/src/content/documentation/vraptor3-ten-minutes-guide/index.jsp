
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/header.jsp">
	<jsp:param name="extras" value='
		<link href="../includes/css/java.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="../includes/css/xml2html.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="../includes/css/style.css" rel="stylesheet" type="text/css" media="screen" />
	'/>
</jsp:include><div id="contentWrap">
    	<div id="contentDocumentacao">
        	<h2><span>documentação</span></h2>
            <h3>documentação toda em português, configuração, migração e utilização.</h3>
            
            <div id="subMenuDoc">
            	<img id="positionTop" src="../includes/images/subMenuTop-trans.png" />
                <img id="positionBottom" src="../includes/images/subMenuBottom-trans.png" />
            	<ol type="1">
									<li><a class="link_toc" href="../../documentation/vraptor3-one-minute-guide/">1. VRaptor3 - One minute guide</a></li>
		
									<li><a class="link_toc" href="../../documentation/vraptor3-ten-minutes-guide/">2. VRaptor3 - Ten minutes guide</a></li>
		
									<li><a class="link_toc" href="../../documentation/resources-rest/">3. Resources-Rest</a></li>
		
									<li><a class="link_toc" href="../../documentation/components/">4. Components</a></li>
		
									<li><a class="link_toc" href="../../documentation/converters/">5. Converters</a></li>
		
									<li><a class="link_toc" href="../../documentation/interceptors/">6. Interceptors</a></li>
		
									<li><a class="link_toc" href="../../documentation/validation/">7. Validation</a></li>
		
									<li><a class="link_toc" href="../../documentation/view-and-ajax/">8. View and Ajax</a></li>
		
									<li><a class="link_toc" href="../../documentation/dependency-injection/">9. Dependency injection</a></li>
		
									<li><a class="link_toc" href="../../documentation/downloading/">10. Downloading</a></li>
		
									<li><a class="link_toc" href="../../documentation/utility-components/">11. Utility Components</a></li>
		
									<li><a class="link_toc" href="../../documentation/advanced-configurations-overriding-vraptor-s-behavior-and-conventions/">12. Advanced configurations: overriding VRaptor's behavior and conventions</a></li>
		
									<li><a class="link_toc" href="../../documentation/testing-components-and-controllers/">13. Testing components and controllers</a></li>
		
									<li><a class="link_toc" href="../../documentation/changelog/">14. ChangeLog</a></li>
		
									<li><a class="link_toc" href="../../documentation/migrating-from-vraptor2-to-vraptor3/">15. Migrating from VRaptor2 to VRaptor3</a></li>
		
                </ol>
            </div><!-- submenu-->
                        
            <div id="textoCapitulo">
	
		<h2 class="chapter">VRaptor3 - Ten minutes guide</h2>

		


<h3 class="section">Starting a project: a on-line store</h3>
	    	<span class="paragraph">Let's start by downloading the <em class="italic">vraptor-blank-project</em> from 
<a class="link" target="_blank" href="http://vraptor.caelum.com.br/download.html.">http://vraptor.caelum.com.br/download.html.</a> This blank-project has the required configuration on 
<code class="inlineCode">web.xml</code> and the dependencies on <code class="inlineCode">WEB-INF/lib</code> that are needed to start using VRaptor. You 
can also import this project on Eclipse.</span>
	    	<span class="paragraph">We need to change the base package using this config at <code class="inlineCode">web.xml</code>:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.packages</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<strong><span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">com.companyname.projectname</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span></strong>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div>
	    	<span class="paragraph">On the example, all classes from your application <strong class="definition">must</strong> be in a subpackage of 
<code class="inlineCode">com.companyname.projectname</code>, so VRaptor can find your components and manage your dependencies. 
In this example project the base package will be:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.packages</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<strong><span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">br.com.caelum.onlinestore</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span></strong>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div>
	    	<span class="paragraph">Assuming that the context root of the application was changed to <code class="inlineCode">/onlinestore</code>, if you run
this example you should be able to access <code class="inlineCode"><a class="link" target="_blank" href="http://localhost:8080/onlinestore</code>">http://localhost:8080/onlinestore%%</a> and see
an <strong class="definition">It works!</strong> on screen.</span>
		

<h3 class="section">Product registry</h3>
	    	<span class="paragraph">Let's start the system with a products registry. We need a class that will represent the products,
and we'll use it to persist products on the database, with Hibernate:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Entity<br />
</span><span class="java4">public class </span><span class="java10">Product </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Id<br />
&nbsp;&nbsp;&nbsp; @GeneratedValue<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Long id;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">String name;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">String description;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Double price;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//getter and setters<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">We also need a class that will <em class="italic">control</em> the products' register, handling web requests.
This class will be the Products <code class="inlineCode">Controller</code>:</span>
	    	<div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
}</span></code></div>
	    	<span class="paragraph">ProductsController will expose URIs to be accessed through web, i.e, will expose resources of your
application. And for indicate it, you must annotate it with <code class="inlineCode">@Resource</code>:</span>
	    	<div class="java"><code class="java">
<strong><span class="java16">@Resource<br /></strong>
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
}</span></code></div>
	    	<span class="paragraph">By using this annotation, all public methods of the annotated class will be reachable through web.
For instance, if there is a <code class="inlineCode">list</code> method on the class:</span>
	    	<div class="java"><code class="java">
<strong><span class="java16">@Resource<br /></strong>
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; list</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">ArrayList&lt;Product&gt;</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Then, VRaptor will automatically redirect all requests to the URI <code class="inlineCode">/products/list</code> to this method.
The convention for URIs is: <code class="inlineCode">/&lt;controller_name&gt;/&lt;method_name&gt;</code>.</span>
	    	<span class="paragraph">At the end of method execution, VRaptor will <code class="inlineCode">dispatch</code> the request to the jsp at
<code class="inlineCode">/WEB-INF/jsp/products/list.jsp</code>. The convention for the default view is
<code class="inlineCode">/WEB-INF/jsp/&lt;controller_name&gt;/&lt;method_name&gt;.jsp</code>.</span>
	    	<span class="paragraph">The <code class="inlineCode">list</code> method will return a product list, so how can I get it on jsp? On VRaptor, 
the method return value will be exported to the jsp by request attributes. In this case,
the name of the exported attribute will be <code class="inlineCode">productList</code>, holding the method returned value:</span>
	    	<span class="paragraph">list.jsp</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;ul&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;c:forEach</span>&nbsp;<span class="texattrib">items=</span><span class="texvalue">"${productList}"</span>&nbsp;<span class="texattrib">var=</span><span class="texvalue">"product"</span><span class="textag">&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;li&gt;</span><span class="texnormal">&nbsp;${product.name}&nbsp;-&nbsp;${product.description}&nbsp;</span><span class="textag">&lt;/li&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/c:forEach&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/ul&gt;</span></code></div>
	    	<span class="paragraph">The convention for the attribute names is pretty intuitive: if it is a collection, as it is the case,
the name will be <code class="inlineCode">&lt;collection_type&gt;List</code>; if it is any other type, the name will be the class name
with the first letter in lowercase, i.e, if the type is <code class="inlineCode">Product</code>, the name will be <code class="inlineCode">product</code>.</span>
		

<h3 class="section">Creating ProductDao: Dependency Injection</h3>
	    	<span class="paragraph">VRaptor widely uses the Dependency Injection and Inversion of Control concept.
The whole idea is simple: if you need a resource, you won't create it, but will have it ready for 
you when you ask for it. You can get more information about it on the Dependency Injection chapter.</span>
	    	<span class="paragraph">We are returning a hard coded empty list on our list method. It would be more helpful if we
return a real list, for example all of registered products of the system. In order to do that,
let's create a product DAO, for listing the products:</span>
	    	<div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">ProductDao </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; listAll</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">ArrayList&lt;Product&gt;</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
	    	<span class="paragraph">And in the ProductsController we might use the dao for listing products:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">ProductDao dao;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; list</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">dao.listAll</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
	    	<span class="paragraph">We could create a new ProductDao inside the controller, but we can simply loose coupling by
receiving it on the class constructor, and letting VRaptor do its Dependency Management Magic
and provide an instance of ProductDao when creating our controller! And for enabling this
behavior we only have to annotate the ProductDao class with @Component:</span>
	    	<div class="java"><code class="java">
<strong><span class="java16">@Component<br /></strong>
</span><span class="java4">public class </span><span class="java10">ProductDao </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
</span><span class="java8">}<br />
<br />
</span><span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">ProductDao dao;<br />
<br />
<strong>&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ProductsController</span><span class="java8">(</span><span class="java10">ProductDao dao</span><span class="java8">) {<br /></strong>
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br /></strong>
<strong>&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br /></strong>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; list</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">dao.listAll</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
		

<h3 class="section">Add form: redirecting the request</h3>
	    	<span class="paragraph">We have a Products listing, but no way to register products. Thus, let's create a
form for adding products. Since it is not a good idea to access the jsps directly,
let's create an empty method that only redirects to a jsp:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">form</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; }<br />
}</span></code></div>
	    	<span class="paragraph">So we can access the form by URI <code class="inlineCode">/products/form</code>, and the form will be at
<code class="inlineCode">/WEB-INF/jsp/products/form.jsp</code>:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;form</span>&nbsp;<span class="texattrib">action=</span><span class="texvalue">"&lt;c:url&nbsp;value="</span>/products/adiciona"<span class="textag">/&gt;</span><span class="texnormal">"&gt;<br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;input</span>&nbsp;<span class="texattrib">type=</span><span class="texvalue">"text"</span>&nbsp;<span class="texattrib">name=</span><span class="texvalue">"product.name"</span>&nbsp;<span class="textag">/&gt;</span><span class="texnormal"><span class="textag">&lt;br/&gt;</span><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;Description:</span><span class="textag">&lt;input</span>&nbsp;<span class="texattrib">type=</span><span class="texvalue">"text"</span>&nbsp;<span class="texattrib">name=</span><span class="texvalue">"product.description"</span>&nbsp;<span class="textag">/&gt;</span><span class="texnormal"><span class="textag">&lt;br/&gt;</span><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;Price:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;input</span>&nbsp;<span class="texattrib">type=</span><span class="texvalue">"text"</span>&nbsp;<span class="texattrib">name=</span><span class="texvalue">"product.price"</span>&nbsp;<span class="textag">/&gt;</span><span class="texnormal"><span class="textag">&lt;br/&gt;</span><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;input</span>&nbsp;<span class="texattrib">type=</span><span class="texvalue">"submit"</span>&nbsp;<span class="texattrib">value=</span><span class="texvalue">"Save"</span>&nbsp;<span class="textag">/&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/form&gt;</span></code></div>
	    	<span class="paragraph">This form will save a product using the URI <code class="inlineCode">/products/add</code>, so we must create
this method on the controller:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; }<br />
}</span></code></div>
	    	<span class="paragraph">Look at the input names: <strong class="definition">product.name</strong>, <strong class="definition">product.description</strong> and <strong class="definition">product.price</strong>.
If we receive a <code class="inlineCode">Product</code> named <code class="inlineCode">product</code> as parameter on add method, VRaptor will 
set the fields <strong class="definition">name</strong>, <strong class="definition">description</strong> and <strong class="definition">price</strong> with the input values, using
the corresponding setters on Product. The <strong class="definition">product.price</strong> parameter will also be converted
into Double before being set on the product. More information on Converters chapter.</span>
	    	<span class="paragraph">Thus, having the correct names on the form inputs, we can create the add method:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">dao.save</span><span class="java8">(</span><span class="java10">product</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Right after saving something on a form we usually want to be redirected to the listing
or back to the form. In this case we want to be redirected to the products listing.
For this purpose there is a VRaptor component: the <code class="inlineCode">Result</code>. It is responsible for 
adding attributes on the request, and for dispatching to a different view. To get a Result
instance you must receive it as a constructor parameter:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ProductsController</span><span class="java8">(</span><span class="java10">ProductDao dao, Result result</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result = result;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">In order to redirect to the listing, you can use the result object:</span>
	    	<div class="java"><code class="java">
<span class="java10">result.use</span><span class="java8">(</span><span class="java10">Results.logic</span><span class="java8">())</span><span class="java10">.redirectTo</span><span class="java8">(</span><span class="java10">ProductsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;</span></code></div>
	    	<span class="paragraph">This code snippet can be read as: <em class="italic">As a result, use a logic, redirecting to the 
list method in ProductsController</em>. All redirect configuration is 100% java code, 
with no strings involved! It's clear from the code that the result from your logic
is not the default, and which one you're using. There is no need to worry about 
configuration files. Furthermore, if you need to rename the <code class="inlineCode">list</code> method, there
is no need to go through your entire application looking for redirects to this method,
just use your usual refactoring IDE to do the rename.</span>
	    	<span class="paragraph">Our <code class="inlineCode">add</code> method would look like this:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">dao.add</span><span class="java8">(</span><span class="java10">product</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; result.use</span><span class="java8">(</span><span class="java10">Results.logic</span><span class="java8">())</span><span class="java10">.redirectTo</span><span class="java8">(</span><span class="java10">ProductsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">You can get more info on <code class="inlineCode">Result</code> at the Views and Ajax chapter.</span>
		

<h3 class="section">Validation</h3>
	    	<span class="paragraph">It wouldn't make sense adding a nameless product in the system, nor a negative value for it's price.
Before adding the product, we need to check if it is a valid product - which has a name and a positive price. In case it's not valid, we want to get back to the form and show error messages.</span>
	    	<span class="paragraph">In order to do that, we can use a VRaptor component: the Validator. You can get it in your Controller's constructor and use it like this:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ProductsController</span><span class="java8">(</span><span class="java10">ProductDao dao, Result result, Validator validator</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.validator = validator;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {<br />
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">() {{<br /></strong>
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!product.getName</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;product.name&#34;</span><span class="java10">, </span><span class="java5">&#34;nome.empty&#34;</span><span class="java8">)</span><span class="java10">;<br /></strong>
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; that</span><span class="java8">(</span><span class="java10">product.getPrice</span><span class="java8">() </span><span class="java10">&gt; </span><span class="java7">0</span><span class="java10">, </span><span class="java5">&#34;product.price&#34;</span><span class="java10">, </span><span class="java5">&#34;price.invalid&#34;</span><span class="java8">)</span><span class="java10">;<br /></strong>
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br /></strong>
<strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">Results.page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">ProductsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.form</span><span class="java8">()</span><span class="java10">;<br /></strong>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">product</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; result.use</span><span class="java8">(</span><span class="java10">Results.logic</span><span class="java8">())</span><span class="java10">.redirectTo</span><span class="java8">(</span><span class="java10">ProductsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">we can read the validation code as <em class="italic">Validate that the name of the product is not empty and that
the product's price is bigger than zero. If an error occur, use the ProductsController form page as 
the result</em>. Therefore, if the product name is empty, the "name.empty" internationalized message will be added to the "product.name" field. If any error occurs, the system will get the user back to the form page, with all fields set, and error messages that can be accessed like this:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;c:forEach</span>&nbsp;<span class="texattrib">var=</span><span class="texvalue">"error"</span>&nbsp;<span class="texattrib">items=</span><span class="texvalue">"${errors}"</span><span class="textag">&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;${error.category}&nbsp;&nbsp;${error.message}</span><span class="textag">&lt;br</span>&nbsp;<span class="textag">/&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/c:forEach&gt;</span></code></div>
	    	<span class="paragraph">More information on Validation on, well, Validations chapter.</span>
	    	<span class="paragraph">If you learnt what we said so far, you're able to make 90% of your application. Next sessions on 
this tutorial show the solution for some of the most frequent problems that lay on that 10% left.</span>
	    	<span class="paragraph">[sectin Using Hibernate to store Products]</span>
	    	<span class="paragraph">Let's make a real implementation of ProductDao, now, using Hibernate to persist products. You'll 
need a Session in your ProductDao. Using injection of dependencies, you'll have to declare you'll 
receive a Session in your constructor.</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">ProductDao </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">Session session;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ProductDao</span><span class="java8">(</span><span class="java10">Session session</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session = session;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">session.save</span><span class="java8">(</span><span class="java10">product</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">But, wait, for VRaptor to know how to create that Session, and I can't simply put a @Component on 
the Session class because it is a Hibernate class. That's the reason why the ComponentFactory 
interface was created. More info on creating your own ComponentFactories can be found in Components 
chapter. You can also use the ComponentFactories available in VRaptor, as shown in the Utils 
chapter.</span>
		

<h3 class="section">Controlling transactions: Interceptors</h3>
	    	<span class="paragraph">We often want to intercept as requests (or some of them) and execute a business logic, such as in a 
transaction control. That why VRaptor has interceptors. Learn more about them on the Interceptors' 
chapter. There is also an implemented TransactionInterceptor in VRaptor - learn how to use it on the Utils chapter.</span>
		

<h3 class="section">Shopping Cart: session components</h3>
	    	<span class="paragraph">If we want to make a shopping cart in our system, we need some way to keep cart items in the user's 
session. In order to do it, we can create a session scoped component, i.e., a component that will 
last as long as the user session last. For that, simply create a component and annotate it with 
@SessionScoped:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
@SessionScoped<br />
</span><span class="java4">public class </span><span class="java10">ShoppingCart </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">List&lt;Product&gt; items = </span><span class="java4">new </span><span class="java10">ArrayList&lt;Product&gt;</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; getAllItems</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">items;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">addItem</span><span class="java8">(</span><span class="java10">Product item</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">items.add</span><span class="java8">(</span><span class="java10">item</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">As this shopping cart is a component, we can receive it on the shopping cart's Controller's 
constructor:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ShoppingCartController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ShoppingCartController</span><span class="java8">(</span><span class="java10">ShoppingCart cart</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.cart = cart;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">cart.addItem</span><span class="java8">(</span><span class="java10">product</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; listItems</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">cart.getAllItems</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Besides session scope, there is also the application scope and the @ApplicationScoped annotation.
Components annotated with @ApplicationScoped will be created only once for the whole application.</span>
		

<h3 class="section">A bit of REST</h3>
	    	<span class="paragraph">On REST's ideal of URIs identifying resources on the web to make good use of the structural 
advantages the HTTP protocol provides us, observe how simple it is, in VRaptor, mapping the 
different HTTP methods in the same URI to invoke different Controllers' methods. Suppose we 
want to use the following URIs on the products' crud:</span>
	    	<div class="java"><code class="java">GET&nbsp;/products&nbsp;-&nbsp;lista&nbsp;todos&nbsp;os&nbsp;products<br />
POST&nbsp;/products&nbsp;-&nbsp;adiciona&nbsp;um&nbsp;product<br />
GET&nbsp;/products/{id}&nbsp;-&nbsp;visualiza&nbsp;o&nbsp;product&nbsp;com&nbsp;o&nbsp;id&nbsp;passado<br />
PUT&nbsp;/products/{id}&nbsp;-&nbsp;atualiza&nbsp;as&nbsp;informa&ccedil;&otilde;es&nbsp;do&nbsp;product&nbsp;com&nbsp;o&nbsp;id&nbsp;passado<br />
DELETE&nbsp;/products/{id}&nbsp;-&nbsp;remove&nbsp;o&nbsp;product&nbsp;com&nbsp;o&nbsp;id&nbsp;passado</code></div>
	    	<span class="paragraph">In order to create a REST behaviour in VRaptor, we can use the @Path annotations - that changes
the URI to access a given method. Also, we use the annotations that indicate which HTTP methods 
are allowed to call that logic - @Get, @Post, @Delete and @Put.</span>
	    	<span class="paragraph">A REST version of our <code class="inlineCode">ProductsControler</code> would be something like that:</span>
	    	<div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">ProductsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//...<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Get<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/products&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Product&gt; list</span><span class="java8">() {</span><span class="java10">...</span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Post<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/products&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {</span><span class="java10">...</span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Get<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/products/{product.id}&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">view</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {</span><span class="java10">...</span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Put<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/products/{product.id}&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">update</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {</span><span class="java10">...</span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Delete<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/products/{product.id}&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">remove</span><span class="java8">(</span><span class="java10">Product product</span><span class="java8">) {</span><span class="java10">...</span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
	    	<span class="paragraph">Note we can receive parameters on the URIs. For instance, if we can the <strong class="definition">GET /products/5</strong> URI, 
the <code class="inlineCode">view</code> method will be invoked and the <code class="inlineCode">product</code> parameter will have its id set as <code class="inlineCode">5</code>.</span>
	    	<span class="paragraph">More info on that are on the REST Resources chapter.</span>
		

<h3 class="section">Message bundle File</h3>
	    	<span class="paragraph">Internationalization (i18n) is a powerful feature present in almost all Web frameworks 
nowadays. And it’s no different with VRaptor3. With i18n you can make your applications 
support several different languages (such as French, Portuguese, Spanish, English, etc) 
in a very easy way: simply translating the application messages.</span>
	    	<span class="paragraph">In order to support i18n, you must create a file called <code class="inlineCode">messages.properties</code> and make 
it available in your application classpath (<code class="inlineCode">WEB-INF/classes</code>). That file contains 
lines which are a set of key/value entries, for example:</span>
	    	<div class="java"><code class="java">field.userName&nbsp;<span class="texvalue">=&nbsp;Username&nbsp;<br /></span>
field.password&nbsp;<span class="texvalue">=&nbsp;Password</span></code></div>
	    	<span class="paragraph">So far, it’s easy, but what if you want to create files containing messages in other 
languages, for example, Portuguese? Also easy. You just need to create another properties 
file called <code class="inlineCode">messages_pt_BR.properties</code>. Notice the suffix <em class="italic">_pt_BR</em> on the file name. 
It indicates that when the user access your application from his computer configured with 
Brazilian Portuguese locale, the messages in this file will be used. The file contents 
would be:</span>
	    	<div class="java"><code class="java">field.userName&nbsp;<span class="texvalue">=&nbsp;Nome&nbsp;do&nbsp;Usu&aacute;rio<br /></span>
field.password&nbsp;<span class="texvalue">=&nbsp;Senha</span></code></div>
	    	<span class="paragraph">Notice that the keys are the same in both files, what changes is the value to the specific 
language.</span>
	    	<span class="paragraph">In order to use those messages in your JSP files, you could use JSTL. The code would go 
as follows:</span>
	    	<div class="java"><code class="java">&lt;html&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&lt;body&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;fmt:message&nbsp;key="field.userName"&nbsp;/&gt;&nbsp;&lt;input&nbsp;name="user.userName"&nbsp;/&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;br&nbsp;/&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;fmt:message&nbsp;key="field.password"&nbsp;/&gt;&nbsp;&lt;input&nbsp;type="password"&nbsp;name="user.password"&nbsp;/&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;input&nbsp;type="submit"&nbsp;/&gt;&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/body&gt;&nbsp;<br />
&lt;/html&gt;</code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>