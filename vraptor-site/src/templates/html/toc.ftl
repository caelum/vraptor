<#assign relative = ".">
<#assign menu = "../">
<#include "header.ftl">

<div id="contentWrap">
    	<div id="contentDocumentacao">
        	<h2><span>documentação</span></h2>
            <h3>documentação toda em português, configuração, migração e utilização.</h3>
            
            <div id="subMenuDoc">
            	<img id="positionTop" src="${relative}/includes/images/subMenuTop-trans.png" />
                <img id="positionBottom" src="${relative}/includes/images/subMenuBottom-trans.png" />
            	<ol type="1">
								<#assign curdir = 1>
								<#list chapters as ch>
									<li><a class="link_toc" href="../${dirTree[curdir]}/">${ch_index + 1}. ${ch.title}</a></li>
									<#assign curdir = curdir + 1>
								</#list>
                </ol>
            </div><!-- submenu-->
                        
            <div id="textoCapitulo">
</div><!-- content -->
</div><!-- content cnt -->
    </div><!-- content wrap-->
<#include "footer.ftl">
