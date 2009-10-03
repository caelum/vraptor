
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
	
		<h2 class="chapter">Advanced configurations: overriding VRaptor's behavior and conventions</h2>

		


<h3 class="section">Changing the default rendered view</h3>
	    	<span class="paragraph">If you need to change the default rendered view, or change the place where it'll be look for,
you'll only need to create the following class:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">CustomPathResolver </span><span class="java4">extends </span><span class="java10">DefaultPathResolver </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getPrefix</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;/root/directory/&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String getExtension</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java5">&#34;ftl&#34;</span><span class="java10">; </span><span class="java3">// or any other extension<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String extractControllerFromName</span><span class="java8">(</span><span class="java10">String baseName</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//your convention here<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //ex.: If you want to redirect UserController to 'userResource' instead of 'user'<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //ex.2: If you override the convention for Controllers name to XXXResource<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; //and still want to redirect to 'user' and not to 'userResource'<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">If you need a more complex convention, just implement the PathResolver interface.</span>
		

<h3 class="section">Mudando a URI padrão</h3>
	    	<span class="paragraph">The default URI for ClientsController.list() is /clients/list, i.e, controller_name/method_name.
If you want to override this convention, you can create a class like:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
@ApplicationScoped<br />
</span><span class="java4">public class </span><span class="java10">MyRoutesParser </span><span class="java4">extends </span><span class="java10">PathAnnotationRoutesParser </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//delegate constructor<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String extractControllerNameFrom</span><span class="java8">(</span><span class="java10">Class&lt;?&gt; type</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//your convention here<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java10">String defaultUriFor</span><span class="java8">(</span><span class="java10">String controllerName, String methodName</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java3">//your convention here<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">If you need a more complex convention, just implement the RoutesParser interface.</span>
		

<h3 class="section">Changing IoC provider</h3>
	    	<span class="paragraph">VRaptor's default IoC provider is Spring. In order to change, just add the following to your
web.xml:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.provider</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">com.package.from.your.PrefferedProviderClass</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div>
	    	<span class="paragraph">VRaptor comes with built-in support to both Spring (br.com.caelum.vraptor.ioc.spring.SpringProvider) and PicoContainer
(br.com.caelum.vraptor.ioc.pico.PicoProvider).
You still have the option to extend any of these classes and use your own provider.</span>
		

<h3 class="section">Changing Spring's base ApplicationContext</h3>
	    	<span class="paragraph">If VRaptor isn't using your ApplicationContext as base, just extend SpringProvider and implement the
getParentApplicationContext method, giving it your application's ApplicationContext:</span>
	    	<div class="java"><code class="java">
<span class="java4">package </span><span class="java10">br.com.apackage.aproject;<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ApplicationContext getParentApplicationContext</span><span class="java8">(</span><span class="java10">ServletContext context</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">ApplicationContext applicationContext = </span><span class="java3">//your own logic to create your applicationContext<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">applicationContext;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">and change the provider at your web.xml:</span>
	    	<div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.provider</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">br.com.apackage.aproject.CustomProvider</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div>
	    	<span class="paragraph">By default, VRaptor try to find the applicationContext via
<code class="inlineCode">WebApplicationContextUtils.getWebApplicationContext(servletContext);</code> or loading the applicationContext.xml
that is on your classpath.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>