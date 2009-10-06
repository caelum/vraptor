<#include "begin.ftl">

<#list book.chapters as chapter>
\npnchapter{${parser.parse(chapter.title)}}
${chapter.getIntroduction(parser)}
	<#list chapter.sections as section>
\section{${parser.parse(section.title!"")}}			
   		<#list section.chunks as chunk>
${chunk.getContent(parser)!""}
   		</#list>
	</#list>
</#list>		

<#include "end.ftl">
