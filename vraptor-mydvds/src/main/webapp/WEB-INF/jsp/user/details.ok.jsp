<%@ include file="/header.jsp" %> 

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<table>
	<tr>
		<td>Dvds</td>
		<td>$userdvds</td>
		<td>$errors.dvds</td>
	</tr>
	<tr>
		<td>Id</td>
		<td>$userid</td>
		<td>$errors.id</td>
	</tr>
	<tr>
		<td>Login</td>
		<td>$userlogin</td>
		<td>$errors.login</td>
	</tr>
	<tr>
		<td>Name</td>
		<td>$username</td>
		<td>$errors.name</td>
	</tr>
	<tr>
		<td>Password</td>
		<td>$userpassword</td>
		<td>$errors.password</td>
	</tr>
</table>


<%@ include file="/footer.jsp" %> 