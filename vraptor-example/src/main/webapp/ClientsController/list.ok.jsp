<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<a href="<c:url value="/clients/random"/>">random pick</a>
<br/><br/>

<form action="<c:url value="/clients"/>" method="post">
<table>
	<tr>
		<td>Name:</td>
		<td><input type="text" name="client.name" value="" /> (min length: 5)</td>
	</tr>
	<tr>
		<td>Balance:</td>
		<td><input type="text" name="client.balance" value="" /></td>
	</tr>
	<tr>
		<td>Street:</td>
		<td><input type="text" name="client.addresses[0].street" value="" /></td>
	</tr>
	<tr>
		<td>Number:</td>
		<td><input type="text" name="client.addresses[0].number" value="" /></td>
	</tr>
	<tr>
		<td>Age:</td>
		<td><input type="text" name="client.age" value="" /> (min: 10)</td>
	</tr>
	<tr>
		<td>Id:</td>
		<td><input type="text" name="client.id" value="" /><br /></td>
	</tr>
	<tr>
		<td colspan="2"><input type="submit" /></td>
	</tr>
</table>
</form>

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
			<a href="<c:url value="/clients/${client.id }"/>?_method=delete">delete</a>
			</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>