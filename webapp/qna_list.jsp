<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // 1. 세션에서 유저 정보 가져오기 & 테마 설정
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 2. CSS 경로 설정
    String baseCss   = ctx + "/style/style.css";        // 공통 레이아웃
    String designCss = ctx + "/style/design.css";       // 게시판/테이블 전용 스타일
    String themeCss  = null;                            // 테마

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Q&A 게시판</title>

    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= designCss %>">
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        /* 질문하기 버튼 스타일 */
        .btn-write {
            display: inline-block;
            padding: 5px 12px;
            background-color: var(--mnu-blue, #0C4DA1);
            color: #fff;
            border-radius: 4px;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            transition: 0.2s;
            margin-right: 5px;
        }
        .btn-write:hover {
            background-color: var(--mnu-green, #00A295);
            filter: brightness(1.1);
        }

        /* 제목 링크 스타일 */
        .qna-link {
            text-decoration: none;
            color: #333;
            font-weight: 500;
            transition: color 0.2s;
        }
        .qna-link:hover {
            color: var(--mnu-blue, #0C4DA1);
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container"> <div class="table-section">
            <div class="section-title" style="display:flex; justify-content:space-between; align-items:center;">
                <span>❓ Q&A 게시판</span>
                <div>
                    <a href="QnAController?cmd=qna_write" class="btn-write">➕ 질문하기</a>
                    <a href="<%= ctx %>/index.jsp" class="btn-home">🏠 메인</a>
                </div>
            </div>

            <table class="req-table"> <colgroup>
                    <col style="width: 80px;">  <col style="width: auto;">  <col style="width: 120px;"> <col style="width: 120px;"> <col style="width: 80px;">  </colgroup>
                <thead>
                    <tr>
                        <th>No</th>
                        <th>제목</th>
                        <th>작성자</th>
                        <th>작성일</th>
                        <th>조회</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${not empty list}">
                            <c:forEach var="dto" items="${list}">
                                <tr>
                                    <td style="text-align:center;">${dto.q_id}</td>

                                    <td style="text-align:left; padding-left:20px;">
                                        <a href="QnAController?cmd=qna_view&q_id=${dto.q_id}" class="qna-link">
                                            ${dto.title}
                                            <c:if test="${dto.answer_count > 0}">
                                                <span style="color:#ff5c8d; font-size:12px; font-weight:bold;">
                                                    [${dto.answer_count}]
                                                </span>
                                            </c:if>
                                        </a>
                                    </td>

                                    <td style="text-align:center;">${dto.writer_name}</td>

                                    <td style="text-align:center;">
                                        ${fn:substring(dto.created_at, 0, 10)}
                                    </td>

                                    <td style="text-align:center;">${dto.view_count}</td>
                                </tr>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <tr>
                                <td colspan="5" style="padding:50px; color:#999; text-align:center; line-height: 1.6;">
                                    등록된 질문이 없습니다.<br>
                                    궁금한 점이 있다면 첫 번째 질문을 남겨보세요!
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