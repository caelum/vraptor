<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
Name: ${client.name}
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
</body>
</html>