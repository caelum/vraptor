<%@ include file="/header.jsp" %> 

<div class="blue-box">
<table>
	<tr><td align="right"><a href="<c:url value="/logout" />"><fmt:message key="logout"/></a></td></tr>
</table>
</div>

<div class="blue-box">
<h1><fmt:message key="list_users"/></h1>
<hr/>
<table>
<c:forEach var="user" items="${users}">
	<tr>
		<td><a href="<c:url value="/users/${user.id}" />"><fmt:message key="view"/></a></td>
		<td>${user.name}</td>
	</tr>
</c:forEach>
</table>
</div>

<%@ include file="/footer.jsp" %> 