<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Q&A 문의 관리 - My J-Dic Admin</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
    
    <style>
        /* 상태 뱃지용 추가 스타일 (기존 디자인과 어울리게) */
        .status-badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; display: inline-block; }
        .status-wait { background-color: #ffebee; color: #c62828; } /* 답변 대기 (붉은색 계열) */
        .status-done { background-color: #e8f5e9; color: #2e7d32; } /* 답변 완료 (초록색 계열) */
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">

        <div class="table-section">
            <div class="section-title">
                <span>❓ Q&A 문의 목록</span>
                <a href="${pageContext.request.contextPath}/admin/main.jsp" class="btn-home">관리자 홈</a>
            </div>

            <table class="req-table">
                <thead>
                    <tr>
                        <th width="8%">번호</th>
                        <th width="10%">상태</th>
                        <th>제목</th> <th width="12%">작성자</th>
                        <th width="15%">작성일</th>
                        <th width="10%">관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty qnaList}">
                            <c:forEach var="dto" items="${qnaList}">
                                <tr>
                                    <td>${dto.q_id}</td>
                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${dto.answer_count > 0}">
                                                <span class="status-badge status-done">답변완료</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge status-wait">답변대기</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <td style="text-align: left; padding-left: 20px;">
                                        <a href="${pageContext.request.contextPath}/QnAController?cmd=admin_answer_view&q_id=${dto.q_id}" class="title-link">
                                            ${dto.title}
                                        </a>
                                    </td>
                                    
                                    <td>${dto.writer_user}</td>
                                    <td>${dto.created_at}</td>
                                    
                                    <td>
                                        <button class="btn-ok"
                                                style="background-color: #555; border-color: #555;" 
                                                onclick="location.href='${pageContext.request.contextPath}/QnAController?cmd=admin_answer_view&q_id=${dto.q_id}'">
                                            답변
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        
                        <%-- 데이터가 없을 때 --%>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" style="padding: 40px; color: #999; text-align:center;">
                                    등록된 Q&A 문의가 없습니다.
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