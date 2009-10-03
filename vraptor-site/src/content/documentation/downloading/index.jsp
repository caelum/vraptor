
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
	
		<h2 class="chapter">Downloading</h2>

		


<h3 class="section">1 minute example: download</h3>
	    	<span class="paragraph">The following example shows how to expose the file to be downloaded to its client.</span>
	    	<span class="paragraph">Again, see how simple this code is:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProfileController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">File picture</span><span class="java8">(</span><span class="java10">Profile profile</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">File</span><span class="java8">(</span><span class="java5">&#34;/path/to/the/picture.&#34; </span><span class="java10">+ profile.getId</span><span class="java8">()</span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">Adding more info to download</h3>
	    	<span class="paragraph">If you want to add more information to download, you can return a FileDownload:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProfileController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Download picture</span><span class="java8">(</span><span class="java10">Profile profile</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">File file = </span><span class="java4">new </span><span class="java10">File</span><span class="java8">(</span><span class="java5">&#34;/path/to/the/picture.&#34; </span><span class="java10">+ profile.getId</span><span class="java8">()</span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; String contentType = </span><span class="java5">&#34;image/jpg&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; String filename = profile.getName</span><span class="java8">() </span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">FileDownload</span><span class="java8">(</span><span class="java10">file, contentType, filename</span><span class="java8">)</span><span class="java10">; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">1 minute example: upload</h3>
	    	<span class="paragraph">The first example is based on the multipart upload feature.</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">ProfileController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">ProfileDao dao;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">ProfileController</span><span class="java8">(</span><span class="java10">ProfileDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">updatePicture</span><span class="java8">(</span><span class="java10">Profile profile, UploadedFile picture</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">dao.update</span><span class="java8">(</span><span class="java10">picture.getFile</span><span class="java8">()</span><span class="java10">, profile</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">More about Upload</h3>
	    	<span class="paragraph">UploadedFile returns the file content as a InputStream. If you want to save this file on disk in an 
easy way, you can use the <code class="inlineCode">commons-io IOUtils</code>, that is already a VRaptor dependency:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">updatePicture</span><span class="java8">(</span><span class="java10">Profile profile, UploadedFile picture</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">File pictureOnDisk = </span><span class="java4">new </span><span class="java10">File</span><span class="java8">()</span><span class="java10">;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; IOUtils.copy</span><span class="java8">(</span><span class="java10">picture.getFile</span><span class="java8">()</span><span class="java10">, </span><span class="java4">new </span><span class="java10">PrintWriter</span><span class="java8">(</span><span class="java10">pictureOnDisk</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; dao.atribui</span><span class="java8">(</span><span class="java10">pictureOnDisk, profile</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>