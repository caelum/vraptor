<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	
	<c:forEach var="error" items="${errors}">
		<li>${error.message } - ${error.category }</li>
	</c:forEach>
	<form action="<c:url value="/clients"/>" method="post" enctype="multipart/form-data"  >
	<table>
		<tr>
			<td>Name:</td>
			<td><input type="text" name="client.name" value="" /> (min length: 5)</td>
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
			<td>Arquivo:</td>
			<td><input type="file" name="client.file" /><br /></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" /></td>
		</tr>
	</table>
	</form>
</body>
</html>