
<%@ include file="../../../header.jsp" %> 

<c:if test="${not empty param.language}">
	<fmt:setLocale value="${param.language}" scope="session"/>
</c:if>

<c:if test="${not empty errors}">
	<div id="error-box">
	<h1><fmt:message key="errors"/></h1>
	<hr />
	<c:forEach var="error" items="${errors}">
   		<h3><fmt:message key="${error.category}"/> - (${error.message})</h3>
   	</c:forEach>
	</div>
</c:if>

<div class="blue-box">
<h1><fmt:message key="change_language"/></h1>
<hr/>
<a href="?language=de"><img src="images/de.gif" border="0"/></a> | 
<a href="?language=en"><img src="images/en.gif" border="0"/></a> | 
<a href="?language=pt_br"><img src="images/br.gif" border="0"/></a>

</div>

<br/><br/>

<div class="blue-box">

<h1><fmt:message key="welcome"/></h1>

<hr/>

<form action="<c:url value="/home/login"/>" name="loginForm">
	<table>
		<tr><td><fmt:message key="login" /></td>
			<td><input type="text" name="login" /></td></tr>	
		<tr><td><fmt:message key="password" /></td>
			<td><input type="text" name="password" /></td></tr>
		<tr><td><input type="submit" value="send"/></td></tr>	
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

<form action="<c:url value="/users" />" name="registerForm">
	<table>
		<tr><td><fmt:message key="name" /></td>
			<td><input type="text" name="user.name" value="${user.name }"/></td></tr>
		<tr><td><fmt:message key="login" /></td>
			<td><input type="text" name="user.login" value="${user.login }"/></td></tr>
		<tr><td><fmt:message key="password" /></td>
			<td><input type="text" name="user.password" value="${user.password }"/></td></tr>
	</table>
</form>
</div>

<%@ include file="../../../footer.jsp" %> 