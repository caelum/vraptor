<%--
  @author Fabio Kung
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>My Dogs</title></head>
<body>
<h1>This is my dog list</h1>
<ul>
  <c:forEach itens="${dogs}" var="dog">
    <li>${dog.name}</li>
  </c:forEach>
</ul>
</body>
</html>