
<%@ include file="header.jsp" %> 

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

<div id="blue-box">
<h1><fmt:message key="change_language"/></h1>
<hr/>
<a href="index.jsp?language=de"><img src="images/de.gif" border="0"/></a> | 
<a href="index.jsp?language=en"><img src="images/en.gif" border="0"/></a> | 
<a href="index.jsp?language=pt_br"><img src="images/br.gif" border="0"/></a>

</div>

<br/><br/>

<div id="blue-box">

<h1><fmt:message key="welcome"/></h1>

<hr/>

<w:form action="/login" type="table" method="post" border="0">
	<w:text name="login" />
	<w:password name="password" />
	<w:submit id="submit" value="send"/>
</w:form>
</div>

<br/><br/>

<div id="blue-box">

<c:if test="${not empty user}">
	<h1>${user.name}: <fmt:message key="user_added"/></h1>
</c:if>

<c:if test="${empty user}">
	<h1><fmt:message key="new_user_message"/></h1>
</c:if>

<hr/>

<w:form action="/users" type="table" method="post">
	<w:text name="user.name" label="name" />
	<w:text name="user.login" label="login" />
	<w:password name="user.password" label="password" />
	<w:submit value="send" />
</w:form>
</div>

<%@ include file="footer.jsp" %> 