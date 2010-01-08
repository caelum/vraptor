<%@ include file="/header.jsp" %> 

<h1>${userInfo.user.name}: <fmt:message key="your_dvds"/></h1>

<table>
	<thead>
		<tr>
			<td>Title</td>
			<td>Description</td>
			<td>Type</td>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="dvd" items="${userInfo.user.dvds}" varStatus="s">
			<tr class="${s.count % 2 == 0? 'even': 'odd' }">
				<td>${dvd.title}</td>
				<td>${dvd.description}</td>
				<td><fmt:message key="${dvd.type}"/></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<form action="<c:url value="/dvds"/>" enctype="multipart/form-data" name="dvdRegister" method="post">
	<fieldset>
		<legend><fmt:message key="new_dvd"/></legend>
		<label for="title"><fmt:message key="dvd.title"/></label>
		<input type="text" id="title" name="dvd.title" value="${dvd.title }"/>
		<label for="description"><fmt:message key="dvd.description"/></label>
		<input type="text" id="description" name="dvd.description" value="${dvd.title }"/>
		<label for="file"><fmt:message key="sample_file"/></label>
		<input type="file" id="file" name="file"/>
		<label for="type"><fmt:message key="dvd.type"/></label>
		<select name="dvd.type" id="type">
			<c:forEach items="${dvdTypes}" var="type">
				<option value="${type }"><fmt:message key="${type }"/></option>
			</c:forEach>
		</select>
		<button type="submit" id="send"><fmt:message key="send"/></button>
	</fieldset>
</form>

<%@ include file="/footer.jsp" %> 