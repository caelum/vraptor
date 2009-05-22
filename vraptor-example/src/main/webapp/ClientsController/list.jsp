<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<a href="${pageContext.request.contextPath}/clients/random"/>">random pick</a>
<br/><br/>

<table>
	<tr>
		<th>Id</th>
		<th>Name</th>
		<th>Actions</th>
	</tr>
	<c:forEach var="client" items="${clients }">
		<tr>
			<td>${client.id}</td>
			<td>${client.name}</td>
			<td><a href="<c:url value="/clients/${client.id }"/>">view</a> |
			<a href="<c:url value="/clients/${client.id }"/>?_method=DELETE">delete</a>
			</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>