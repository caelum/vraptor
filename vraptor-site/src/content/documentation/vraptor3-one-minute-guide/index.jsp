
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
	
		<h2 class="chapter">VRaptor3 - One minute guide</h2>

		<span class="paragraph">VRaptor 3 focuses in simplicity and, therefore, all of its functionalities have as main
goal solve the developer's problem in the less intrusive way.</span><span class="paragraph">Either CRUD operations or more complex functionalities such as download, upload, 
results in different formats (xml, json, xhtml etc), everything is done through VRaptor3's
simple and easy-to-understand functionalities. You don't have to deal directly with
<code class="inlineCode">HttpServletRequest</code>, <code class="inlineCode">Responses</code> or any <code class="inlineCode">javax.servlet</code> API, although you still
have the control of all Web operations.</span>


<h3 class="section">Starting up</h3>
	    	<span class="paragraph">You can start your project based on vraptor-blank-project, available on 
<a class="link" target="_blank" href="http://vraptor.caelum.com.br/download.html.">http://vraptor.caelum.com.br/download.html.</a> It contains all required jar dependencies, and
the minimal web.xml configuration for working with VRaptor.</span>
		

<h3 class="section">A simple controller</h3>
	    	<span class="paragraph">Having VRaptor properly configured on your web.xml, you can create your controllers for
dealing with web requests and start building your system.</span>
	    	<span class="paragraph">A simple controller would be:</span>
	    	<div class="java"><code class="java">
<span class="java2">/*<br />
 * You should annotate your controller with @Resource, so all of its public methods will<br />
 * be ready to deal with web requests.<br />
 */<br />
</span><span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ClientsController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">ClientDao dao;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java2">/*<br />
&nbsp;&nbsp;&nbsp;&nbsp; * You can get your class dependencies through constructor, and VRaptor will be in charge<br />
&nbsp;&nbsp;&nbsp;&nbsp; * of creating or locating these dependencies and manage them to create your controller.<br />
&nbsp;&nbsp;&nbsp;&nbsp; * If you want that VRaptor3 manages creation of ClientDao, you should annotate it with<br />
&nbsp;&nbsp;&nbsp;&nbsp; * @Component<br />
&nbsp;&nbsp;&nbsp;&nbsp; */<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ClientsController</span><span class="java8">(</span><span class="java10">ClientDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java2">/*<br />
&nbsp;&nbsp;&nbsp;&nbsp; * All public methods from your controller will be reachable through web.<br />
&nbsp;&nbsp;&nbsp;&nbsp; * For example, form method can be accessed by URI /clients/form,<br />
&nbsp;&nbsp;&nbsp;&nbsp; * and will render the view /WEB-INF/jsp/clients/form.jsp<br />
&nbsp;&nbsp;&nbsp;&nbsp; */<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">form</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java3">// code that loads data for checkboxes, selects, etc<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java2">/*<br />
&nbsp;&nbsp;&nbsp;&nbsp; * You can receive parameters on your method, and VRaptor will set your parameters<br />
&nbsp;&nbsp;&nbsp;&nbsp; * fields with request parameters. If the request have:<br />
&nbsp;&nbsp;&nbsp;&nbsp; * custom.name=Lucas<br />
&nbsp;&nbsp;&nbsp;&nbsp; * custom.address=Vergueiro Street<br />
&nbsp;&nbsp;&nbsp;&nbsp; * VRaptor will set the fields name and address of Client custom with values<br />
&nbsp;&nbsp;&nbsp;&nbsp; * &#34;Lucas&#34; and &#34;Vergueiro Street&#34;, using the fields setters.<br />
&nbsp;&nbsp;&nbsp;&nbsp; * URI: /clients/add<br />
&nbsp;&nbsp;&nbsp;&nbsp; * view: /WEB-INF/jsp/clients/add.jsp<br />
&nbsp;&nbsp;&nbsp;&nbsp; */<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Client custom</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">dao.save</span><span class="java8">(</span><span class="java10">custom</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java2">/*<br />
&nbsp;&nbsp;&nbsp;&nbsp; * VRaptor will export your method return value to the view. In this case,<br />
&nbsp;&nbsp;&nbsp;&nbsp; * since your method return type is List&lt;Clients&gt;, then you can access the<br />
&nbsp;&nbsp;&nbsp;&nbsp; * returned value on your jsp with the variable ${clientList}<br />
&nbsp;&nbsp;&nbsp;&nbsp; * URI: /clients/list<br />
&nbsp;&nbsp;&nbsp;&nbsp; * view: /WEB-INF/jsp/clients/list.jsp<br />
&nbsp;&nbsp;&nbsp;&nbsp; */<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">List&lt;Client&gt; list</span><span class="java8">() {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">dao.listAll</span><span class="java8">()</span><span class="java10">:<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java2">/*<br />
&nbsp;&nbsp;&nbsp;&nbsp; * If the return type is a simple type, the name of exported variable will be<br />
&nbsp;&nbsp;&nbsp;&nbsp; * the class name with the first letter in lower case. Since this method return<br />
&nbsp;&nbsp;&nbsp;&nbsp; * type is Client, the variable will be ${client}.<br />
&nbsp;&nbsp;&nbsp;&nbsp; * A request parameter would be something like id=5, and then VRaptor is able<br />
&nbsp;&nbsp;&nbsp;&nbsp; * to get this value, convert it to Long, and pass it as parameter to your method.<br />
&nbsp;&nbsp;&nbsp;&nbsp; * URI: /clients/view<br />
&nbsp;&nbsp;&nbsp;&nbsp; * view: /WEB-INF/jsp/clients/view.jsp<br />
&nbsp;&nbsp;&nbsp;&nbsp; */<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Client view</span><span class="java8">(</span><span class="java10">Long id</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return </span><span class="java10">dao.load</span><span class="java8">(</span><span class="java10">id</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
	    	<span class="paragraph">Note this class is independent of <code class="inlineCode">javax.servlet</code> API. The code is also very
simple and can be unit tested easily. VRaptor will make associations with these
URIs by default:</span>
	    	<div class="java"><code class="java">/client/form&nbsp;&nbsp;&nbsp;invokes&nbsp;form()<br />
/client/add&nbsp;&nbsp;&nbsp;&nbsp;invokes&nbsp;add(client)&nbsp;populating&nbsp;the&nbsp;client&nbsp;with&nbsp;request&nbsp;parameters<br />
/clients/list&nbsp;&nbsp;invokes&nbsp;list()&nbsp;and&nbsp;returns&nbsp;${clientList}&nbsp;to&nbsp;JSP<br />
/clients/view?id=3&nbsp;&nbsp;invokes&nbsp;view(3l)&nbsp;and&nbsp;returns&nbsp;${client}&nbsp;to&nbsp;JSP</code></div>
	    	<span class="paragraph">We'll see later how easy it is to change the URI <code class="inlineCode">/clients/view?id=3</code> to
the more elegant <code class="inlineCode">/clients/view/3</code>.</span>
	    	<span class="paragraph">ClientDao will also be injected by VRaptor, as we'll see.
You can see now the Ten minutes guide.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>