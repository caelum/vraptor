
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
	
		<h2 class="chapter">Testing components and controllers</h2>

		<span class="paragraph">VRaptor3 manages your class dependencies, so there is no need to worry about
instantiating your components and controllers, you can just receive your dependencies
on the constructor and VRaptor3 will locate them and instantiate your class.</span><span class="paragraph">You can take advantage of dependency injection when testing your classes:
you can instantiate your class with fake implementations and unit test the class.</span><span class="paragraph">Nevertheless, there are two VRaptor3 components that are dependencies of most of your 
controllers: <code class="inlineCode">Result</code> and <code class="inlineCode">Validator</code>. Their fluent interfaces makes it difficult to
create fake implementations or mocks. Therefore there are fake implementations for
these components on VRaptor3: <code class="inlineCode">MockResult</code> e <code class="inlineCode">MockValidator</code>.</span>


<h3 class="section">MockResult</h3>
	    	<span class="paragraph">MockResult ignores all redirects, and stores the included objects, so you can inspect
them and make assertions.</span>
	    	<span class="paragraph">This snippet shows you how you can use MockResult:</span>
	    	<div class="java"><code class="java">
<span class="java10">MockResult result = </span><span class="java4">new </span><span class="java10">MockResult</span><span class="java8">()</span><span class="java10">;<br />
ClientController controller = </span><span class="java4">new </span><span class="java10">ClientController</span><span class="java8">(</span><span class="java10">..., result</span><span class="java8">)</span><span class="java10">;<br />
controller.list</span><span class="java8">()</span><span class="java10">; </span><span class="java3">// will call result.include(&#34;clients&#34;, something);<br />
</span><span class="java10">List&lt;Client&gt; clients = result.included</span><span class="java8">(</span><span class="java5">&#34;clients&#34;</span><span class="java8">)</span><span class="java10">; </span><span class="java3">// the cast is implicit<br />
</span><span class="java10">Assert.assertNotNull</span><span class="java8">(</span><span class="java10">clients</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java3">// more assertions</span></code></div>
	    	<span class="paragraph">Any calls to result.use(...) will be ignored.</span>
		

<h3 class="section">MockValidator</h3>
	    	<span class="paragraph">MockValidator will store generated errors, so if there is any error when 
validator.onErrorUse is called, a ValidationError will be thrown. Therefore
you can inspect the added errors, or simply check if there is any error.</span>
	    	<div class="java"><code class="java">
<span class="java16">@Test</span><span class="java8">(</span><span class="java10">expected=ValidationException.</span><span class="java4">class</span><span class="java8">)<br />
</span><span class="java4">public </span><span class="java9">void </span><span class="java10">testThatAValidationErrorOccurs</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">ClientController controller = </span><span class="java4">new </span><span class="java10">ClientController</span><span class="java8">(</span><span class="java10">..., </span><span class="java4">new </span><span class="java10">MockValidator</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; controller.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Client</span><span class="java8">())</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">ou</span>
	    	<div class="java"><code class="java">
<span class="java16">@Test<br />
</span><span class="java4">public </span><span class="java9">void </span><span class="java10">testThatAValidationErrorOccurs</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">ClientController controller = </span><span class="java4">new </span><span class="java10">ClientController</span><span class="java8">(</span><span class="java10">..., </span><span class="java4">new </span><span class="java10">MockValidator</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">try </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">controller.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Cliente</span><span class="java8">())</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Assert.fail</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">} </span><span class="java4">catch </span><span class="java8">(</span><span class="java10">ValidationException e</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">List&lt;Message&gt; errors = e.getErrors</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">//assertions on errors<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>