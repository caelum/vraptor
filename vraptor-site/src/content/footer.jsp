<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:setLocale value="${locale}"/>
<c:if test="${param.docs}">
		<c:set var="path">../..</c:set>
</c:if>
    <div id="footerWrap">
    	<div id="footerContent">              
            <div id="suporteFooter">
	           	<a href="http://www.caelum.com.br/contato/" class="moreIcon" title="Saiba Mais"><span>+</span></a>
	           	<h3><a href="http://www.caelum.com.br/contato/"><fmt:message key="suporte"/></a></h3>
                <p><a href="http://www.caelum.com.br/contato/"><fmt:message key="suporte.head"/></a></p>            
            </div><!-- suporte-->
            <div id="consultoriaFooter">
           		<a href="http://www.caelum.com.br/servicos/consultoria/" class="moreIcon" title="Saiba Mais"><span>+</span></a>
           		<h3><a href="http://www.caelum.com.br/servicos/consultoria/"><fmt:message key="consultoria"/></a></h3>
                <p><a href="http://www.caelum.com.br/servicos/consultoria/"><fmt:message key="consultoria.head"/></a></p>            
            </div><!-- consultoria-->
        	<div id="treinamentoFooter">
            	<a href="http://www.caelum.com.br/curso/fj-28-vraptor-hibernate-ajax/" class="moreIcon" title="Saiba Mais"><span>+</span></a><h3><a href="http://www.caelum.com.br/curso/fj-28-vraptor-hibernate-ajax/"><fmt:message key="treinamento"/></a></h3>
                <p><a href="http://www.caelum.com.br/curso/fj-28-vraptor-hibernate-ajax/"><fmt:message key="treinamento.head"/></a></p>
            </div><!--treinamento-->
     <div class="footbar">
            	<img class="logoFooter" src="${path }/images/logoCaelumGray-trans.png" alt="vraptor logo" />
                <ul>
                	<li>site map:</li>
                    <li><a href="${path }/<fmt:message key='home.link'/>">home</a></li>
                    <li>|</li>
                    <li><a href="${path }/<fmt:message key='download.link'/>">download</a></li>
                    <li>|</li>
                    <li><a href="${path }/<fmt:message key='documentacao.link'/>"><fmt:message key="documentacao"/></a></li>
                    <li>|</li>
                    <li><a href="${path }/<fmt:message key='beneficios.link'/>"><fmt:message key="beneficios"/></a></li>
                    <li>|</li>
                    <li><a href="${path }/<fmt:message key='suporte.link'/>"><fmt:message key="suporte"/></a></li>
                    <li>|</li>
                    <li><a href="${path }/<fmt:message key='vraptor2.link'/>">vraptor2</a></li>
                </ul>
                <p><a href="http://www.apache.org/licenses/LICENSE-2.0"><fmt:message key="license"/></a> - VRaptor ©2009 Caelum - Ensino e Inovação</p>
                <p>Profiled with <a href="http://www.ej-technologies.com/products/jprofiler/overview.html">JProfiler</a></p>
            </div><!-- footnote-->
        </div><!-- footer content -->
        <a id="signature" href="mailto:lokidg@gmail.com">loki|design</a>
    </div><!-- footer wrap-->
<script type="text/javascript">
	var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
	document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>

<script type="text/javascript">
	try {
	var pageTracker = _gat._getTracker("UA-270161-11");
	pageTracker._trackPageview();
	} catch(err) {}
 </script>
    
</body>
</html>
