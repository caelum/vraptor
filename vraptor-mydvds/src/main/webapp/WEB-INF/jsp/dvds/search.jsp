<%@ include file="/header.jsp" %> 

<div class="blue-box">
<table>
	<tr><td align="right"><a href="user.logout.logic"><fmt:message key="logout"/></a></td></tr>
</table>
</div>

<div class="blue-box">
<h1><fmt:message key="search_results"/></h1>
<hr/>

<table>
<c:forEach var="dvd" items="${dvds}">
<tr>
    <td>
        <form action="<c:url value="/dvds/addToList/${dvd.id}" />" method="post" class="buttonForm">
            <button type="submit" class="link"><fmt:message key="add_to_my_list"/></button>
        </form>
	</td>
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