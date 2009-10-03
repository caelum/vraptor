
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
	
		<h2 class="chapter">Interceptors</h2>

		


<h3 class="section">Why intercept</h3>
	    	<span class="paragraph">Interceptors are implemented in order to execute tasks before and/or after the execution
of a business logic, being data validation, database connection and transaction control, logging and data cryptography/compression the most common use cases.</span>
		

<h3 class="section">How to intercept</h3>
	    	<span class="paragraph">In VRaptor 3 we adopted an approach in which the interceptor defines who will be intercepted. This is closer to the intercepting style used in systems based on AOP (Aspect Oriented Programming) than the one that was implemented in VRaptor's previous version.</span>
	    	<span class="paragraph">Therefore, to intercept a request, just implement the <strong class="definition">Interceptor</strong> interface and annotate
the class with <strong class="definition">@Intercepts</strong>.</span>
	    	<span class="paragraph">Just like any other component, you can specify the interceptor's scope using the scope annotations.</span>
	    	<div class="java"><code class="java">
<span class="java4">public interface </span><span class="java10">Interceptor </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java9">void </span><span class="java10">intercept</span><span class="java8">(</span><span class="java10">InterceptorStack stack, ResourceMethod method, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Object resourceInstance</span><span class="java8">) </span><span class="java4">throws </span><span class="java10">InterceptionException;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java9">boolean </span><span class="java10">accepts</span><span class="java8">(</span><span class="java10">ResourceMethod method</span><span class="java8">)</span><span class="java10">;<br />
<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">Simple example</h3>
	    	<span class="paragraph">The following class shows an example of how to intercept all requests using session scope and simply print the invocation to default output.</span>
	    	<span class="paragraph">Remember that the interceptor is a component just like any other, so it can receive its
dependencies in the constructor through Dependency Injection.</span>
	    	<div class="java"><code class="java">
<span class="java16">@Intercepts<br />
@SessionScoped<br />
</span><span class="java4">public class </span><span class="java10">Log </span><span class="java4">implements </span><span class="java10">Interceptor </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">HttpServletRequest request;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Log</span><span class="java8">(</span><span class="java10">HttpServletRequest request</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.request = request;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">intercept</span><span class="java8">(</span><span class="java10">InterceptorStack stack, ResourceMethod method, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Object resourceInstance</span><span class="java8">) </span><span class="java4">throws </span><span class="java10">InterceptionException </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">System.out.println</span><span class="java8">(</span><span class="java5">&#34;Intercepting &#34; </span><span class="java10">+ request.getRequestURI</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; stack.next</span><span class="java8">(</span><span class="java10">method, resourceInstance</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">boolean </span><span class="java10">accepts</span><span class="java8">(</span><span class="java10">ResourceMethod method</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return true</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
}</span></code></div>
		

<h3 class="section">Example using Hibernate</h3>
	    	<span class="paragraph">Probably one of the most common uses of an Interceptor is to implement the Open Session In View pattern,
which provides a database connection whenever a request is made to your application.
And in the end of that request, the connection is disposed.
This is specially useful to avoid exceptions like LazyInitializationException when rendering JSPs.</span>
	    	<span class="paragraph">Here is a simple example that starts a database transaction in every request, and when
the logic execution ends and the page is rendered, it commits the transaction and 
closes the database connection.</span>
	    	<div class="java"><code class="java">
<span class="java16">@RequestScoped<br />
@Intercepts<br />
</span><span class="java4">public class </span><span class="java10">DatabaseInterceptor </span><span class="java4">implements </span><span class="java10">br.com.caelum.vraptor.Interceptor </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">Database controller;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">Result result;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">HttpServletRequest request;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">DatabaseInterceptor</span><span class="java8">(</span><span class="java10">Database controller, Result result, HttpServletRequest request</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.controller = controller;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result = result;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.request = request;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">intercept</span><span class="java8">(</span><span class="java10">InterceptorStack stack, ResourceMethod method, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Object instance</span><span class="java8">) </span><span class="java4">throws </span><span class="java10">InterceptionException </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">result.include</span><span class="java8">(</span><span class="java5">&#34;contextPath&#34;</span><span class="java10">, request.getContextPath</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">try </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.beginTransaction</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; stack.next</span><span class="java8">(</span><span class="java10">method, instance</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; controller.commit</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java8">} </span><span class="java4">finally </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">if </span><span class="java8">(</span><span class="java10">controller.hasTransaction</span><span class="java8">()) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.rollback</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.close</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; }<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">boolean </span><span class="java10">accepts</span><span class="java8">(</span><span class="java10">ResourceMethod method</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return true</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
}</span></code></div>
	    	<span class="paragraph">This way, to use the available connection in your Resource, the following code would apply:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">EmployeeController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">EmployeeController</span><span class="java8">(</span><span class="java10">Result result, Database controller</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.result = result;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.controller = controller;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Post<br />
&nbsp;&nbsp;&nbsp; @Path</span><span class="java8">(</span><span class="java5">&#34;/employee&#34;</span><span class="java8">)<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.getEmployeeDao</span><span class="java8">()</span><span class="java10">.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ...<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">How to ensure ordering: InterceptorSequence</h3>
	    	<span class="paragraph">If you want to ensure order of the execution of a set of interceptors, you can implement
the interface InterceptorSequence and return the order you want to execute the interceptors:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Intercepts<br />
</span><span class="java4">public class </span><span class="java10">MySequence </span><span class="java4">implements </span><span class="java10">InterceptorSequence </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Class&lt;? </span><span class="java4">extends </span><span class="java10">Interceptor&gt;</span><span class="java8">[] </span><span class="java10">getSequence</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">Class</span><span class="java8">[] { </span><span class="java10">FirstInterceptor.class, SecondInterceptor.</span><span class="java4">class </span><span class="java8">}</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">You should not annotate the interceptors returned by InterceptorSequence.getSequence() with @Intercepts.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>