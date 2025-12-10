<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="com.mjdi.user.UserDTO" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // 세션에서 유저 정보 가져오기
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");

    // 현재 테마 결정
    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 공통 스타일 + 테마 스타일 경로
    String baseCss  = request.getContextPath() + "/style/style.css";        // 공통 레이아웃
    String themeCss = null;                                                 // 테마 (있을 때만)
    if (!"default".equals(currentTheme)) {
        themeCss = request.getContextPath() + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>알림 메세지함</title>

    <!-- 공통 스타일 -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <!-- 테마 스타일 (default가 아닐 때만) -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        .msg-container { max-width: 800px; margin: 50px auto; }
        .msg-list { list-style: none; padding: 0; }
        .msg-item {
            background: #fff; padding: 20px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px; border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        /* 테마에 노란 포인트 컬러가 있으면 var(--mnu-yellow)로, 없으면 Fallback(#FFD700) */
        .msg-item.unread {
            border-left: 5px solid var(--mnu-yellow, #FFD700);
            background: #fffdf0;
        }
        .msg-sender {
            font-weight: bold;
            color: var(--mnu-blue);   /* 기본 파랑 계열 테마 변수 사용 */
            margin-bottom: 5px;
            display: block;
        }
        .msg-date { float: right; color: #999; font-size: 12px; }
        .msg-content { font-size: 15px; color: #333; line-height: 1.5; }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="inner msg-container">
        <h2 style="margin-bottom: 30px; color:#555;">📩 알림 메세지함</h2>
        
        <ul class="msg-list">
            <c:choose>
                <c:when test="${not empty msgList}">
                    <c:forEach var="m" items="${msgList}">
                        <!-- 안 읽은 메세지면 unread 클래스 추가 -->
                        <li class="msg-item ${m.read ? '' : 'unread'}">
                            <span class="msg-date">${m.sendDate}</span>
                            <span class="msg-sender">📢 관리자 알림</span>
                            <p class="msg-content">${m.content}</p>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <li style="text-align:center; padding:50px; color:#999;">
                        받은 메세지가 없습니다.
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
    
    <%
        // 페이지 진입 시 해당 유저의 모든 메세지를 '읽음'으로 처리
        if (myUser != null) {
            java.sql.Connection conn = com.mjdi.util.DBM.getConnection();
            java.sql.PreparedStatement pstmt =
                conn.prepareStatement("UPDATE jdi_message SET is_read='Y' WHERE receiver=?");
            pstmt.setString(1, myUser.getJdi_user());
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        }
    %>
</body>
</html>
