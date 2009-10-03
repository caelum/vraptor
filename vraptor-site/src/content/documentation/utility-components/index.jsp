
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
	
		<h2 class="chapter">Utility Components</h2>

		


<h3 class="section">Registering optional components</h3>
	    	<span class="paragraph">VRaptor have some optional components, on package br.com.caelum.vraptor.util.
For registering them you can do as follows:</span>
	    	<ul class="list"><li><span class="paragraph">Create a child class of your DI Profile (Spring is the default):</span><div class="java"><code class="java">
<span class="java4">package </span><span class="java10">com.companyname.projectName;<br />
<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div></li><li><span class="paragraph">Register this class as your DI provider on web.xml:</span><div class="xml"><code class="xml"><span class="textag">&lt;context-param&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-name&gt;</span><span class="texnormal">br.com.caelum.vraptor.provider</span><span class="textag">&lt;/param-name&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="textag">&lt;param-value&gt;</span><span class="texnormal">com.companyname.projectName.CustomProvider</span><span class="textag">&lt;/param-value&gt;</span><span class="texnormal"><br /></span>
<span class="texnormal"></span><span class="textag">&lt;/context-param&gt;</span></code></div></li><li><span class="paragraph">Override the registerCustomComponents method and add your optional components:</span><div class="java"><code class="java">
<span class="java4">package </span><span class="java10">com.companyname.projectName;<br />
<br />
</span><span class="java4">public class </span><span class="java10">CustomProvider </span><span class="java4">extends </span><span class="java10">SpringProvider </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Override<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">OptionalComponent.class, OptionalComponent.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div></li></ul>
		

<h3 class="section">Hibernate Session and SessionFactory</h3>
	    	<span class="paragraph">If your components need Hibernate Session and SessionFactory, you will need
a ComponentFactory to create them for you. If you use annotated entities, and
you have a hibernate.cfg.xml in the root of WEB-INF/classes, you can use VRaptor's
built-in ComponentFactory. All you have to do is:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">SessionCreator.class, SessionCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; registry.registry</span><span class="java8">(</span><span class="java10">SessionFactoryCreator.class, SessionFactoryCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">You can also enable the interceptor that manages Hibernate transactions:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">HibernateTransactionInterceptor.class,<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; HibernateTransactionInterceptor.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">JPA EntityManager e EntityManagerFactory</h3>
	    	<span class="paragraph">If you have a persistence.xml with the persistence-unit called "default", you can use 
VRaptor3 built-in ComponentFactories for EntityManager and EntityManagerFactory:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">EntityManagerCreator.class, EntityManagerCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; registry.registry</span><span class="java8">(</span><span class="java10">EntityManagerFactoryCreator.class, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; EntityManagerFactoryCreator.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">You can also enable the interceptor that manages JPA transactions:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Override<br />
</span><span class="java4">protected </span><span class="java9">void </span><span class="java10">registerCustomComponents</span><span class="java8">(</span><span class="java10">ComponentRegistry registry</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">registry.registry</span><span class="java8">(</span><span class="java10">JPATransactionInterceptor.class, <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; JPATransactionInterceptor.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>