<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="com.mjdi.user.UserDAO" %>
<%@ page import="com.mjdi.user.PointDAO" %>
<%@ page import="com.mjdi.user.ThemeDTO" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 1. 로그인 체크 (서비스가 없으므로 여기서 직접 체크)
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    if(myUser == null) { 
        response.sendRedirect("login.jsp"); 
        return; 
    }
    
    String userId = myUser.getJdi_user();
    
    // 2. 데이터 직접 로드 (서비스가 하던 일을 여기서 수행)
    UserDAO uDao = UserDAO.getInstance();
    PointDAO pDao = PointDAO.getInstance();

    int currentPoint = pDao.getTotalPoint(userId);
    ArrayList<String> myThemes = uDao.getMyThemes(userId);
    ArrayList<ThemeDTO> allThemes = uDao.getAllThemes();
    
    // 3. 현재 적용된 테마 확인 (없으면 default)
    String userTheme = myUser.getJdi_theme();
    if(userTheme == null) userTheme = "default";
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>테마 상점 - My J-Dic</title>
    
    <!-- 공통 CSS 로딩 로직 -->
    <%
        String cssPath = request.getContextPath() + "/style/style.css";
        if (!"default".equals(userTheme)) {
            cssPath = request.getContextPath() + "/style/" + userTheme + "/style.css";
        }
    %>
    <link rel="stylesheet" href="<%= cssPath %>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
    
    <style>
        .store-container { max-width: 900px; margin: 60px auto; padding: 0 20px; }
        .store-header { text-align: center; margin-bottom: 40px; }
        .store-header h2 { font-size: 32px; color: var(--mnu-blue); margin-bottom: 10px; }
        .my-point-box { 
            display: inline-block; background: #f5f5f5; 
            padding: 10px 20px; border-radius: 30px; 
            font-size: 16px; color: #555; 
        }
        .my-point-box strong { color: var(--mnu-green); font-size: 18px; }

        .theme-grid { 
            display: grid; 
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); 
            gap: 25px; 
        }
        
        .theme-card {
            background: #fff; border: 1px solid #eee; border-radius: 15px;
            padding: 25px; text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.05);
            transition: 0.3s;
            display: flex; flex-direction: column; justify-content: space-between;
            min-height: 200px;
        }
        .theme-card:hover { transform: translateY(-5px); border-color: var(--mnu-green); }
        
        .theme-icon { font-size: 40px; margin-bottom: 15px; display: block; }
        .theme-name { font-size: 18px; font-weight: bold; color: #333; margin-bottom: 5px; }
        .theme-desc { font-size: 13px; color: #888; margin-bottom: 20px; flex-grow: 1; }
        
        .btn-store { 
            width: 100%; padding: 12px; border-radius: 10px; border: none; 
            font-weight: bold; cursor: pointer; font-size: 14px; transition: 0.2s;
        }
        .btn-current { background: #eee; color: #999; cursor: default; }
        .btn-apply { background: var(--mnu-green); color: #fff; }
        .btn-apply:hover { background: #00857a; }
        .btn-buy { background: #fff; border: 2px solid var(--mnu-blue); color: var(--mnu-blue); }
        .btn-buy:hover { background: var(--mnu-blue); color: #fff; }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="store-container">
        <div class="store-header">
            <h2>🎨 테마 상점</h2>
            <div class="my-point-box">
                보유 포인트: <strong><%= String.format("%,d", currentPoint) %> P</strong>
            </div>
        </div>

        <div class="theme-grid">
            <% 
                if (allThemes != null) {
                    for(ThemeDTO theme : allThemes) { 
                       String code = theme.getThemeCode();
                       String name = theme.getThemeName();
                       int price = theme.getPrice();
                       String desc = theme.getDescription();
                       if(desc == null) desc = "";
                       
                       // 아이콘 결정 (단순 예시)
                       String icon = "🎨";
                       if(code.contains("orange") || code.contains("1")) icon = "🍊";
                       if(code.contains("black") || code.contains("2")) icon = "🌙";
            %>
                <div class="theme-card">
                    <div>
                        <span class="theme-icon"><%= icon %></span>
                        <h3 class="theme-name"><%= name %></h3>
                        <p class="theme-desc"><%= desc %></p>
                    </div>
                    
                    <% if (userTheme.equals(code)) { %>
                        <!-- 1. 현재 사용 중 -->
                        <button class="btn-store btn-current" disabled>사용 중</button>
                        
                    <% } else if (myThemes != null && myThemes.contains(code)) { %>
                        <!-- 2. 보유 중 (적용 가능) -->
                        <button class="btn-store btn-apply" 
                                onclick="applyTheme('<%= code %>')">
                            적용하기
                        </button>
                        
                    <% } else { %>
                        <!-- 3. 미보유 (구매 가능) -->
                        <button class="btn-store btn-buy" 
                                onclick="buyTheme('<%= code %>', '<%= name %>', <%= price %>)">
                            <%= price %>P 구매
                        </button>
                    <% } %>
                </div>
            <% 
                    } 
                } else {
            %>
                <p style="text-align:center; width:100%; color:#999;">등록된 테마가 없습니다.</p>
            <% } %>
        </div>
        
        <div style="text-align:center; margin-top:50px;">
            <a href="mypage.jsp" class="btn-action" style="background:#eee; color:#555;">마이페이지로 돌아가기</a>
        </div>
    </div>

    <script>
        function applyTheme(themeCode) {
            location.href = '${pageContext.request.contextPath}/themeApply.do?theme=' + themeCode;
        }

        function buyTheme(themeCode, themeName, price) {
            if (confirm(themeName + ' 테마를 ' + price + 'P에 구매하시겠습니까?')) {
                location.href = '${pageContext.request.contextPath}/themeBuy.do?theme=' + themeCode;
            }
        }
    </script>
</body>
</html>