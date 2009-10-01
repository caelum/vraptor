<#assign relative = "..">
<#assign menu = "../../../">

<#assign chapter_name = chapter.title >
<#assign section_name = "none" >

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
								<#list book.chapters as ch>
									<li><a class="link_toc" href="../${dirTree[curdir]}/">${ch_index + 1}. ${ch.title}</a></li>
									<#assign curdir = curdir + 1>
		
								</#list>
                </ol>
            </div><!-- submenu-->
                        
            <div id="textoCapitulo">
							<h2 class="chapter">${chapter.title}</h2>

							${chapter.getIntroduction(parser)}
		
							<#list chapter.sections as section>
								<#include "sectionContent.ftl">		
							</#list>
						</div><!-- content -->
            
        </div><!-- content cnt -->
    </div><!-- content wrap-->

<#include "footer.ftl">
