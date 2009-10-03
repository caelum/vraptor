
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
	
		<h2 class="chapter">Validation</h2>

		<span class="paragraph">VRaptor3 supports two different validation styles: classic and fluent. The starting point to both styles is the Validator
interface.
In order to access the Validator, your resource must receive it in the constructor:</span><div class="java"><code class="java">
<span class="java4">import </span><span class="java10">br.com.caelum.vraptor.Validator;<br />
...<br />
<br />
</span><span class="java16">@Resource<br />
</span><span class="java4">class </span><span class="java10">EmployeeController </span><span class="java8">{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private </span><span class="java10">Validator validator;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">EmployeeController</span><span class="java8">(</span><span class="java10">Validator validator</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.validator = validator;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>


<h3 class="section">Classic style</h3>
	    	<span class="paragraph">The classic style is very similar to VRaptor2's validation.
Inside your business logic, all you have to do is check the data you want, and if you find any validation errors, add them to the errors list.
For example, to validate that employee name is 'John Doe':</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if </span><span class="java8">(</span><span class="java10">!employee.getName</span><span class="java8">()</span><span class="java10">.equals</span><span class="java8">(</span><span class="java5">&#34;John Doe&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">ValidationMessage</span><span class="java8">(</span><span class="java5">&#34;error&#34;</span><span class="java10">,</span><span class="java5">&#34;invalidName&#34;</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">EmployeeController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.form</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">When you call validator.onErrorUse, if there are any validation errors, VRaptor will stop execution and redirect
to the page you specified. This redirect has the same behavior as the result.use(..) redirects.</span>
		

<h3 class="section">Fluent style</h3>
	    	<span class="paragraph">The goal of fluent style is to write the validation code in such way that it feels natural.
For example, if we want the employee name to be required:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!employee.getName</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;error&#34;</span><span class="java10">,</span><span class="java5">&#34;nameIsRequired&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">EmployeeController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.form</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">You can read the code above like this: "Validator, check my validations. First one is that employee name cannot be empty".
Much closer to natural language.</span>
	    	<span class="paragraph">So, if employee name is empty, the flow will be redirected to the "form" logic,
which shows the user a form to insert employee data again.
Also, the error message is sent back to the form.</span>
	    	<span class="paragraph">There are validations that may occur only if other validation succeeded, for instance I will check
user age only if the user is not null. The that method will return a boolean that represents the success
of the validation:</span>
	    	<div class="java"><code class="java">
<span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if </span><span class="java8">(</span><span class="java10">that</span><span class="java8">(</span><span class="java10">user != null, </span><span class="java5">&#34;user&#34;</span><span class="java10">, </span><span class="java5">&#34;null.user&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">user.getAge</span><span class="java8">() </span><span class="java10">&gt;= </span><span class="java7">18</span><span class="java10">, </span><span class="java5">&#34;user.age&#34;</span><span class="java10">, </span><span class="java5">&#34;user.is.underage&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}})</span></code></div>
	    	<span class="paragraph">So the second validation will execute only if the first didn't fail.</span>
		

<h3 class="section">Validation using Hamcrest Matchers</h3>
	    	<span class="paragraph">You can use Hamcrest matchers for making validation even more fluent and readable,
with the advantage of matcher composition and the creation of new matchers that
Hamcrest allows:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">admin</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">employee.getRoles</span><span class="java8">()</span><span class="java10">, hasItem</span><span class="java8">(</span><span class="java5">&#34;ADMIN&#34;</span><span class="java8">)</span><span class="java10">, </span><span class="java5">&#34;admin&#34;</span><span class="java10">,</span><span class="java5">&#34;employee.is.not.admin&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">LoginController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.login</span><span class="java8">()</span><span class="java10">;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">Hibernate validator</h3>
	    	<span class="paragraph">VRaptor 3 also supports HibernateValidator integration. In the example above, to validate the employee object using HibernateValidator, just add one line to your code:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Validation with Hibernate Validator<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java10">Hibernate.validate</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!employee.getName</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;error&#34;</span><span class="java10">,</span><span class="java5">&#34;nameIsRequired&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">EmployeeController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.form</span><span class="java8">()</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

<h3 class="section">Where to redirect in case of errors</h3>
	    	<span class="paragraph">Another issue that one must consider when validating data is where to redirect
when an error occurs. How do one redirect the user to another resource using VRaptor3 in case of validation errors?</span>
	    	<span class="paragraph">Easy, just tell your validator to do just that: when you find any validation error, send the user to the specified resource. See the example:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java10">add</span><span class="java8">(</span><span class="java10">Employee employee</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Fluent validation<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.checking</span><span class="java8">(</span><span class="java4">new </span><span class="java10">Validations</span><span class="java8">(){{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">that</span><span class="java8">(</span><span class="java10">!employee.getName</span><span class="java8">()</span><span class="java10">.isEmpty</span><span class="java8">()</span><span class="java10">, </span><span class="java5">&#34;error&#34;</span><span class="java10">,</span><span class="java5">&#34;nameIsRequired&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}})</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">//Classic validation<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">if </span><span class="java8">(</span><span class="java10">!employee.getName</span><span class="java8">()</span><span class="java10">.equals</span><span class="java8">(</span><span class="java5">&#34;John Doe&#34;</span><span class="java8">)) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.add</span><span class="java8">(</span><span class="java4">new </span><span class="java10">ValidationMessage</span><span class="java8">(</span><span class="java5">&#34;error&#34;</span><span class="java10">,</span><span class="java5">&#34;invalidName&#34;</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<strong>&nbsp;&nbsp;&nbsp; </span><span class="java10">validator.onErrorUse</span><span class="java8">(</span><span class="java10">page</span><span class="java8">())</span><span class="java10">.of</span><span class="java8">(</span><span class="java10">EmployeeController.</span><span class="java4">class</span><span class="java8">)</span><span class="java10">.form</span><span class="java8">()</span><span class="java10">;<br /></strong>
&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; dao.add</span><span class="java8">(</span><span class="java10">employee</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
	    	<span class="paragraph">If your logic may add any validation error you <strong class="definition">must</strong> specify where to go in case of error.
Validator.onErrorUse works just like result.use: you can use any view from Results class.</span>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>