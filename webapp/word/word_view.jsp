<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.word.WordDTO" %>
<%
    // Controller에서 "vDto"라는 이름으로 데이터를 보내줍니다.
    WordDTO word = (WordDTO)request.getAttribute("vDto");
    
    // 데이터가 없으면 뒤로가기
    if(word == null) {
%>
    <script>alert('존재하지 않는 단어입니다.'); history.back();</script>
<%
        return;
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title><%= word.getWord() %> - 상세 정보</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <style>
        .view-container { 
            max-width: 600px; margin: 80px auto; padding: 50px; 
            background: #fff; border-radius: 20px; 
            box-shadow: 0 10px 25px rgba(0,0,0,0.1); text-align: center;
            
            /* ★ [핵심 수정] 이 속성이 있어야 별 버튼이 이 박스 안 우측 상단에 붙습니다. */
            position: relative; 
        }
        .view-badge { display: inline-block; background: #e0f2f1; color: #00A295; padding: 5px 15px; border-radius: 20px; font-weight: bold; margin-bottom: 20px; }
        .view-word { font-size: 48px; color: #333; font-weight: bold; margin-bottom: 10px; }
        .view-doc { font-size: 20px; color: #888; margin-bottom: 40px; }
        .view-korean { font-size: 32px; font-weight: bold; color: #0C4DA1; border-top: 2px dashed #eee; padding-top: 40px; margin-bottom: 40px; }
        .btn-edit-req { display: inline-block; padding: 12px 25px; background: #f5f5f5; color: #666; border-radius: 30px; text-decoration: none; font-size: 14px; transition: 0.3s; cursor: pointer; }
        .btn-edit-req:hover { background: #e0e0e0; }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="view-container">
        <div class="view-badge"><%= word.getJlpt() %></div>
        
        <div style="position: absolute; top: 30px; right: 30px;">
            <a href="${pageContext.request.contextPath}/WordController?cmd=bookmark_toggle&word_id=<%= word.getWord_id() %>" 
               style="text-decoration: none; font-size: 30px;">
               <% 
                  Boolean isBm = (Boolean)request.getAttribute("isBookmarked");
                  // 로그인 상태이고 찜했다면 꽉 찬 별
                  if(isBm != null && isBm) { 
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
            <a href="${pageContext.request.contextPath}/request/requesr_edit.jsp?word_id=<%= word.getWord_id() %>"
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