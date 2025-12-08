<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="com.mjdi.user.PointDAO" %>
<%@ page import="com.mjdi.quiz.QuizDAO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    if(myUser == null) { response.sendRedirect("login.jsp"); return; }
    
    String userId = myUser.getJdi_user();
    
    // 포인트 & 통계 조회
    int currentPoint = PointDAO.getInstance().getTotalPoint(userId);
    int wrongWords = QuizDAO.getInstance().getIncorrectCount(userId);
    int mySolveCount = QuizDAO.getInstance().getMySolveCount(userId);
    
    int correctCount = mySolveCount - wrongWords;
    if(correctCount < 0) correctCount = 0; 
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - My J-Dic</title>
    
    <!-- CSS 로딩 로직 -->
    <%
        String currentTheme = (myUser.getJdi_theme() != null) ? myUser.getJdi_theme() : "default";
        String cssPath = request.getContextPath() + "/style/style.css";
        if (!"default".equals(currentTheme)) {
            cssPath = request.getContextPath() + "/style/" + currentTheme + "/style.css";
        }
    %>
    <link rel="stylesheet" href="<%= cssPath %>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="mypage-container">
        
        <!-- 왼쪽: 프로필 카드 -->
        <div class="profile-card">
            <div class="point-badge">
                💰 <%= String.format("%,d", currentPoint) %> P
            </div>
            <div class="profile-img-box">
                <img src="${pageContext.request.contextPath}/images/<%= myUser.getJdi_profile() %>" alt="프로필">
            </div>
            <h2 class="user-name"><%= myUser.getJdi_name() %></h2>
            <p class="user-email"><%= myUser.getJdi_email() %></p>
            
            <a href="pwd_check.jsp" class="btn-mypage btn-gray">내 정보 수정 ></a>
            
            <!-- 오답노트 (테마 포인트 컬러 적용) -->
            <a href="${pageContext.request.contextPath}/QuizController?cmd=quiz_incorrect" class="btn-mypage" style="border:1px solid var(--chart-color-wrong); color:var(--chart-color-wrong); background:#fff;">
                📝 오답노트 확인 (<%= wrongWords %>개)
            </a>
            
            <!-- 즐겨찾기 (하드코딩된 금색 제거 -> 테마 서브 컬러 적용) -->
            <a href="${pageContext.request.contextPath}/WordController?cmd=bookmark_list" class="btn-mypage" style="border:1px solid var(--mnu-green); color:var(--mnu-green); background:#fff;">
                ⭐ 즐겨찾기 단어장
            </a>
            
            <!-- 테마 상점 (테마 메인 컬러 적용) -->
          	<a href="${pageContext.request.contextPath}/theme_store.jsp" class="btn-mypage" style="background:#fff; border:1px solid var(--mnu-blue); color:var(--mnu-blue);">
		    	🎨 테마 상점 가기
			</a>

            <a href="${pageContext.request.contextPath}/request/requesr_edit.jsp" class="btn-mypage btn-outline-green">
                + 단어 등록 신청
            </a>
        </div>

        <!-- 오른쪽: 학습 통계 -->
        <div class="chart-section">
            <h3 class="chart-title">나의 학습 활동</h3>
            <div style="width:300px; height:300px; position:relative;">
                <% if(mySolveCount == 0) { %>
                    <p style="text-align:center; padding-top:130px; color:#999;">
                        아직 푼 문제가 없어요.<br>퀴즈에 도전해보세요!
                    </p>
                <% } else { %>
                    <canvas id="myChart"></canvas>
                <% } %>
            </div>
             <p style="text-align:center; margin-top:20px; font-size:14px; color:#666;">
                총 <strong><%= mySolveCount %></strong>문제 풀이 / 
                <!-- 하드코딩된 빨간색 제거 -> 테마 오답 컬러 변수 적용 -->
                <span style="color:var(--chart-color-wrong); font-weight:bold;"><%= wrongWords %></span>개 오답
            </p>
        </div>
    </div>

    <% if(mySolveCount > 0) { %>
    <script>
        // 1. 현재 적용된 테마의 CSS 변수 값 읽어오기 (JavaScript가 테마를 인식하도록 함)
        const styles = getComputedStyle(document.documentElement);
        const colorCorrect = styles.getPropertyValue('--chart-color-correct').trim();
        const colorWrong = styles.getPropertyValue('--chart-color-wrong').trim();

        const ctx = document.getElementById('myChart');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['정답', '오답'],
                datasets: [{
                    data: [<%= correctCount %>, <%= wrongWords %>],
                    // 2. 읽어온 변수 값 적용 (테마에 따라 그래프 색 자동 변경)
                    backgroundColor: [colorCorrect, colorWrong], 
                    borderWidth: 0
                }]
            },
            options: { cutout: '70%', plugins: { legend: { position: 'bottom' } } }
        });
    </script>
    <% } %>
</body>
</html>