<%@ include file="../header.jsp" %> 

<div id="blue-box" style="width: 700px;">

This feature is here only for demonstration. You should turn it off while live.<br/>
<br/>

<table style="width: 100%; border-style: solid; border-width: 1pt; border: solid 1pt; border-collapse: collapse;">
<tr>
	<td>Name</td>
	<td>Component class</td>
	<td>Interceptors</td>
	<td>Read</td>
	<td>Logics</td>
</tr>
<c:forEach var="component" items="${components}">
<tr>
	<td>${component.name}</td>
	<td>${component.componentClass.simpleName}</td>
	<td>
<c:forEach var="i" items="${component.interceptors}">
	- ${i.interceptorClass.simpleName}<br/>
</c:forEach>
	</td>
	<td>
<c:forEach var="p" items="${component.readParameters}">
	- ${p.key}<br/>
</c:forEach>
	</td>
	<td>
<c:forEach var="logic" items="${component.logics}">
	- ${logic.name} : ${logic.metadata.name}<br/>
</c:forEach>
	</td>
</tr>
</c:forEach>
</table>

<br/><br/>

<table style="width: 100%; border-style: solid; border-width: 1pt; border: solid 1pt; border-collapse: collapse;">
<tr>
	<td>Url</td>
	<td>Callback</td>
</tr>
<c:forEach var="component" items="${components}">
<c:forEach var="logic" items="${component.logics}">
<tr>
	<td>${component.name}.${logic.name}.logic</td>
	<td>${component.componentClass.name}.${logic.metadata.name}</td>
</tr>
</c:forEach>
</c:forEach>
</table>

</div>
<%@ include file="../footer.jsp" %> 