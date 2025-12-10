<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="com.mjdi.user.UserDAO" %>
<%@ page import="com.mjdi.user.PointDAO" %>
<%@ page import="com.mjdi.user.ThemeDTO" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 1. 로그인 체크
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    if(myUser == null) { 
        response.sendRedirect("login.jsp"); 
        return; 
    }
    
    String userId = myUser.getJdi_user();
    
    // 👉 관리자 여부 체크 (jdi_role이 ADMIN인지)
    boolean isAdmin = "ADMIN".equals(myUser.getJdi_role());

    // 2. 데이터 로드
    UserDAO uDao = UserDAO.getInstance();
    PointDAO pDao = PointDAO.getInstance();

    int currentPoint = pDao.getTotalPoint(userId);
    ArrayList<String> myThemes = uDao.getMyThemes(userId);
    ArrayList<ThemeDTO> allThemes = uDao.getAllThemes();
    
    // 3. 현재 적용된 테마 확인
    String userTheme = myUser.getJdi_theme();
    if(userTheme == null || userTheme.trim().isEmpty()) userTheme = "default";

    // 4. CSS 경로 설정
    String ctx      = request.getContextPath();
    String baseCss  = ctx + "/style/style.css";
    String userCss  = ctx + "/style/user.css";
    String themeCss = null;
    if (!"default".equals(userTheme)) {
        themeCss = ctx + "/style/" + userTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>테마 상점 - My J-Dic</title>
    
    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= userCss %>">
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>
    
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
            position: relative;
        }
        .theme-card:hover { transform: translateY(-5px); border-color: var(--mnu-green); }
        
        /* 시크릿 테마 스타일 */
        .secret-card { border: 2px solid #9C27B0; background: #fdf5ff; }
        .secret-badge {
            position: absolute; top: 10px; right: 10px;
            background: #9C27B0; color: #fff; font-size: 10px;
            padding: 3px 6px; border-radius: 4px; font-weight: bold;
        }

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
                       String isActive = theme.getIsActive(); 
                       if(isActive == null) isActive = "Y";

                       if(desc == null) desc = "";

                       boolean isOwned = (myThemes != null && myThemes.contains(code));

                       // 1. 판매 중지('N') 상태면 아예 안 보여줌
                       if("N".equals(isActive)) {
                           continue; 
                       }
                       if("A".equals(isActive) && !isAdmin) {
                           continue; 
                       }
                       // ========================================================
                       
                       // 아이콘 결정
                       String icon = "🎨";
                       if(code.contains("orange") || code.contains("1")) icon = "🍊";
                       if(code.contains("black") || code.contains("2")) icon = "🌙";
                       if(code.contains("pixie") || code.contains("3")) icon = "✨";
                       if(code.contains("kessoku") || code.contains("4")) icon = "🎸";
                       if("A".equals(isActive)) icon = "🔒"; // 시크릿은 자물쇠 등
            %>
                <div class="theme-card <%= "A".equals(isActive) ? "secret-card" : "" %>">
                    
                    <% if("A".equals(isActive)) { %>
                        <span class="secret-badge">SECRET</span>
                    <% } %>

                    <div>
                        <span class="theme-icon"><%= icon %></span>
                        <h3 class="theme-name"><%= name %></h3>
                        <p class="theme-desc"><%= desc %></p>
                    </div>
                    
                    <% if (userTheme.equals(code)) { %>
                        <button class="btn-store btn-current" disabled>사용 중</button>
                        
                    <% } else if (isOwned) { %>
                        <button class="btn-store btn-apply" 
                                onclick="applyTheme('<%= code %>')">
                            적용하기
                        </button>
                        
                    <% } else { %>
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
        <div style="margin-top: 20px; text-align: center;">
        
        <div style="text-align:center; margin-top:50px;">
            <a href="<%= ctx %>/mypage.jsp" class="btn-action" style="background:#eee; color:#555;">
                마이페이지로 돌아가기
            </a>
        </div>
    </div>

    <script>
        function applyTheme(themeCode) {
            location.href = '<%= ctx %>/themeApply.do?theme=' + themeCode;
        }

        function buyTheme(themeCode, themeName, price) {
            if (confirm(themeName + ' 테마를 ' + price + 'P에 구매하시겠습니까?')) {
                location.href = '<%= ctx %>/themeBuy.do?theme=' + themeCode;
            }
        }
        
    </script>
</body>
</html>