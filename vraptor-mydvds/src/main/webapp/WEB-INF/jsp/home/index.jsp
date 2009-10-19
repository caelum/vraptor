
<%@ include file="../../../header.jsp" %> 

<div class="blue-box">

<h1><fmt:message key="welcome"/></h1>

<hr/>

<form action="<c:url value="/home/login"/>" name="loginForm" method="post">
	<fieldset>
		<legend>Sign In</legend>
		<label for="login"><fmt:message key="login" /></label>
		<input type="text" name="login" id="login" />	
		<label for="password"><fmt:message key="password" /></label>
		<input type="password" name="password" id="password"/>
		<button type="submit"><fmt:message key="send"/></button>
	</fieldset>
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
	<fieldset>
		<legend>Sign Up</legend>
		<label for="user.name"><fmt:message key="name" /></label>
		<input type="text" name="user.name" value="${user.name }"/>
		<label for="user.login"><fmt:message key="login" /></label>
		<input type="text" name="user.login" value="${user.login }"/>
		<label for="user.password"><fmt:message key="password" /></label>
		<input type="password" name="user.password" value="${user.password }"/>
		<button type="submit"><fmt:message key="send"/></button>
	</fieldset>
</form>
</div>

<%@ include file="../../../footer.jsp" %> 