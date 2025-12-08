<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mjdi.quiz.QuizDAO" %>

<%
    // 오늘의 퀴즈 세팅
    QuizDAO.getInstance().checkAndSetGlobalQuiz(application);
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MNU 일본어 사전</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <section class="search-section">
        <div class="inner">
            <div class="title-area">
                <h1>일본어사전</h1>
                <p class="sub-title">MNU와 함께하는 스마트한 일본어 단어 학습</p>
            </div>
            
            <div class="search-box">
                <form action="WordController" method="GET" autocomplete="off">
                    <input type="hidden" name="cmd" value="word_search">
                    <input type="text" id="searchInput" name="query" value="${searchQuery}" 
                           placeholder="단어, 뜻을 입력해보세요" class="search-input">
                    <button type="submit" class="search-btn">검색</button>
                    
                    <div id="autoBox" class="auto-box"></div>
                </form>
            </div>
        </div>
    </section>

    <section class="daily-section">
        <div class="inner center-box">
            
            <c:choose>
                <%-- [CASE A] 검색 결과가 있을 때 --%>
                <c:when test="${not empty wordList}">
                    <div style="width:100%; max-width:850px;">
                        <h3 style="margin-bottom: 20px; color:#555; font-size:18px;">
                            '<span style="color:#0C4DA1; font-weight:bold;">${searchQuery}</span>' 검색 결과
                        </h3>
                        <ul class="result-list">
                            <c:forEach var="w" items="${wordList}">
                                <li class="result-item">
                                    <a href="WordController?cmd=word_view&word_id=${w.word_id}">
                                        <div>
                                            <span class="word">${w.word}</span>
                                            <span class="doc">[${w.doc}]</span>
                                        </div>
                                        <span class="korean">${w.korean}</span>
                                        <span class="jlpt">${w.jlpt}</span>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:when>
                
                <%-- [CASE B] 검색 결과가 없을 때 --%>
                <c:when test="${not empty searchQuery and empty wordList}">
                    <div class="no-result">
                        <p style="margin-bottom:15px; font-size:18px;">검색 결과가 없습니다.</p>
                        <a href="WordController?cmd=main" class="btn-action peri">메인으로 돌아가기</a>
                        <br><br>
                        <a href="request_word.jsp" style="color:#00A295; font-weight:bold; text-decoration:underline;">+ 없는 단어 등록 신청하기</a>
                    </div>
                </c:when>

                <%-- [CASE C] 메인 대시보드 --%>
                <c:otherwise>
                    <div class="card-container">
                        
                        <article class="card quiz-card">
                            <div class="card-header">TODAY'S QUIZ</div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty applicationScope.todayQuiz}">
                                        <h2 class="quiz-question">${applicationScope.todayQuiz.word}</h2>
                                        <p class="quiz-desc">이 단어의 올바른 뜻은 무엇일까요?</p>
                                        <a href="QuizController?cmd=daily_quiz" class="btn-action peri">정답 맞히기 ></a>
                                    </c:when>
                                    <c:otherwise>
                                        <h2 class="quiz-question" style="color:#ccc;">Loading...</h2>
                                        <p class="quiz-desc">퀴즈 데이터를 불러오는 중입니다.</p>
                                        <a href="index.jsp" style="color:#666; font-size:13px;">↻ 새로고침</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>

                        <article class="card jlpt-card">
                            <div class="card-header">JLPT VOCABULARY</div>
                            <div class="card-body">
                                <div style="margin-bottom:20px; display:flex; flex-wrap:wrap; justify-content:center; gap:5px;">
                                    <a href="WordController?cmd=word_search&query=N1" class="btn-level">N1</a>
                                    <a href="WordController?cmd=word_search&query=N2" class="btn-level">N2</a>
                                    <a href="WordController?cmd=word_search&query=N3" class="btn-level">N3</a>
                                    <a href="WordController?cmd=word_search&query=N4" class="btn-level">N4</a>
                                    <a href="WordController?cmd=word_search&query=N5" class="btn-level">N5</a>
                                </div>
                                <p style="color:#999; font-size:14px;">
                                    급수를 선택하면 해당 단어 목록을 볼 수 있습니다.
                                </p>
                            </div>
                        </article>

                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </section>

   <script>
   // 자동완성 스크립트 (디자인 수정됨)
   const searchInput = document.getElementById("searchInput");
   const autoBox = document.getElementById("autoBox");

   searchInput.addEventListener("keyup", function() {
       const key = this.value.trim();
       if (key.length === 0) {
           autoBox.innerHTML = "";
           autoBox.style.display = "none";
           return;
       }

       fetch("WordController?cmd=auto_complete&key=" + encodeURIComponent(key))
           .then(res => res.json())
           .then(data => {
               autoBox.innerHTML = "";
               if (!data || data.length === 0) {
                   autoBox.style.display = "none";
               } else {
                   // 자동완성 박스 스타일
                   autoBox.style.cssText = `
                       display: block !important;
                       position: absolute !important;
                       top: 65px !important;
                       left: 0 !important;
                       width: 100% !important;
                       background-color: white !important;
                       border: 1px solid #ddd !important;
                       border-radius: 0 0 15px 15px;
                       box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                       z-index: 99999 !important;
                       overflow: hidden;
                   `;

                   data.forEach(item => {
                       const word = item.word; 
                       const korean = item.korean;
                       const div = document.createElement("div");
                       
                       // 아이템 스타일
                       div.style.cssText = `
                           padding: 12px 20px;
                           border-bottom: 1px solid #f5f5f5;
                           cursor: pointer;
                           color: #333;
                           font-size: 15px;
                           background: white;
                           text-align: left;
                       `;
                       
                       // 매칭되는 글자 강조 (옵션)
                       div.innerHTML = "<span style='font-weight:bold; color:#0C4DA1;'>" + word + "</span> <span style='color:#888; font-size:13px; margin-left:8px;'>" + korean + "</span>";

                       div.addEventListener("click", () => {
                           searchInput.value = word;
                           autoBox.style.display = "none";
                       });
                       
                       // 마우스 오버 시 MNU Green 배경
                       div.onmouseover = function() { this.style.backgroundColor = "#e0f2f1"; this.style.color = "#00A295"; };
                       div.onmouseout = function() { this.style.backgroundColor = "#fff"; this.style.color = "#333"; };

                       autoBox.appendChild(div);
                   });
               }
           })
           .catch(err => console.error("에러:", err));
   });
   
   document.addEventListener("click", function(e) {
       if (e.target !== searchInput && e.target !== autoBox) {
           autoBox.style.display = "none";
       }
   });
   </script>
</body>
</html>