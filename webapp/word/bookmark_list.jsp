<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.mjdi.user.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 세션에서 유저 정보 가져오기
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");

    // 현재 테마 결정
    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 공통 스타일 + 테마 스타일 경로
    String ctx      = request.getContextPath();
    String baseCss  = ctx + "/style/style.css";           // 공통 레이아웃
    String userCss  = ctx + "/style/user.css";            // 마이페이지/북마크 전용
    String themeCss = null;                               // 테마 (있을 때만)
    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>나만의 단어장 - My J-Dic</title>

    <!-- 1) 항상 공통 style.css -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <!-- 2) 마이페이지/북마크 전용 user.css -->
    <link rel="stylesheet" href="<%= userCss %>">
    <!-- 3) 테마가 default가 아닐 때만 색/변수 덮어쓰기 -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>
</head>
<body class="bookmark-page">
    <jsp:include page="/include/header.jsp" />

    <div class="bookmark-wrap">
        <div class="bookmark-header">
            <div>
                <h2 class="bookmark-title">📔 나만의 즐겨찾기 단어장</h2>
                <p class="bookmark-count">
                    <c:choose>
                        <c:when test="${not empty bookmarkList}">
                            총 <strong>${bookmarkList.size()}</strong>개의 즐겨찾기 단어가 있습니다.
                        </c:when>
                        <c:otherwise>
                            아직 즐겨찾기한 단어가 없습니다.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>

        <ul class="bookmark-list">
            <c:choose>
                <c:when test="${not empty bookmarkList}">
                    <c:forEach var="w" items="${bookmarkList}">
                        <li class="bookmark-item">
                            <a class="bookmark-link"
                               href="${pageContext.request.contextPath}/WordController?cmd=word_view&word_id=${w.word_id}">
                                <div class="bookmark-row-top">
                                    <div class="bookmark-word-box">
                                        <span class="word">${w.word}</span>
                                        <span class="doc">[${w.doc}]</span>
                                    </div>
                                    <span class="jlpt-badge">${w.jlpt}</span>
                                </div>
                                <div class="bookmark-row-bottom">
                                    <span class="korean">${w.korean}</span>
                                </div>
                            </a>
                            <a href="${pageContext.request.contextPath}/WordController?cmd=bookmark_toggle&word_id=${w.word_id}" 
                               onclick="return confirm('즐겨찾기에서 삭제하시겠습니까?');"
                               class="bookmark-remove">
                               ✕
                            </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-bookmark">
                        <p>즐겨찾기한 단어가 아직 없습니다.</p>
                        <a href="${pageContext.request.contextPath}/WordController?cmd=main" class="btn-bookmark-main">
                            단어 찾아보기
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </ul>
        
        <div class="bookmark-footer">
            <a href="${pageContext.request.contextPath}/mypage.jsp" class="btn-bookmark-back">
                메인으로 돌아가기
            </a>
        </div>
    </div>
</body>
</html>
