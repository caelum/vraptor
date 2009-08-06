<#assign relative = "..">
<#assign menu = "../../../">

<#assign chapter_name = chapter.title >
<#assign section_name = "none" >

<#include "header.ftl">
	

		<h1 class="chapter">${chapter.title}</h1>

		${chapter.getIntroduction(parser)}
		
		<#list chapter.sections as section>
			<#include "sectionContent.ftl">		
		</#list>


<#include "footer.ftl">