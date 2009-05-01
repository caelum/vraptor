<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<form action="<c:url value="/clients/add"/>">
<table>
	<tr>
		<td>Name: <input type="text" name="client.name" value="" /> (min
		length: 5)<br />
		<input type="text" name="client.balance" value="" /><br />
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
	<c:forEach var="client" items="${clients }">
		<tr>
			<td>Name</td>
			<td>Info</td>
			<td>Actions</td>
		</tr>
		<tr>
			<td>${client.name} (id= ${client.id})</td>
			<td>
			<ul>
				<c:forEach var="address" items="${client.addresses }">
					<li>${address.street }, ${address.number }</li>
				</c:forEach>
			</ul>
			<ul>
				<c:forEach var="email" items="${client.emails }">
					<li>${email }</li>
				</c:forEach>
			</ul>
			</td>
			<td>
			<form 
				action="<c:url value="/clients/delete/${client.id}"  />"><input
				type="submit" value="remove" /></form>
			</td>
		</tr>
	</c:forEach>
</table>
</body>
</html>