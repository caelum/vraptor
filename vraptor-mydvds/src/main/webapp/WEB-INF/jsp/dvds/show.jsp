<%@ include file="/header.jsp" %> 

<div class="blue-box" id="message">
<h1>${dvd.title} <fmt:message key="dvd_added"/></h1>

<p><strong>Description:</strong> ${dvd.description }</p>

<p><strong>Owners:</strong></p>
<ul>
	<c:forEach items="${dvd.copies}" var="copy">
		<li>${copy.owner }</li>
	</c:forEach>
</ul>
</div>

<%@ include file="/footer.jsp" %> 