<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>단어 수정 요청 관리</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">

        <div class="table-section">
            <div class="section-title">
                <span>🛠️ 단어 수정 요청 대기열</span>
                <a href="${pageContext.request.contextPath}/adminMain.apply" class="btn-home">관리자 홈</a>
            </div>

            <table class="req-table">
                <thead>
                    <tr>
                        <th>요청번호</th>
                        <th>요청자(ID)</th>
                        <th>단어</th>
                        <th>요미가나</th>
                        <th>한국어 뜻</th>
                        <th>JLPT</th>
                        <th>요청일</th>
                        <th>처리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty list}">
                            <c:forEach var="dto" items="${list}">
                                <tr>
                                    <td>${dto.reqId}</td>
                                    <td>${dto.jdiUser}</td>
                                    <td style="font-weight:bold; color:var(--mnu-blue);">
                                        ${dto.word}
                                    </td>
                                    <td>${dto.doc}</td>
                                    <td>${dto.korean}</td>
                                    <td>
                                        <span style="background:#eef3ff; padding:3px 6px; border-radius:4px; font-size:11px; font-weight:bold;">
                                            ${dto.jlpt}
                                        </span>
                                    </td>
                                    <td>${dto.regDate}</td>
                                    <td>
                                        <button class="btn-ok"
                                                onclick="if (confirm('수정 내용을 반영하시겠습니까?')) 
                                                         location.href='${pageContext.request.contextPath}/approveEdit.apply?id=${dto.reqId}'">
                                            승인
                                        </button>
                                        <button class="btn-no"
                                                onclick="if (confirm('정말 거절하시겠습니까?')) 
                                                         location.href='${pageContext.request.contextPath}/rejectEdit.apply?id=${dto.reqId}'">
                                            거절
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="padding: 40px; color: #999; text-align:center;">
                                    수정 대기 중인 항목이 없습니다.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

    </div>

</body>
</html>
