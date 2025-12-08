<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="com.mjdi.user.PointDAO" %>
<%@ page import="com.mjdi.user.MessageDAO" %>

<%
    UserDTO headerUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath(); // 절대 경로용 변수

    int headerPoint = 0;
    int unreadMsg = 0;
    String headerProfile = "profile1.png";   // 기본 프로필 파일명
    
    // 1. 로그인 상태일 때 정보 갱신
    if (headerUser != null) {
        headerPoint = PointDAO.getInstance().getTotalPoint(headerUser.getJdi_user());

        if (headerUser.getJdi_profile() != null && !headerUser.getJdi_profile().trim().isEmpty()) {
            headerProfile = headerUser.getJdi_profile();
        }
        
        // 안 읽은 메시지 수 확인
        unreadMsg = MessageDAO.getInstance().getUnreadCount(headerUser.getJdi_user());
    }

    // 2. 프로필 이미지 경로 결정 (핵심 로직)
    String profileSrc = "";
    if (headerProfile.startsWith("profile")) {
        // 기본 제공 프로필 (profile1.png 등) -> /images/ 폴더
        profileSrc = ctx + "/images/" + headerProfile;
    } else {
        // 사용자 업로드 프로필 등 (경로가 포함된 경우 그대로, 아니면 /images/ 등 정책에 따름)
        // 여기서는 업로드된 파일도 images 폴더에 있다고 가정하거나, 별도 로직 적용
        // 만약 'upload/...' 처럼 경로가 DB에 저장되어 있다면 ctx + "/" + headerProfile
        // 파일명만 있다면 ctx + "/images/" + headerProfile (일단 기본값과 동일하게 처리)
        profileSrc = ctx + "/images/" + headerProfile; 
    }
%>

<header class="top-header">
    <div class="inner">
        <div class="logo">
            <a href="<%= ctx %>/WordController?cmd=main">My J-Dic</a>
        </div>
        <nav class="util-nav">
            <% if(headerUser != null) { %>
                <div class="user-info-bar">
                    <!-- ✅ 수정된 부분: profileSrc 변수 사용 -->
                    <img src="<%= profileSrc %>" 
                         style="width:38px !important; height:38px !important; border-radius:50%; border:2px solid #eee; object-fit: cover;" 
                         alt="프사">
                    <span><%= headerUser.getJdi_name() %>님</span>
                    
                    <!-- 알림 종 아이콘 -->
                    <a href="<%= ctx %>/msgBox.do" class="alarm-bell <%= unreadMsg > 0 ? "active" : "" %>" title="알림">
                        🔔
                        <% if(unreadMsg > 0) { %>
                            <span class="dot"></span>
                        <% } %>
                    </a>
                </div>
            <% } else { %>
                <a href="<%= ctx %>/login.jsp" class="login-link">로그인</a>
            <% } %>

            <a href="javascript:void(0)" class="btn-menu" onclick="toggleMenu()">:::</a>
            
            <div id="userMenu" class="dropdown-content">
                <% if(headerUser != null) { %>
                    <div class="menu-profile-area">
                        <span class="menu-name"><%= headerUser.getJdi_name() %>님</span>
                        <span class="menu-point">💰 <%= String.format("%,d", headerPoint) %> P</span>
                    </div>
                    <div class="menu-divider"></div>
                    <a href="<%= ctx %>/mypage.jsp" class="menu-item">마이페이지</a>
                    <a href="<%= ctx %>/msgBox.do" class="menu-item">
                        📩 메세지함 
                        <% if(unreadMsg > 0) { %> <span style="color:red; font-size:12px;">(N)</span> <% } %>
                    </a>
                    <% if("ADMIN".equals(headerUser.getJdi_role())) { %>
                        <a href="<%= ctx %>/adminMain.apply" class="menu-item" style="color:#0C4DA1; font-weight: bold;">관리자 페이지</a>
                    <% } %>
                    <a href="<%= ctx %>/logout.do" class="menu-item logout">로그아웃</a>
                <% } else { %>
                    <a href="<%= ctx %>/login.jsp" class="menu-item">로그인</a>
                    <a href="<%= ctx %>/join.jsp" class="menu-item">회원가입</a>
                <% } %>
            </div>
        </nav>
    </div>
</header>

<script>
    /* 메뉴 토글 기능 */
    function toggleMenu() {
        var menu = document.getElementById("userMenu");
        menu.classList.toggle("show");
    }

    /* 메뉴 바깥쪽 클릭 시 닫기 */
    window.onclick = function(event) {
        if (!event.target.matches('.btn-menu')) {
            var dropdowns = document.getElementsByClassName("dropdown-content");
            for (var i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                }
            }
        }
    }
</script>

<style>
/* 알림 종 스타일 (헤더 전용) */
.alarm-bell {
    text-decoration: none;
    font-size: 20px;
    color: #ccc;
    margin-left: 10px;
    position: relative;
    transition: 0.3s;
}
.alarm-bell.active {
    color: #FFD700;
    animation: swing 1s ease infinite;
}
.alarm-bell .dot {
    position: absolute; top: 0; right: -2px;
    width: 6px; height: 6px;
    background: red; border-radius: 50%;
}
@keyframes swing { 
    0%, 100% { transform: rotate(0deg); } 
    25% { transform: rotate(15deg); } 
    75% { transform: rotate(-15deg); } 
}
</style>