<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // 1. 세션에서 유저 정보 가져오기
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    // 2. 현재 테마 결정
    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 3. 공통/디자인/테마 CSS 경로
    String baseCss   = ctx + "/style/style.css";        // 공통 레이아웃
    String designCss = ctx + "/style/design.css";       // 관리자/테이블 전용
    String themeCss  = null;                            // 테마(있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항</title>

    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= designCss %>">
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        /* 상단고정 버튼 스타일 – 테마 컬러 사용 */
        .btn-top {
            padding: 3px 8px;
            background-color: var(--mnu-blue);
            color: #fff;
            border-radius: 6px;
            border: none;
            font-size: 12px;
            cursor: pointer;
            margin-left: 5px;
            transition: 0.2s;
        }
        .btn-top:hover {
            background-color: var(--mnu-green);
        }

        /* 공지 라벨 컬럼 살짝 정렬 */
        .notice-label-col {
            text-align: center;
            font-size: 13px;
        }

        .top-btn-col {
            text-align: center;
        }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">

        <div class="table-section">
            <div class="section-title">
                <span>📢 공지사항</span>
                <a href="<%= ctx %>/index.jsp" class="btn-home">메인 홈</a>
            </div>

            <table class="req-table">
                <thead>
                    <tr>
                        <th style="width:100px;"></th>		<th>제목</th>
                        <th style="width:100px;"></th>		<th style="width:180px;">작성자</th>
                        <th style="width:180px;">작성일</th>
                    </tr>
                </thead>

                <tbody>
                    <c:choose>
                        <c:when test="${not empty list}">
                            <c:forEach var="n" items="${list}">
                                <tr>
                                    <td class="notice-label-col">
                                        <c:if test="${n.is_top == 1}">
                                            <span style="color:var(--mnu-blue); font-weight:bold;">📌 공지</span>
                                        </c:if>
                                    </td>

                                    <td style="font-weight:600; color:var(--mnu-blue);">
                                        <a href="${pageContext.request.contextPath}/NoticeController?cmd=notice_view&idx=${n.idx}">
                                            ${n.title}
                                        </a>
                                    </td>

                                    <td class="top-btn-col">
                                        <c:if test="${sessionScope.sessionUser != null && sessionScope.sessionUser.jdi_role == 'ADMIN'}">
                                            <form action="${pageContext.request.contextPath}/NoticeController" method="post" style="display:inline;">
                                                <input type="hidden" name="cmd" value="notice_top">
                                                <input type="hidden" name="idx" value="${n.idx}">
                                                <input type="hidden" name="isTop" value="${n.is_top == 1 ? 'false' : 'true'}">
                                                <button type="submit" class="btn-top">
                                                    ${n.is_top == 1 ? '상단해제' : '상단고정'}
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>

                                    <td>${n.writer}</td>
                                    
                                    <td>${fn:substring(n.created_at,0,10)}</td>
                                </tr>
                            </c:forEach>
                        </c:when>

                        <c:otherwise>
                            <tr>
                                <td colspan="5" style="padding:40px; color:#999; text-align:center;">
                                    등록된 공지사항이 없습니다.
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