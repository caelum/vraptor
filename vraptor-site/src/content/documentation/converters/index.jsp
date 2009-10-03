
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
	
		<h2 class="chapter">Converters</h2>

		


<h3 class="section">Default</h3>
	    	<span class="paragraph">VRaptor registers a default set of converters for your day-to-day use.</span>
		

<h3 class="section">Primitive types</h3>
	    	<span class="paragraph">All primitive types (int, long etc) are supported.</span>
	    	<span class="paragraph">If the request parameter is empty or null, primitive type variables will be set to its default
value, as if it was a class attribute. In general:</span>
	    	<ul class="list"><li><span class="paragraph">boolean - false</span></li><li><span class="paragraph">short, int, long, double, float, byte - 0</span></li><li><span class="paragraph">char - caracter de c&oacute;digo 0</span></li></ul>
		

<h3 class="section">Primitive type wrappers</h3>
	    	<span class="paragraph">All primitive type wrappers (Integer, Long, Character, Boolean etc) are supported.</span>
		

<h3 class="section">Enum</h3>
	    	<span class="paragraph">Enums are also supported using the element's name or ordinal value.
In the following example, either 1 or DEBIT values are converted to Type.DEBIT:</span>
	    	<div class="java"><code class="java">public&nbsp;enum&nbsp;Type&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;CREDIT,&nbsp;DEBIT<br />
}</code></div>
		

<h3 class="section">BigInteger and BigDecimal</h3>
	    	<span class="paragraph">Both are supported using your JVM's default locale.
To enable decimal values based on the user's locale, you can check how the class LocaleBasedCalendarConverter works.</span>
		

<h3 class="section">Calendar and Date</h3>
	    	<span class="paragraph">Both LocaleBasedCalendarConverter and LocaleBasedDateConverter are based on the user's locale,
defined using JSTL pattern to understand the parameter's format.</span>
	    	<span class="paragraph">For example, if the locale is pt-br, then "18/09/1981" stands for September 18th 1981.
On the other hand, if the locale is en, the same date is formatted as "09/18/1981".</span>
		

<h3 class="section">Interface</h3>
	    	<span class="paragraph">All converters must implement VRaptor's Converter interface.
The concrete class will define which type it is able to convert, and will be invoked
with a request parameter, the target type and a resource bundle containing i18n messages,
useful if you wish to raise a ConversionException in case of convertion errors.</span>
	    	<div class="java"><code class="java">public&nbsp;interface&nbsp;Converter&lt;T&gt;&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;T&nbsp;convert(String&nbsp;value,&nbsp;Class&lt;?&nbsp;extends&nbsp;T&gt;&nbsp;type,&nbsp;ResourceBundle&nbsp;bundle);<br />
}</code></div>
	    	<span class="paragraph">Also, your must tell VRaptor (not the compiler) which type your converter is able to handle. You do that by annotating your converter class with @Convert:</span>
	    	<div class="java"><code class="java">@Convert(Long.class)<br />
public&nbsp;class&nbsp;LongConverter&nbsp;implements&nbsp;Converter&lt;Long&gt;&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;...<br />
}</code></div>
	    	<span class="paragraph">Finally, don't forget to specify the scope of your converter, just like you do with any other resource in VRaptor.
For example, if your converter doesn't need any user specific information, it can be registered as application scoped and only one instance of that converter will be created:</span>
	    	<div class="java"><code class="java">@Convert(Long.class)<br />
@ApplicationScoped<br />
public&nbsp;class&nbsp;LongConverter&nbsp;implements&nbsp;Converter&lt;Long&gt;&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp;...<br />
}</code></div>
	    	<span class="paragraph">In the following lines, you can see a LongConverter implementation, showing how simple it is to assemble all the information mentioned above:</span>
	    	<div class="java"><code class="java">@Convert(Long.class)<br />
@ApplicationScoped<br />
public&nbsp;class&nbsp;LongConverter&nbsp;implements&nbsp;Converter&lt;Long&gt;&nbsp;{<br />
<br />
&nbsp;&nbsp;&nbsp;&nbsp;public&nbsp;Long&nbsp;convert(String&nbsp;value,&nbsp;Class&lt;?&nbsp;extends&nbsp;Long&gt;&nbsp;type,&nbsp;ResourceBundle&nbsp;bundle)&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if&nbsp;(value&nbsp;==&nbsp;null&nbsp;||&nbsp;value.equals(""))&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return&nbsp;null;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;try&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return&nbsp;Long.valueOf(value);<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}&nbsp;catch&nbsp;(NumberFormatException&nbsp;e)&nbsp;{<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw&nbsp;new&nbsp;<br />
&nbsp;&nbsp;&nbsp;&nbsp;ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"),&nbsp;value));<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br />
&nbsp;&nbsp;&nbsp;&nbsp;}<br />
<br />
}</code></div>
		

<h3 class="section">Registering a new converter</h3>
	    	<span class="paragraph">No further configuration is needed except implementing the Converter interface and annotating
the converter class with @Convert for your custom converter to be registered in VRaptor's container.</span>
		

<h3 class="section">More complex converters</h3>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>