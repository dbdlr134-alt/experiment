<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.mjdi.user.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 세션에서 유저 정보 가져오기
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    // 현재 테마 결정
    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 공통 스타일 + 테마 스타일 경로
    String baseCss  = ctx + "/style/style.css";              // 공통 레이아웃/기본 스타일
    String themeCss = null;                                  // 테마 (있을 때만)
    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>나만의 일본어 사전 (JSP Ver.)</title>

    <!-- 공통 스타일 -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <!-- 테마 스타일 (default가 아닐 때만) -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <!-- 공통 헤더 사용 -->
    <jsp:include page="/include/header.jsp" />

    <section class="search-section">
        <div class="inner">
            <div class="title-area">
                <h1>일본어사전</h1>
                <p class="sub-title">나만의 단어장으로 실력 향상!</p>
            </div>
            <div class="search-box">
                <form action="<%= ctx %>/WordController" method="GET">
                    <input type="hidden" name="cmd" value="word_search">
                    <input type="text" name="query" value="${searchQuery}"
                           placeholder="단어, 뜻을 입력해보세요" class="search-input">
                    <button type="submit" class="search-btn">검색</button>
                </form>
            </div>
        </div>
    </section>

    <section class="daily-section">
        <div class="inner center-box">
            
            <c:choose>
                <%-- 1. 검색 결과가 있는 경우 --%>
                <c:when test="${not empty wordList}">
                    <h3 style="margin-bottom: 15px; color:#555;">
                        '<span style="color:#0C4DA1; font-weight:bold;">${searchQuery}</span>' 검색 결과
                    </h3>
                    <ul class="result-list">
                        <c:forEach var="w" items="${wordList}">
                            <li class="result-item">
                                <span class="word">${w.word}</span>
                                <span class="doc">[${w.doc}]</span>
                                <span class="korean">${w.korean}</span>
                                <span class="jlpt">${w.jlpt}</span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                
                <%-- 2. 검색은 했는데 결과가 없는 경우 --%>
                <c:when test="${not empty searchQuery and empty wordList}">
                    <div class="no-result">
                        <p>'${searchQuery}'에 대한 검색 결과가 없습니다 ㅠㅠ</p>
                    </div>
                </c:when>

                <%-- 3. 아무것도 안 했을 때: 아래 카드들만 노출 --%>
            </c:choose>

            <!-- 오늘의 퀴즈 카드 -->
            <article class="card quiz-card">
                <div class="card-header">오늘의 퀴즈</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty sessionScope.quizWord}">
                            <h2 class="quiz-question">${sessionScope.quizWord.word}</h2>
                            <p class="quiz-desc">이 단어의 올바른 뜻은?</p>
                        </c:when>
                        <c:otherwise>
                            <p style="color:#999; padding: 20px 0;">
                                오늘의 단어를 불러오는 중...<br>
                                (홈 버튼을 눌러주세요!)
                            </p>
                        </c:otherwise>
                    </c:choose>
                    <a href="<%= ctx %>/quiz.jsp" class="btn-action peri">퀴즈 풀러 가기 ></a>
                </div>
            </article>

            <!-- JLPT 급수 카드 -->
            <article class="card jlpt">
                <div class="card-header">JLPT 급수별 단어</div>
                <div class="card-body">
                    <a href="<%= ctx %>/WordController?cmd=word_search&query=N1" class="level-btn">N1</a>
                    <a href="<%= ctx %>/WordController?cmd=word_search&query=N2" class="level-btn">N2</a>
                    <a href="<%= ctx %>/WordController?cmd=word_search&query=N3" class="level-btn">N3</a>
                    <a href="<%= ctx %>/WordController?cmd=word_search&query=N4" class="level-btn">N4</a>
                    <a href="<%= ctx %>/WordController?cmd=word_search&query=N5" class="level-btn">N5</a>
                    <a href="<%= ctx %>/quiz.jsp" class="btn-action peri">JLPT 급수별 단어 보러가기</a>
                </div>
            </article>

        </div>
    </section>

</body>
</html>
