<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>CoderBy</title>
</head>
<body>
<h1>정보</h1>
${rData}<p>
${rData.colNames}<p>
${rData.data}<p>
<table border="1">
<tr>
<c:forEach var="colName" items="${rData.colNames}">
<th>${colName}</th>
</c:forEach>
</tr>
<c:forEach var="i" begin="0" end="${fn:length(rData.data[0])-1}">
<c:if test="${i le 1000}">
<c:set  var="row" value="${rData.data}" /> 
<tr>
<c:forEach var="data" items="${row}">
<td>${data[i]}</td>
</c:forEach>
</tr>
</c:if>
</c:forEach>
</table>
</body>
</html>