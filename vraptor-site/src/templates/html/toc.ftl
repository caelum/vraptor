
<!-- left bar -->
<div class="linkBar">

	<#assign curdir = 1>
	<#list chapters as chapter>
		<h2 class="chapterLink"><a href="../${dirTree[curdir]}/">${chapter.title}</a></h2>
		<#assign curdir = curdir + 1>
		
		<#list chapter.sections as section>
			<h3 class="sectionLink"><a href="../${dirTree[curdir]}/">${section.title}</a></h3>		
			<#assign curdir = curdir + 1>
		</#list>
	</#list>
	
</div>