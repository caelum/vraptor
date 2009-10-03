
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
	
		<h2 class="chapter">ChangeLog</h2>

		<ul class="list"><li><span class="paragraph">ValidationError renamed to ValidationException</span></li><li><span class="paragraph">result.use(Results.http()) for setting headers and status codes of HTTP protocol</span></li><li><span class="paragraph">bug fixes</span></li><li><span class="paragraph">documentation</span></li><li><span class="paragraph">new site</span></li></ul>


<h3 class="section">3.0.0-rc-1</h3>
	    	<ul class="list"><li><span class="paragraph">example application: mydvds</span></li><li><span class="paragraph">new way to add options components into VRaptor:</span><div class="java"><code class="java">
<span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">OptionComponent.class, OptionComponent.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div></li><li><span class="paragraph">Utils: HibernateTransactionInterceptor and JPATransactionInterceptor</span></li><li><span class="paragraph">Full application example inside the docs</span></li><li><span class="paragraph">English docs</span></li></ul>
		

<h3 class="section">3.0.0-beta-5</h3>
	    	<ul class="list"><li><span class="paragraph">New way to do validations:</span><div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">visualiza</span><span class="java8">(</span><span class="java10">Client client</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">() {{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">client.getId</span><span class="java8">() </span><span class="java10">!= null, </span><span class="java5">&#34;id&#34;</span><span class="java10">, </span><span class="java5">&#34;id.should.be.filled&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">ClientsController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.list</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//continua o metodo<br />
</span><span class="java8">}</span></code></div></li><li><span class="paragraph">UploadedFile.getFile() now returns InputStream.</span></li><li><span class="paragraph">EntityManagerCreator and EntityManagerFactoryCreator</span></li><li><span class="paragraph">bugfixes</span></li></ul>
		

<h3 class="section">3.0.0-beta-4</h3>
	    	<ul class="list"><li><span class="paragraph">New result: result.use(page()).of(MyController.class).myLogic() renders the default view
	(/WEB-INF/jsp/meu/myLogica.jsp) without execting the logic.</span></li><li><span class="paragraph">Mock classes for testing: MockResult e MockValidator, to make easier to unit test your logics.
	They ignores the fluent interface calls and keep the parameters included under the result and
	the validation errors.</span></li><li><span class="paragraph">The URIs passed to result.use(page()).forward(uri) and result.use(page()).redirect(uri)
	can't be logic URIs. Use forwards or redirects from result.use(logic()) instead.</span></li><li><span class="paragraph">Parameters passed to URI's now accepts pattern-matching:</span><ul class="list"><li><span class="paragraph">Automatic: if we have the URI /clients/{client.id} and client.id is a Long, the {client.id} parameter 
		will only match numbers, so, the URI /clients/42 matches, but the /clients/random doesn't matches.
		This works for all numeric types, booleans and enums. VRaptor will restrict the possible values.</span></li><li><span class="paragraph">Manual: in your CustomRoutes you can do:
		routeFor("/clients/{client.id}").withParameter("client.id").matching("\\d{1,4}")
  			.is(ClienteController.class).mostra(null);
  	which means you can restrict values for the parameters you want by regexes at the matching method.</span></li></ul></li><li><span class="paragraph">Converters for joda-times's LocalDate and LocalTime comes by default.</span></li><li><span class="paragraph">When Spring is the IoC provider, VRaptor tries to find your application's spring to use as
	a father container. This search is made by one of the following two ways:</span><ul class="list"><li><span class="paragraph">WebApplicationContextUtils.getWebApplicationContext(servletContext), when you have Spring's listeners
	configured.</span></li><li><span class="paragraph">applicationContext.xml inside the classpath</span></li></ul><span class="paragraph">If it's not enough, you can implements the SpringLocator interface and enable the Spring's ApplicationContext
	used by your application.</span></li><li><span class="paragraph">Utils:</span><ul class="list"><li><span class="paragraph">SessionCreator and SessionFactoryCreator to create Hibernate's Session and SessionFactory 
	to your registered components.</span></li><li><span class="paragraph">EncodingInterceptor, to change you default encoding.</span></li></ul></li><li><span class="paragraph">several bugfixes and docs improvements.</span></li></ul>
		

<h3 class="section">3.0.0-beta-3</h3>
	    	<ul class="list"><li><span class="paragraph">Spring becomes the default IoC provider</span></li><li><span class="paragraph">the applicationContext.xml under the classpath is used as Spring initial configuration, if it exists.</span></li><li><span class="paragraph">improved docs at <a class="link" target="_blank" href="http://vraptor.caelum.com.br/documentacao">http://vraptor.caelum.com.br/documentacao</a></span></li><li><span class="paragraph">small bugfixes and optimizations</span></li></ul>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>