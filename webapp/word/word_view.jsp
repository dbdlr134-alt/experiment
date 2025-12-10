<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.mjdi.word.WordDTO, com.mjdi.user.UserDTO" %>

<%
    // 단어 상세 데이터
    WordDTO word = (WordDTO)request.getAttribute("vDto");
    if (word == null) {
%>
    <script>alert('존재하지 않는 단어입니다.'); history.back();</script>
<%
        return;
    }

    // === 테마 정보 세팅 ===
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    String baseCss  = ctx + "/style/style.css";             // 공통 레이아웃/기본 스타일
    String themeCss = null;                                 // 테마 (있을 때만)
    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><%= word.getWord() %> - 상세 정보</title>

    <!-- 공통 스타일 -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <!-- 테마 스타일 (default가 아닐 때만) -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        .view-container { 
            max-width: 600px;
            margin: 80px auto;
            padding: 50px; 
            background: #fff;
            border-radius: 20px; 
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            text-align: center;
            position: relative; /* 별 버튼 기준 박스 */
        }
        .view-badge {
            display: inline-block;
            padding: 5px 15px;
            border-radius: 20px;
            font-weight: bold;
            margin-bottom: 20px;
            background: var(--chip-bg, #e0f2f1);
            color: var(--mnu-green, #00A295);
        }
        .view-word {
            font-size: 48px;
            color: #333;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .view-doc {
            font-size: 20px;
            color: #888;
            margin-bottom: 40px;
        }
        .view-korean {
            font-size: 32px;
            font-weight: bold;
            color: var(--mnu-blue, #0C4DA1);
            border-top: 2px dashed #eee;
            padding-top: 40px;
            margin-bottom: 40px;
        }
        .btn-edit-req {
            display: inline-block;
            padding: 12px 25px;
            background: #f5f5f5;
            color: #666;
            border-radius: 30px;
            text-decoration: none;
            font-size: 14px;
            transition: 0.3s;
            cursor: pointer;
        }
        .btn-edit-req:hover {
            background: #e0e0e0;
        }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="view-container">
        <div class="view-badge"><%= word.getJlpt() %></div>
        
        <!-- 즐겨찾기 별 버튼 -->
        <div style="position: absolute; top: 30px; right: 30px;">
            <a href="<%= ctx %>/WordController?cmd=bookmark_toggle&word_id=<%= word.getWord_id() %>" 
               style="text-decoration: none; font-size: 30px;">
               <%
                  Boolean isBm = (Boolean)request.getAttribute("isBookmarked");
                  if (isBm != null && isBm) {
               %>
                   <span style="color: gold;">★</span>
               <% } else { %>
                   <span style="color: #ccc;">☆</span>
               <% } %>
            </a>
        </div>
        
        <div class="view-word"><%= word.getWord() %></div>
        <div class="view-doc"><%= word.getDoc() %></div>
        <div class="view-korean"><%= word.getKorean() %></div>
        
        <div>
            <a href="<%= ctx %>/request/requesr_edit.jsp?word_id=<%= word.getWord_id() %>"
               class="btn-edit-req">
                🛠️ 정보 수정 요청하기
            </a>
        </div>
        
        <div style="margin-top: 20px;">
            <a href="javascript:history.back()" style="color:#999; text-decoration:underline;">뒤로가기</a>
        </div>
    </div>
</body>
</html>
