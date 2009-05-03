<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>

<a href="<c:url value="/clients/random"/>">random pick</a>
<br/><br/>

<form action="<c:url value="/clients"/>" method="post">
<table>
	<tr>
		<td>Name</td>
		<td><input type="text" name="client.name" value="" /></td>
		<td>(min length: 5)</td>
	</tr>
	<tr>
		<td><input type="text" name="client.balance" value="" /><br />
		Street: <input type="text" name="client.addresses[0].street" value="" /><br />
		Number: <input type="text" name="client.addresses[0].number" value="" /><br />
		Age: <input type="text" name="client.age" value="" /> (min: 10)<br />
		Id: <input type="text" name="client.id" value="" /><br />
		</td>
	</tr>
	<tr>
		<td><input type="submit" /></td>
	</tr>
</table>
</form>
<table>
	<tr>
		<td>Id</td>
		<td>Name</td>
		<td>Actions</td>
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