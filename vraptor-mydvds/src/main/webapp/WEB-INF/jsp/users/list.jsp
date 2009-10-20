<%@ include file="/header.jsp" %> 

<h1><fmt:message key="list_users"/></h1>
<table>
<c:forEach var="user" items="${users}">
	<tr>
		<td><a href="<c:url value="/users/${user.login}" />"><fmt:message key="view"/></a></td>
		<td>${user.name}</td>
	</tr>
</c:forEach>
</table>

<%@ include file="/footer.jsp" %> 