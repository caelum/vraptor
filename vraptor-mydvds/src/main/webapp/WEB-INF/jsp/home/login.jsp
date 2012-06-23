
<%@ include file="../../../header.jsp" %> 

<form action="<c:url value="/home/login"/>" name="loginForm" method="post">
	<fieldset>
		<legend>Sign In</legend>
		<label for="login"><fmt:message key="login" /></label>
		<input type="text" name="login" id="login" />	
		<label for="password"><fmt:message key="password" /></label>
		<input type="password" name="password" id="password"/>
		<button type="submit" id="submit"><fmt:message key="send"/></button>
	</fieldset>
</form>

<form action="<c:url value="/users" />" name="registerForm" method="post">
	<fieldset>
		<legend>Sign Up</legend>
		<label for="user.name"><fmt:message key="name" /></label>
		<input type="text" name="user.name" value="${user.name }"/>
		<label for="user.login"><fmt:message key="login" /></label>
		<input type="text" name="user.login" value="${user.login }"/>
		<label for="user.password"><fmt:message key="password" /></label>
		<input type="password" name="user.password" value="${user.password }"/>
		<button type="submit" id="user.submit"><fmt:message key="send"/></button>
	</fieldset>
</form>

<%@ include file="../../../footer.jsp" %> 