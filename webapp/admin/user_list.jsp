<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원 목록 관리</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
    <style>
        .user-table th, .user-table td { text-align: center; }
        .user-table td { font-size: 13px; padding: 10px 5px; }
        .badge-block { color: red; font-weight: bold; }
        .badge-active { color: green; font-weight: bold; }
    </style>
</head>
<body>

    <div class="admin-container">
        
        <div class="table-section">
            <div class="section-title">
                <span>👥 전체 회원 목록 조회</span>
                <a href="${pageContext.request.contextPath}/adminMain.apply" class="btn-home">관리자 홈</a>
            </div>

            <table class="req-table user-table">
                <thead>
                    <tr>
                        <th style="width:10%;">아이디</th>
                        <th style="width:10%;">이름</th>
                        <th style="width:20%;">이메일</th>
                        <th style="width:15%;">전화번호</th>
                        <th style="width:10%;">상태</th>
                        <th style="width:25%;">관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty userList}">
                            <c:forEach var="user" items="${userList}">
                                <tr>
                                    <td>${user.jdi_user}</td>
                                    <td>${user.jdi_name}</td>
                                    <td>${user.jdi_email}</td>
                                    <td>${user.jdi_phone}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.jdi_status eq 'BLOCK'}">
                                                <span class="badge-block">차단됨</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-active">활동중</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <!-- 메세지 전송 -->
                                        <button class="btn-ok" onclick="sendWarning('${user.jdi_user}')" style="background:#ff9800; margin-right:5px;">경고</button>
                                        
                                        <!-- 차단/해제 토글 -->
                                        <c:choose>
                                            <%-- 관리자 본인은 차단 불가 --%>
                                            <c:when test="${user.jdi_role eq 'ADMIN'}">
                                                <span style="color:#999; font-size:12px;">(관리자)</span>
                                            </c:when>
                                            
                                            <%-- 차단 상태면 -> 해제 버튼 --%>
                                            <c:when test="${user.jdi_status eq 'BLOCK'}">
                                                <button class="btn-ok" onclick="if(confirm('차단을 해제하시겠습니까?')) location.href='${pageContext.request.contextPath}/userBlock.do?id=${user.jdi_user}&action=active'">해제</button>
                                            </c:when>
                                            
                                            <%-- 활동 상태면 -> 차단 버튼 --%>
                                            <c:otherwise>
                                                <button class="btn-no" onclick="if(confirm('정말 차단하시겠습니까?')) location.href='${pageContext.request.contextPath}/userBlock.do?id=${user.jdi_user}&action=block'">차단</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" style="padding: 50px; color: #999;">등록된 회원이 없습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

<script>
function sendWarning(userId) {
    const msg = prompt(userId + "님에게 보낼 경고/알림 내용을 입력하세요:");
    if(msg) {
        location.href = "${pageContext.request.contextPath}/msgSend.do?receiver=" + userId + "&content=" + encodeURIComponent(msg);
    }
}
</script>
</body>
</html>