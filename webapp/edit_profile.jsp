<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%@ page import="java.io.File, java.util.List, java.util.ArrayList" %>
<%
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    if(session.getAttribute("isPwdChecked") == null) {
        response.sendRedirect("pwd_check.jsp");
        return;
    }

    // ✅ /images 폴더에서 profile*.png 파일 자동으로 모으기
    String imgDir = application.getRealPath("/images");
    File folder = new File(imgDir);
    File[] files = folder.listFiles();
    List<String> profileList = new ArrayList<>();

    if (files != null) {
        for (File f : files) {
            String name = f.getName();
            // 파일명이 profile로 시작하고 .png로 끝나면 후보로 사용
            if (name.startsWith("profile") && name.endsWith(".png")) {
                profileList.add(name);
            }
        }
    }

    String ctx = request.getContextPath();

    // ✅ 현재 프로필이 기본 이미지 목록에 포함되어 있는지 확인
    String currentProfile = (myUser != null) ? myUser.getJdi_profile() : null;
    boolean showCustomProfile = false;

    if (currentProfile != null && !currentProfile.trim().isEmpty()) {
        boolean inDefaultList = false;
        for (String p : profileList) {
            if (p.equals(currentProfile)) {
                inDefaultList = true;
                break;
            }
        }
        // 기본 profile*.png 에 없는 값이면 "커스텀 프로필"로 취급
        if (!inDefaultList && !currentProfile.startsWith("profile")) {
            showCustomProfile = true;
        }
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원정보 수정</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    
    <style>
        /* === 1. 전체 레이아웃 스타일 === */
        .edit-container { 
            max-width: 600px; 
            margin: 50px auto;  
            background: #fff; padding: 40px; 
            border-radius: 20px; box-shadow: 0 5px 20px rgba(0,0,0,0.05);
        }
        .row { margin-bottom: 20px;  }
        .label { display: block; margin-bottom: 5px; color: #666; font-weight: 500;}
        .input-edit { 
            width: 100%; 
            padding: 12px;  
            border: 1px solid #ddd; border-radius: 8px; font-size: 15px;
        }
        .input-edit:read-only { background: #f5f5f5; color: #999;}
        
		.section-line { margin: 30px 0; border-top: 2px dashed #eee; }
        
        .btn-update { 
            width: 100%;
            padding: 15px; 
            background: #0C4DA1; 
            color: #fff; 
            border: none; border-radius: 30px; 
            font-size: 16px; font-weight: bold; cursor: pointer;
        }
        .btn-update:hover { 
            background-color: #093b7c;
        }

        /* === 2. 프로필 선택용 스타일 === */
        .profile-selector {
            display: flex; 
            justify-content: center; 
            gap: 15px; 
            margin-bottom: 30px;
            flex-wrap: wrap;
        }
        .profile-option {
            cursor: pointer; 
            position: relative;
        }
        .profile-option img {
            width: 70px; 
            height: 70px; border-radius: 50%;
            border: 3px solid #eee; transition: 0.2s; 
        }
        .profile-option input:checked + img {
            border-color: var(--main-color); 
            transform: scale(1.1);
            box-shadow: 0 0 10px rgba(158,173,255,0.5);
        }
        .profile-option input { display: none;  }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="edit-container">
        <h2 style="text-align:center; margin-bottom:30px; color:var(--main-color);">회원정보 수정</h2>
        
        <form action="updateAll.do" method="post">
    
            <h3 style="text-align:center; font-size:16px; margin-bottom:15px; color:#555;">프로필 사진 선택</h3>
            
            <p style="font-size:12px; color:#e53935; text-align:center; margin-bottom:15px; font-weight:bold;">
                ※ 새 프로필 등록 시 50 P가 차감됩니다.
            </p>
            
            <div class="profile-selector">
                <%-- ✅ 기본 profile*.png 이미지들 출력 --%>
                <%
                    for (String p : profileList) {
                %>
                    <label class="profile-option">
                        <input type="radio" name="profile" value="<%= p %>"
                            <%= p.equals(currentProfile) ? "checked" : "" %> >
                        <img src="<%= ctx %>/images/<%= p %>" alt="<%= p %>">
                    </label>
                <%
                    }
                %>

                <%-- ✅ 커스텀(신청 승인) 프로필이 있을 경우 별도로 표시 --%>
                <%
                    if (showCustomProfile) {
                %>
                    <label class="profile-option">
                        <input type="radio" name="profile" value="<%= currentProfile %>" checked>
                        <!-- currentProfile 이 'upload/profile/xxx.png' 형태라고 가정 -->
                        <img src="<%= ctx %>/<%= currentProfile %>" alt="현재 프로필">
                    </label>
                <%
                    }
                %>

                <%-- ✅ 새 프로필 신청 버튼은 그대로 유지 --%>
                <label class="profile-option"
                       onclick="location.href='<%= ctx %>/request_profile.jsp'; return false;">
                    <img src="<%= ctx %>/images/newprofile.png" alt="새 프로필 신청">
                </label>
			</div>

            <div class="row">
                <span class="label">아이디 (수정 불가)</span>
                <input type="text" class="input-edit" value="<%= myUser.getJdi_user() %>" readonly>
            </div>
            
            <div class="row">
                <span class="label">이름</span>
                <input type="text" name="name" class="input-edit" value="<%= myUser.getJdi_name() %>">
            </div>

            <div class="row">
                <span class="label">전화번호</span>
                <input type="text" name="phone" class="input-edit" value="<%= myUser.getJdi_phone() %>">
            </div>
            
            <div class="row">
                <span class="label">이메일</span>
                <input type="text" name="email" class="input-edit" value="<%= myUser.getJdi_email() %>">
            </div>

            <div class="section-line"></div>
            
            <h3 style="margin-bottom:15px; font-size:16px;">비밀번호 변경 (선택사항)</h3>
            <p style="font-size:12px; color:#888; margin-bottom:10px;">변경을 원하지 않으면 비워두세요.</p>

            <div class="row">
                <input type="password" name="newPw" class="input-edit" placeholder="새로운 비밀번호">
            </div>
            <div class="row">
                <input type="password" name="newPwCheck" class="input-edit" placeholder="새로운 비밀번호 확인">
            </div>

            <button type="submit" class="btn-update">수정 완료</button>
        </form>
    </div>
</body>
</html>