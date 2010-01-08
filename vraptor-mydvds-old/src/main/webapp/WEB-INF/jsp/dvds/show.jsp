<%@ include file="/header.jsp" %> 

<h1>${dvd.title}</h1>

<p><strong>Description:</strong> ${dvd.description }</p>

<p><strong>Owners:</strong></p>
<ul>
	<c:forEach items="${dvd.rents}" var="rent">
		<li>${rent.owner }</li>
	</c:forEach>
</ul>

<%@ include file="/footer.jsp" %> 