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
    String baseCss   = ctx + "/style/style.css";      // 공통 레이아웃
    String designCss = ctx + "/style/design.css";     // 관리자/테이블 전용
    String themeCss  = null;                          // 테마(있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항 상세보기</title>

    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= designCss %>">
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        /* 공지 상세 전용 살짝 정리 */
        .notice-detail-title {
            color: var(--mnu-blue);
            font-weight: 700;
            margin-bottom: 10px;
        }
        .notice-detail-meta {
            display: flex;
            justify-content: space-between;
            color: #999;
            font-size: 14px;
            margin-bottom: 20px;
        }
        .notice-detail-body {
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background: #f9f9f9;
            line-height: 1.6;
            min-height: 200px; /* 본문 높이 최소값 확보 */
        }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">

        <div class="table-section" style="max-width:800px; margin:auto;">
            <div class="section-title">
                <span>📢 공지사항</span>
                <a href="<%= ctx %>/NoticeController?cmd=notice_list" class="btn-home">목록으로 돌아가기</a>
            </div>

            <div class="notice-detail">
                <h2 class="notice-detail-title">
                    ${dto.title}
                </h2>

                <div class="notice-detail-meta">
                    <span>작성자: ${dto.writer}</span>
                    <span>작성일: ${fn:substring(dto.created_at,0,10)}</span>
                </div>

                <div class="notice-detail-body">
                    ${dto.content} </div>
                
                <%-- 관리자만 삭제/수정 버튼 --%>
                <c:if test="${sessionScope.sessionUser != null && sessionScope.sessionUser.jdi_role == 'ADMIN'}">
                    <div style="margin-top:20px; text-align:right;">
                        <a href="<%= ctx %>/NoticeController?cmd=notice_delete&idx=${dto.idx}"
                           onclick="return confirm('정말 삭제하시겠습니까?');"
                           class="btn-home" style="margin-right:10px; background-color:#d32f2f;">
                           삭제
                        </a>
                        <a href="<%= ctx %>/NoticeController?cmd=notice_modify&idx=${dto.idx}"
                           class="btn-home">
                           수정
                        </a>
                    </div>
                </c:if>
            </div>

        </div>
    </div>

</body>
</html>