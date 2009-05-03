<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<h2>${client.id }. ${client.name }</h2>
<ul>
	<c:forEach var="address" items="${client.addresses }">
		<li>${address.street }, ${address.number }</li>
	</c:forEach>
</ul>
<br />
<ul>
	<c:forEach var="email" items="${client.emails }">
		<li>${email }</li>
	</c:forEach>
</ul>
<br />
<br />
<a href="<c:url value="/clients/${client.id }"/>">view</a>
|
<a href="<c:url value="/clients/${client.id }"/>?_method=delete">delete</a>
|
<a href="<c:url value="/clients"/>">list</a>


</body>
</html>