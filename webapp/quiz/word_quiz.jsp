<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>나만의 일본어 사전 (JSP Ver.)</title>
    <link rel="stylesheet" href="style/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <header class="top-header">
        <div class="inner">
            <div class="logo"><a href="">My J-Dic</a></div>
            <nav class="util-nav">
                <a href="login.jsp" class="btn-login">로그인</a>
                <a href="#" class="btn-menu">:::</a>
            </nav>
        </div>
    </header>

    <section class="search-section">
        <div class="title-area">
            <h1>일본어사전</h1>
            <p class="sub-title">나만의 단어장으로 실력 향상!</p>
        </div>
        <div class="search-box">
            <form action="WordController" method="GET">
            	<input type="hidden" name="cmd" value="word_search">
                <input type="text" name="query" value="${searchQuery}" placeholder="단어, 뜻을 입력해보세요" class="search-input">
                <button type="submit" class="search-btn">검색</button>
            </form>
        </div>
    </section>

    <section class="daily-section">
        <div class="inner center-box">
            
            <c:choose>
                <%-- 1. 검색 결과가 있는 경우 (wordList가 비어있지 않음) --%>
                <c:when test="${not empty wordList}">
                    <h3 style="margin-bottom: 15px; color:#555;">'${searchQuery}' 검색 결과</h3>
                    <ul class="result-list">
                        <c:forEach var="w" items="${wordList}">
                            <li class="result-item">
                                <a class="word">${w.word}</a>
                                <a class="doc">[${w.doc}]</a>
                                <a class="korean">${w.korean}</a>
                                <a class="jlpt">${w.jlpt}</a>
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

                <%-- 3. 아무것도 안 했을 때 (처음 접속) -> 오늘의 퀴즈 보여주기 --%>
            </c:choose>
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
                            <a href="quiz.jsp" class="btn-action peri">퀴즈 풀러 가기 ></a>
                        </div>
                    </article>
             <article class="card jlpt">
                 <div class="card-header">jlpt급수별 단어</div>
                 <div class="card-body">
                    <a href="WordController?cmd=word_search&query=N1" class="level-btn">N1</a>
			        <a href="WordController?cmd=word_search&query=N2" class="level-btn">N2</a>
			        <a href="WordController?cmd=word_search&query=N3" class="level-btn">N3</a>
			        <a href="WordController?cmd=word_search&query=N4" class="level-btn">N4</a>
			        <a href="WordController?cmd=word_search&query=N5" class="level-btn">N5</a>
                     <a href="quiz.jsp" class="btn-action peri">jlpt 급수별 단어 보러가기</a>
                 </div>
             </article>

        </div>
    </section>

</body>
</html>