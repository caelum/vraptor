<#assign relative = ".">
<#assign menu = "../">
<#include "header.ftl">

<!-- left bar -->
<div class="linkBar">

	<#assign curdir = 1>
	<#list chapters as chapter>
		<h3 class="chapterLink"><a class="link_toc" href="../${dirTree[curdir]}/">${chapter_index + 1}. ${chapter.title}</a></h3>
		<#assign curdir = curdir + 1>
		
		<#list chapter.sections as section>
			<#if section.title != "">
				<#assign curdir = curdir + 1>
			</#if>
		</#list>
	</#list>
	
</div>

<#include "footer.ftl">
