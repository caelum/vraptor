
<%@ include file="../../../header.jsp" %> 

<div class="blue-box">
<h1><fmt:message key="change_language"/></h1>
<hr/>
<a href="?language=de"><img src="<c:url value="/"/>images/de.gif" border="0"/></a> | 
<a href="?language=en"><img src="<c:url value="/"/>images/en.gif" border="0"/></a> | 
<a href="?language=pt_br"><img src="<c:url value="/"/>images/br.gif" border="0"/></a>

</div>

<br/><br/>

<div class="blue-box">

<h1><fmt:message key="welcome"/></h1>

<hr/>

<form action="<c:url value="/home/login"/>" name="loginForm" method="post">
	<table>
		<tr><td><fmt:message key="login" /></td>
			<td><input type="text" name="login" /></td></tr>	
		<tr><td><fmt:message key="password" /></td>
			<td><input type="password" name="password" /></td></tr>
		<tr><td></td><td><input type="submit" value="send"/></td></tr>	
	</table>
</form>
</div>

<br/><br/>

<div class="blue-box">

<c:if test="${not empty user}">
	<h1>${user.name}: <fmt:message key="user_added"/></h1>
</c:if>

<c:if test="${empty user}">
	<h1><fmt:message key="new_user_message"/></h1>
</c:if>

<hr/>

<form action="<c:url value="/users" />" name="registerForm" method="post">
	<table>
		<tr><td><fmt:message key="name" /></td>
			<td><input type="text" name="user.name" value="${user.name }"/></td></tr>
		<tr><td><fmt:message key="login" /></td>
			<td><input type="text" name="user.login" value="${user.login }"/></td></tr>
		<tr><td><fmt:message key="password" /></td>
			<td><input type="password" name="user.password" value="${user.password }"/></td></tr>
		<tr><td></td><td><input type="submit" value="send"/></td></tr>
	</table>
</form>
</div>

<%@ include file="../../../footer.jsp" %> 