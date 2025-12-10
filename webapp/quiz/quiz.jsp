<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // 1. 세션에서 유저 정보 가져오기 (없으면 테마는 default)
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");

    String cssPath = request.getContextPath() + "/style/style.css"; // 기본 테마
    if (myUser != null && myUser.getJdi_theme() != null && !"default".equals(myUser.getJdi_theme())) {
        cssPath = request.getContextPath() + "/style/" + myUser.getJdi_theme() + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>퀴즈 풀기 - My J-Dic</title>

    <!-- 레이아웃/퀴즈 전용 스타일 먼저 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/quiz.css">
    <!-- 마지막에 테마 CSS 적용 (현재 테마에 따라 경로 변경) -->
    <link rel="stylesheet" href="<%= cssPath %>">
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="quiz-wrapper">
        <div class="quiz-card">
            
            <div class="quiz-header-text">
                [ ${empty jlpt ? '전체' : jlpt} ] 오늘의 단어 퀴즈
            </div>

            <div class="level-menu">
                <%-- ContextPath를 사용하여 경로 안전하게 지정 --%>
                <a href="${pageContext.request.contextPath}/QuizController?cmd=word_quiz&jlpt=N1" class="btn-level ${jlpt == 'N1' ? 'active' : ''}">N1</a>
                <a href="${pageContext.request.contextPath}/QuizController?cmd=word_quiz&jlpt=N2" class="btn-level ${jlpt == 'N2' ? 'active' : ''}">N2</a>
                <a href="${pageContext.request.contextPath}/QuizController?cmd=word_quiz&jlpt=N3" class="btn-level ${jlpt == 'N3' ? 'active' : ''}">N3</a>
                <a href="${pageContext.request.contextPath}/QuizController?cmd=word_quiz&jlpt=N4" class="btn-level ${jlpt == 'N4' ? 'active' : ''}">N4</a>
                <a href="${pageContext.request.contextPath}/QuizController?cmd=word_quiz&jlpt=N5" class="btn-level ${jlpt == 'N5' ? 'active' : ''}">N5</a>
            </div>

            <c:choose>
                
                <%-- [CASE A] 채점 결과 화면 --%>
                <c:when test="${not empty resultList}">
                    
                    <div class="score-circle">
                        <span class="score-val">${score}</span>
                        <span class="score-txt">점</span>
                    </div>
                    
                    <div class="result-msg">수고하셨습니다!</div>

                    <div class="stats-grid">
                        <div class="stat-box">
                            <span class="stat-num c-blue">${correctCount}</span>
                            <span class="stat-label">정답</span>
                        </div>
                        <div class="stat-box">
                            <span class="stat-num c-red">${wrongCount}</span>
                            <span class="stat-label">오답</span>
                        </div>
                        <div class="stat-box">
                            <span class="stat-num c-green">+${earnedPoints}</span>
                            <span class="stat-label">획득 포인트</span>
                        </div>
                    </div>

                    <table class="result-table">
                        <thead>
                            <tr>
                                <th>문제</th>
                                <th>내 답</th>
                                <th>정답</th>
                                <th>결과</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="res" items="${resultList}">
                                <tr>
                                    <td style="font-weight:bold; color:#0C4DA1;">${res.word}</td>
                                    <td>${res.myAns}</td>
                                    <td style="font-weight:bold;">${res.correctAns}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${res.isCorrect == 'O'}">
                                                <span class="badge badge-o">정답</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-x">오답</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/WordController?cmd=main" class="btn-sub">메인으로</a>
                        <a href="${pageContext.request.contextPath}/QuizController?cmd=quiz_incorrect" class="btn-sub btn-main" style="background-color: var(--mnu-green);">오답노트 확인</a>
                    </div>

                </c:when>


                <%-- [CASE B] 문제 풀기 화면 --%>
                <c:otherwise>
                    
                    <%-- 이미 푼 경우 (오늘의 퀴즈 등) --%>
                    <c:if test="${alreadySolved}">
                        <div style="padding: 40px 0;">
                            <h3 style="color:#555; margin-bottom:15px;">${msg}</h3>
                            <p style="color:#888;">내일 새로운 문제가 찾아옵니다!</p>
                            <p style="margin-top:20px; font-size:18px;">
                                오늘의 기록: <span style="color:var(--mnu-blue); font-weight:bold;">${savedScore}</span>
                            </p>
                            <div class="btn-group">
                                <a href="${pageContext.request.contextPath}/WordController?cmd=main" class="btn-sub btn-main">메인으로</a>
                            </div>
                        </div>
                    </c:if>

                    <%-- 문제 풀기 --%>
                    <c:if test="${!alreadySolved && not empty qlist}">
                        <form action="${pageContext.request.contextPath}/QuizController?cmd=word_quiz" method="post">
                            <input type="hidden" name="jlpt" value="${jlpt}">
                            
                            <c:if test="${not empty isDaily}">
                                <input type="hidden" name="isDaily" value="true">
                            </c:if>
                            <c:if test="${not empty isRetry}">
                                <input type="hidden" name="isRetry" value="true">
                            </c:if>

                            <c:forEach var="q" items="${qlist}" varStatus="status">
                                <div class="question-container">
                                    <span class="q-badge">Q${status.count}</span>
                                    <div class="q-word">${q.word}</div>
                                    
                                    <div class="options">
                                        <label class="option-label">
                                            <input type="radio" name="ans_${q.quiz_id}" value="1" required> 
                                            ① ${q.selection1}
                                        </label>
                                        <label class="option-label">
                                            <input type="radio" name="ans_${q.quiz_id}" value="2"> 
                                            ② ${q.selection2}
                                        </label>
                                        <label class="option-label">
                                            <input type="radio" name="ans_${q.quiz_id}" value="3"> 
                                            ③ ${q.selection3}
                                        </label>
                                        <label class="option-label">
                                            <input type="radio" name="ans_${q.quiz_id}" value="4"> 
                                            ④ ${q.selection4}
                                        </label>
                                    </div>
                                    
                                    <input type="hidden" name="correct_${q.quiz_id}" value="${q.answer}">
                                    <input type="hidden" name="word_${q.quiz_id}" value="${q.word}">
                                </div>
                            </c:forEach>

                            <button type="submit" class="btn-submit">답안지 제출하기</button>
                        </form>
                    </c:if>
                    
                    <%-- 문제가 없을 경우 --%>
                    <c:if test="${!alreadySolved && empty qlist}">
                        <div style="padding: 50px;">
                            <p style="color:#999; font-size:16px;">등록된 퀴즈가 없습니다.</p>
                            <div class="btn-group">
                                <a href="${pageContext.request.contextPath}/WordController?cmd=main" class="btn-sub">돌아가기</a>
                            </div>
                        </div>
                    </c:if>

                </c:otherwise>
            </c:choose>
        </div>
    </div>

</body>
</html>
