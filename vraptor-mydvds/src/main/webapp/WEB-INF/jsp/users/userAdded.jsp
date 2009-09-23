<%@ include file="/header.jsp" %> 
<div class="blue-box">
<h1>${user.name}: <fmt:message key="user_added"/></h1>
<hr/>
<a href="<c:url value="/" />" id="index"><fmt:message key="login_now"/></a>
</div>
<%@ include file="/footer.jsp" %> 