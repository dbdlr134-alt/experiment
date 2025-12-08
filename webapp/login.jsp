<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>로그인 - My J-Dic</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="auth-wrap">
        <div class="auth-box">
            <h2 class="auth-title">LOGIN</h2>

            <% if("1".equals(request.getParameter("error"))) { %>
                <p style="color:#e53935; font-size:13px; margin-bottom:15px;">
                    ⚠ 아이디 또는 비밀번호가 일치하지 않습니다.
                </p>
            <% } %>

            <%-- ★ [수정] action 경로를 절대 경로로 변경 --%>
            <form action="${pageContext.request.contextPath}/login.do" method="post">
                <div class="input-group">
                    <input type="text" name="id" class="input-field" placeholder="아이디" required>
                </div>
              
                <div class="input-group">
                    <input type="password" name="pw" class="input-field" placeholder="비밀번호" required>
                </div>
                <button type="submit" class="btn-submit">로그인</button>
            </form>
            
            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/find_user.jsp">아이디/비밀번호 찾기</a>
                <span style="color:#ddd;">|</span>
                <a href="${pageContext.request.contextPath}/join.jsp" style="color:var(--mnu-blue); font-weight:bold;">회원가입</a>
            </div>
        </div>
    </div>

</body>
</html>