<%@ include file="/header.jsp" %> 

<div id="blue-box">
<table>
	<tr><td align="right"><a href="user.logout.logic"><fmt:message key="logout"/></a></td></tr>
</table>
</div>

<div id="blue-box">
<h1><fmt:message key="search_results"/></h1>
<hr/>

<table>
<c:forEach var="dvd" items="${dvds}">
<tr>
	<td><a href="<c:url value="/dvd/addToList/${dvd.id}" />"><fmt:message key="add_to_my_list"/></a></td>
	<td>${dvd.title}</td>
	<td>${dvd.description}</td>
	<td><fmt:message key="${dvd.type}"/></td>
	<td>
		<c:forEach var="user" items="${dvd.users}">
			${user.name}<br/>
		</c:forEach>
	</td>
</tr>
</c:forEach>
</table>
</div>

<%@ include file="/footer.jsp" %> 