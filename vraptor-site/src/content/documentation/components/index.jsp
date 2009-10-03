
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
	
		<h2 class="chapter">Components</h2>

		


<h3 class="section">What are components?</h3>
	    	<span class="paragraph">Components are object instances that your application need to execute tasks or to
keep state in different situations.</span>
	    	<span class="paragraph">DAOs and e-mail senders are classic component examples.</span>
	    	<span class="paragraph">The best practices suggest you should <em class="italic">always</em> create interfaces for your components to implement.
This makes your code much easier to unit test.</span>
	    	<span class="paragraph">The following example shows a VRaptor-managed component:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">ClientDao </span><span class="java8">{<br />
 <br />
&nbsp; </span><span class="java4">private final </span><span class="java10">Session session;<br />
&nbsp; </span><span class="java4">public </span><span class="java10">ClientDao</span><span class="java8">(</span><span class="java10">HibernateControl control</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session = control.getSession</span><span class="java8">()<br />
&nbsp; }<br />
&nbsp; <br />
&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Client client</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">session.save</span><span class="java8">(</span><span class="java10">client</span><span class="java8">)</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
}</span></code></div>
		

<h3 class="section">Scopes</h3>
	    	<span class="paragraph">Just like resources, components live in specific scopes and follow the same rules.
The default scope for a component is the request scope, meaning that a new instance
will be created for each request.</span>
	    	<span class="paragraph">The following example shows a Hibernate-based connection provider.
The application scope is specified for the provider, so only one instance 
per application context will be created:</span>
	    	<div class="java"><code class="java">
<span class="java16">@ApplicationScoped<br />
@Component<br />
</span><span class="java4">public class </span><span class="java10">HibernateControl </span><span class="java8">{<br />
 <br />
&nbsp; </span><span class="java4">private final </span><span class="java10">SessionFactory factory;<br />
&nbsp; </span><span class="java4">public </span><span class="java10">HibernateControl</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.factory = </span><span class="java4">new </span><span class="java10">AnnotationConfiguration</span><span class="java8">()</span><span class="java10">.configure</span><span class="java8">()</span><span class="java10">.buildSessionFactory</span><span class="java8">()</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
&nbsp; </span><span class="java4">public </span><span class="java10">Session getSession</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">factory.openSession</span><span class="java8">()</span><span class="java10">;<br />
&nbsp; </span><span class="java8">}<br />
&nbsp; <br />
}</span></code></div>
		

<h3 class="section">ComponentFactory</h3>
	    	<span class="paragraph">It can happen that one of your class dependencies doesn't belong to your project, like 
the Session from Hibernate or EntityManager from JPA.</span>
	    	<span class="paragraph">In order to do that you can create a ComponentFactory:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Component<br />
</span><span class="java4">public class </span><span class="java10">SessionCreator </span><span class="java4">implements </span><span class="java10">ComponentFactory&lt;Session&gt; </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">SessionFactory factory;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">Session session;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">SessionCreator</span><span class="java8">(</span><span class="java10">SessionFactory factory</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.factory = factory;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@PostConstruct<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">create</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session = factory.openSession</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Session getInstance</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">session;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@PreDestroy<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">destroy</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.session.close</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
}</span></code></div>
	    	<span class="paragraph">Note that you can add listeners like @PostConstruct and @PreDestroy to manage creation
and destruction of you factory resources. You can use these listeners on any component
that you register on VRaptor.</span>
		

<h3 class="section">Dependency injection</h3>
	    	<span class="paragraph">VRaptor uses one of its own dependency injection providers to control what it needs 
in order to create new instances of your components and resources.</span>
	    	<span class="paragraph">For that reason, the former two examples allow any of your resources or components to
receive a ClientDao in its constructor. For example:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ClientController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">ClientDao dao;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ClientController</span><span class="java8">(</span><span class="java10">ClientDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java16">@Post<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Client client</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao.add</span><span class="java8">(</span><span class="java10">client</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>