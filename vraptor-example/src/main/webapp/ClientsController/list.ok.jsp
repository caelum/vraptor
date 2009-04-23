<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<form action="clients/add">Name: <input type="text"
	name="client.name" value="" /><br />
	<input type="text" name="client.balance" value="" /><br/>
Street: <input type="text" name="client.addresses[0].street" value="" /><br />
Number: <input type="text" name="client.addresses[0].number" value="" /><br />
Id: <input type="text" name="client.id" value="" /><br />
<input type="submit" /></form>
<table>
<c:forEach var="client" items="${clients }">
<tr>
<td>
Name: ${client.name} (id= ${client.id})
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
</c:forEach>
</td>
</tr>
</table>
</body>
</html>