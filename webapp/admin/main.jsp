<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // 관리자 권한 체크
    UserDTO adminUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath(); // 절대 경로 변수
    
    if(adminUser == null || !"ADMIN".equals(adminUser.getJdi_role())) {
        response.sendRedirect(ctx + "/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 대시보드 - My J-Dic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
</head>
<body>

    <div class="admin-container">
        
        <div class="admin-header">
            <div class="admin-title">ADMINISTRATOR</div>
            <div class="admin-info">
                <span>👤 <%= adminUser.getJdi_name() %>님</span>
                <a href="<%= ctx %>/logout.do" class="btn-logout">로그아웃</a>
            </div>
        </div>

        <div class="dashboard-grid">
            <div class="stat-card">
                <h3>전체 회원 수</h3>
                <strong>${totalUser}</strong> 
            </div>
            <div class="stat-card">
                <h3>신규 신청 대기</h3>
                <strong style="color:#e53935;">${totalRequest}</strong>
            </div>
            <div class="stat-card">
                <h3>총 등록 단어</h3>
                <strong>${totalWord}</strong>
            </div>
        </div>
        
        <div class="menu-section">
            <h2 class="section-title">관리 메뉴</h2>
            
            <ul class="menu-list">
                <li>
                    <a href="<%= ctx %>/NoticeController?cmd=notice_write" class="menu-item">
                        <span>📢 공지사항 <strong>작성</strong></span>
                        <span class="arrow">›</span>
                    </a>
                </li>
                <li>
                    <a href="<%= ctx %>/NoticeController?cmd=notice_list" class="menu-item">
                        <span>📢 공지사항 <strong>관리</strong></span>
                        <span class="arrow">›</span>
                    </a>
                </li>

                <li>
                    <a href="<%= ctx %>/adminList.apply" class="menu-item">
                        <span>📖 단어 <strong>신규 등록</strong> 신청 관리</span>
                        <span class="arrow">›</span>
                    </a>
                </li>
                <li>
                    <a href="<%= ctx %>/adminEditList.apply" class="menu-item">
                        <span>🛠️ 단어 <strong>정보 수정</strong> 신청 관리</span>
                        <span class="arrow">›</span>
                    </a>
                </li>

                <li>
                    <a href="<%= ctx %>/userList.do" class="menu-item">
                        <span>👥 회원 목록 조회 및 관리</span>
                        <span class="arrow">›</span>
                    </a>
                </li>
                <li>
                    <a href="<%= ctx %>/profileReqList.do" class="menu-item">
                        <span>🖼️ 프로필 <strong>이미지 변경</strong> 신청 관리</span>
                        <span class="arrow">›</span>
                    </a>
                </li>
                
                <li>		
			        <a href="<%= ctx %>/QnAController?cmd=admin_qna_list" class="menu-item">
			            <span>❓ <strong>Q&A 문의</strong> 관리 (답변 작성)</span>
			            <span class="arrow">›</span>
			        </a>
			    </li>
            </ul>
        </div>
        
        <div style="text-align: center; margin-top: 40px;">
            <a href="<%= ctx %>/index.jsp" class="btn-home">← 사용자 메인 페이지로 돌아가기</a>
        </div>

    </div>

</body>
</html>
