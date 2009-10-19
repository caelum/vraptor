<%@ include file="/header.jsp" %> 

<div class="blue-box">
<table id="user">
	<tr><td align="right">${currentUser.name} - <a href="<c:url value="/home/logout" />"><fmt:message key="logout"/></a></td></tr>
</table>
</div>

<div class="blue-box">
<h1>${currentUser.name}: <fmt:message key="your_dvds"/></h1>
<hr/>

<table>
<c:forEach var="dvd" items="${currentUser.dvds}">
	<tr><td>${dvd.title}</td><td>${dvd.description}</td><td><fmt:message key="${dvd.type}"/></td></tr>
</c:forEach>
</table>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="new_dvd"/></h1>
<hr/>
<form action="<c:url value="/dvds"/>" enctype="multipart/form-data" name="dvdRegister" method="post">
	<table>
		<tr><td><fmt:message key="dvd.title"/></td>
			<td><input type="text" name="dvd.title" value="${dvd.title }"/></td></tr>
		<tr><td><fmt:message key="dvd.description"/></td>
			<td><input type="text" name="dvd.description" value="${dvd.description }"/></td></tr>
		<tr><td><fmt:message key="sample_file"/></td>
			<td><input type="file" name="file"/></td></tr>
		<tr><td><fmt:message key="dvd.type"/></td>
			<td><select name="dvd.type">
				<c:forEach items="${dvdTypes}" var="type">
					<option value="${type }"><fmt:message key="${type }"/></option>
				</c:forEach>
			</select></td></tr>
		<tr><td><input type="submit" value="send"/></td></tr>
	</table>
</form>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="search_dvds"/></h1>
<hr/>
<form action="<c:url value="/dvds/search"/>">
	<table>
		<tr><td><fmt:message key="dvd.title"/></td>
			<td><input type="text" name="dvd.title" value="${dvd.title }"/></td></tr>
		<tr><td><input type="submit" value="search"/></td></tr>		
	</table>
</form>
</div>

<br/><br/>

<div class="blue-box">
<h1><fmt:message key="list_users"/></h1>
<hr/>
<a href="<c:url value="/users"/>"><fmt:message key="search"/></a>
</div>

<%@ include file="/footer.jsp" %> 