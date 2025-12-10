<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="com.mjdi.user.PointDAO" %>
<%@ page import="com.mjdi.user.MessageDAO" %>
<%@ page import="com.mjdi.notice.NoticeDAO" %> 
<%@ page import="com.mjdi.notice.NoticeDTO" %>
<%@ page import="java.util.List" %>

<%
    UserDTO headerUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath(); 

    int headerPoint = 0;
    int unreadMsg = 0;
    // 기본값 설정
    String headerProfile = "profile1.png";   
    
    // 1. 로그인 정보 갱신
    if (headerUser != null) {
        headerPoint = PointDAO.getInstance().getTotalPoint(headerUser.getJdi_user());
        
        // DB에 저장된 프로필 값이 있다면 가져옴
        if (headerUser.getJdi_profile() != null && !headerUser.getJdi_profile().trim().isEmpty()) {
            headerProfile = headerUser.getJdi_profile();
        }
        unreadMsg = MessageDAO.getInstance().getUnreadCount(headerUser.getJdi_user());
    }

    // 2. [수정] 프로필 이미지 경로 결정 로직 (mypage.jsp와 동일하게 통일)
    String profileSrc = ""; 
    
    if (headerProfile.startsWith("upload")) {
        // 커스텀 이미지: DB에 이미 'upload/profile/...' 경로가 포함되어 있음
        profileSrc = ctx + "/" + headerProfile;
    } else {
        // 기본 이미지: 'profile1.png' 등 -> /images/ 폴더에서 찾음
        profileSrc = ctx + "/images/" + headerProfile;
    }

    // 3. 최신 공지사항 가져오기
    String noticeMsg = "현재 등록된 공지사항이 없습니다. 새로운 소식을 기다려주세요!"; 
    String noticeLink = ctx + "/NoticeController?cmd=notice_list"; 

    try {
        NoticeDAO noticeDao = NoticeDAO.getInstance();
        List<NoticeDTO> nList = noticeDao.noticeList(); 
        if(nList != null && !nList.isEmpty()) {
            NoticeDTO topNotice = nList.get(0); 
            noticeMsg = "[NEW] " + topNotice.getTitle(); 
            noticeLink = ctx + "/NoticeController?cmd=notice_view&idx=" + topNotice.getIdx();
        }
    } catch(Exception e) {
        e.printStackTrace();
    }
%>

<header class="top-header">
    
    <div class="inner full-width-inner">
        <div class="logo">
            <a href="<%= ctx %>/WordController?cmd=main">My J-Dic</a>
        </div>
        <nav class="util-nav">
            <% if(headerUser != null) { %>
                <div class="user-info-bar">
                    <img src="<%= profileSrc %>" 
                         style="width:38px !important; height:38px !important; border-radius:50%; border:2px solid #eee; object-fit: cover;" 
                         alt="프사">
                    <span><%= headerUser.getJdi_name() %>님</span>
                    
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

    <div class="notice-bar">
        <div class="notice-icon">📢</div>
        <div class="notice-content">
            <div class="notice-scroll">
                <a href="<%= noticeLink %>"><%= noticeMsg %></a>
            </div>
        </div>
    </div>

</header>

<script>
    function toggleMenu() {
        var menu = document.getElementById("userMenu");
        menu.classList.toggle("show");
    }
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
/* ✅ 헤더 전체 구조: 세로 배치 */
.top-header {
    display: flex !important;
    flex-direction: column !important;
    width: 100% !important; /* 전체 너비 강제 */
    margin: 0 !important;
    padding: 0 !important;
}

/* ✅ 상단 내용 영역 (로고, 유저정보) 꽉 채우기 */
.full-width-inner {
    width: 100% !important;
    max-width: 100% !important; /* 너비 제한 해제 */
    display: flex !important;
    justify-content: space-between !important;
    align-items: center !important;
    padding: 10px 20px !important; /* 상하 10px, 좌우 20px 여백 */
    box-sizing: border-box !important; /* 패딩 포함 크기 계산 */
    min-height: 60px; /* 적당한 높이 확보 */
}

/* 알림 종 스타일 */
.alarm-bell {
    text-decoration: none; font-size: 20px; color: #ccc; margin-left: 10px;
    position: relative; transition: 0.3s;
}
.alarm-bell.active { color: #FFD700; animation: swing 1s ease infinite; }
.alarm-bell .dot {
    position: absolute; top: 0; right: -2px; width: 6px; height: 6px;
    background: red; border-radius: 50%;
}
@keyframes swing { 
    0%, 100% { transform: rotate(0deg); } 
    25% { transform: rotate(15deg); } 
    75% { transform: rotate(-15deg); } 
}

/* 공지사항 바 스타일 */
.notice-bar {
    width: 100%;
    height: 35px;
    background-color: #f8f9fa;
    border-top: 1px solid #e9ecef;
    display: flex;
    align-items: center;
    position: relative;
    z-index: 1;
}

.notice-icon {
    width: 50px;
    height: 100%;
    background-color: #0C4DA1;
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 16px;
    z-index: 10;
    flex-shrink: 0;
}

.notice-content {
    flex-grow: 1;
    height: 100%;
    position: relative;
    overflow: hidden;
    display: flex;
    align-items: center;
}

.notice-scroll {
    position: absolute;
    white-space: nowrap;
    will-change: transform;
    animation: marquee 20s linear infinite;
}

.notice-scroll a {
    text-decoration: none; color: #333; font-size: 14px; font-weight: 500;
}
.notice-scroll a:hover { text-decoration: underline; color: #0C4DA1; }

@keyframes marquee {
    0% { left: 100%; transform: translateX(0%); }
    100% { left: 0%; transform: translateX(-100%); }
}
</style>