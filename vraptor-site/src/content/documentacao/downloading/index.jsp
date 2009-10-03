
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
									<li><a class="link_toc" href="../../documentacao/vraptor3-guia-de-1-minuto/">1. VRaptor3 - Guia de 1 minuto</a></li>
		
									<li><a class="link_toc" href="../../documentacao/vraptor3-o-guia-inicial-de-10-minutos/">2. VRaptor3 - o guia inicial de 10 minutos</a></li>
		
									<li><a class="link_toc" href="../../documentacao/resources-rest/">3. Resources-Rest</a></li>
		
									<li><a class="link_toc" href="../../documentacao/componentes/">4. Componentes</a></li>
		
									<li><a class="link_toc" href="../../documentacao/conversores/">5. Conversores</a></li>
		
									<li><a class="link_toc" href="../../documentacao/interceptadores/">6. Interceptadores</a></li>
		
									<li><a class="link_toc" href="../../documentacao/validacao/">7. Validação</a></li>
		
									<li><a class="link_toc" href="../../documentacao/view-e-ajax/">8. View e Ajax</a></li>
		
									<li><a class="link_toc" href="../../documentacao/injecao-de-dependencias/">9. Injeção de dependências</a></li>
		
									<li><a class="link_toc" href="../../documentacao/downloading/">10. Downloading</a></li>
		
									<li><a class="link_toc" href="../../documentacao/componentes-utilitarios-opcionais/">11. Componentes Utilitários Opcionais</a></li>
		
									<li><a class="link_toc" href="../../documentacao/configuracoes-avancadas-sobrescrevendo-as-convencoes-e-comportamento-do-vraptor/">12. Configurações avancadas: sobrescrevendo as convenções e comportamento do VRaptor</a></li>
		
									<li><a class="link_toc" href="../../documentacao/spring-joda-time-hibernate-e-google-app-engine/">13. Spring, Joda Time, Hibernate e Google App Engine</a></li>
		
									<li><a class="link_toc" href="../../documentacao/testando-componentes-e-controllers/">14. Testando componentes e controllers</a></li>
		
									<li><a class="link_toc" href="../../documentacao/changelog/">15. ChangeLog</a></li>
		
									<li><a class="link_toc" href="../../documentacao/migrando-do-vraptor2-para-o-vraptor3/">16. Migrando do VRaptor2 para o VRaptor3</a></li>
		
                </ol>
            </div><!-- submenu-->
                        
            <div id="textoCapitulo">
	
		<h2 class="chapter">Downloading</h2>

		


<h3 class="section">exemplo de 1 minuto: download</h3>
	    	<span class="paragraph">O exemplo a seguir mostra como disponibilizar o download para seu cliente.</span>
	    	<span class="paragraph">Note novamente a simplicidade na implementa&ccedil;&atilde;o:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">PerfilController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">File foto</span><span class="java8">(</span><span class="java10">Perfil perfil</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">File</span><span class="java8">(</span><span class="java5">&#34;/path/para/a/foto.&#34; </span><span class="java10">+ perfil.getId</span><span class="java8">()</span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java8">)</span><span class="java10">; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">Adicionando mais informações no download</h3>
	    	<span class="paragraph">Se voc&ecirc; quiser adicionar mais informa&ccedil;&otilde;es ao download voc&ecirc; pode retornar
um FileDownload:</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">PerfilController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java3">// dao ...<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">Download foto</span><span class="java8">(</span><span class="java10">Perfil perfil</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">File file = </span><span class="java4">new </span><span class="java10">File</span><span class="java8">(</span><span class="java5">&#34;/path/para/a/foto.&#34; </span><span class="java10">+ perfil.getId</span><span class="java8">()</span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; String contentType = </span><span class="java5">&#34;image/jpg&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; String filename = perfil.getNome</span><span class="java8">() </span><span class="java10">+ </span><span class="java5">&#34;.jpg&#34;</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">return new </span><span class="java10">FileDownload</span><span class="java8">(</span><span class="java10">file, contentType, filename</span><span class="java8">)</span><span class="java10">; <br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">exemplo de 1 minuto: upload</h3>
	    	<span class="paragraph">O primeiro exemplo ser&aacute; baseado na funcionalidade de upload multipart.</span>
	    	<div class="java"><code class="java">
<span class="java16">@Resource<br />
</span><span class="java4">public class </span><span class="java10">PerfilController </span><span class="java8">{<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">private final </span><span class="java10">PerfilDao dao;<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java10">PerfilController</span><span class="java8">(</span><span class="java10">PerfilDao dao</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java4">this</span><span class="java10">.dao = dao;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
<br />
&nbsp;&nbsp;&nbsp; </span><span class="java4">public </span><span class="java9">void </span><span class="java10">atualizaFoto</span><span class="java8">(</span><span class="java10">Perfil perfil, UploadedFile foto</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span><span class="java10">dao.atribui</span><span class="java8">(</span><span class="java10">foto.getFile</span><span class="java8">()</span><span class="java10">, perfil</span><span class="java8">)</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; </span><span class="java8">}<br />
}</span></code></div>
		

<h3 class="section">Mais sobre Upload</h3>
	    	<span class="paragraph">O UploadedFile retorna o arquivo como um InputStream. Se voc&ecirc; quiser copiar para
um arquivo no disco facilmente, basta usar o <code class="inlineCode">IOUtils</code> do <code class="inlineCode">commons-io</code>, que j&aacute; &eacute; depend&ecirc;ncia do
VRaptor:</span>
	    	<div class="java"><code class="java">
<span class="java4">public </span><span class="java9">void </span><span class="java10">atualizaFoto</span><span class="java8">(</span><span class="java10">Perfil perfil, UploadedFile foto</span><span class="java8">) {<br />
&nbsp;&nbsp;&nbsp; </span><span class="java10">File fotoSalva = </span><span class="java4">new </span><span class="java10">File</span><span class="java8">()</span><span class="java10">;&nbsp;&nbsp;&nbsp; <br />
&nbsp;&nbsp;&nbsp; IOUtils.copy</span><span class="java8">(</span><span class="java10">foto.getFile</span><span class="java8">()</span><span class="java10">, </span><span class="java4">new </span><span class="java10">PrintWriter</span><span class="java8">(</span><span class="java10">fotoSalva</span><span class="java8">))</span><span class="java10">;<br />
&nbsp;&nbsp;&nbsp; dao.atribui</span><span class="java8">(</span><span class="java10">fotoSalva, perfil</span><span class="java8">)</span><span class="java10">;<br />
</span><span class="java8">}</span></code></div>
		

</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->
    
<%@include file="/footer.jsp" %>