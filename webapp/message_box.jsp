<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>알림 메세지함</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <style>
        .msg-container { max-width: 800px; margin: 50px auto; }
        .msg-list { list-style: none; padding: 0; }
        .msg-item {
            background: #fff; padding: 20px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px; border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .msg-item.unread { border-left: 5px solid #FFD700; background: #fffdf0; } /* 안 읽은건 노란색 강조 */
        .msg-sender { font-weight: bold; color: #0C4DA1; margin-bottom: 5px; display: block; }
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
                            
                            <%-- 읽음 처리 로직은 상세보기가 없으므로, 이 페이지 접속 시 
                                 DAO에서 일괄 읽음 처리하거나, 클릭 시 처리하는 추가 구현이 필요합니다.
                                 (현재는 보기만 하는 기능) --%>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <li style="text-align:center; padding:50px; color:#999;">받은 메세지가 없습니다.</li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
    
    <%
        // 페이지 들어오면 해당 유저의 모든 메세지를 '읽음'으로 처리하는 간단 로직 (JSP 내장)
        // 실제로는 Service에서 하는게 좋지만 편의상 추가
        com.mjdi.user.UserDTO u = (com.mjdi.user.UserDTO)session.getAttribute("sessionUser");
        if(u != null) {
            // 일괄 읽음 처리 쿼리 실행
            java.sql.Connection conn = com.mjdi.util.DBM.getConnection();
            java.sql.PreparedStatement pstmt = conn.prepareStatement("UPDATE jdi_message SET is_read='Y' WHERE receiver=?");
            pstmt.setString(1, u.getJdi_user());
            pstmt.executeUpdate();
            conn.close();
        }
    %>
</body>
</html>