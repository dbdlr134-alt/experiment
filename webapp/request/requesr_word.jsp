<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // ==== ✅ 테마 기반 CSS 로딩 설정 ====
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    String baseCss   = ctx + "/style/style.css";   // 공통 레이아웃
    String userCss   = ctx + "/style/user.css";    // 회원/폼 공통
    String themeCss  = null;                       // 테마 (있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>단어 등록 신청 - My J-Dic</title>

    <!-- ✅ 공통 레이아웃 & 폼 스타일 -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= userCss %>">

    <!-- ✅ 테마 CSS (default가 아닐 때만) -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>
</head>
<body>

    <!-- ✅ 공통 상단 헤더 -->
    <jsp:include page="/include/header.jsp" />

    <div class="auth-wrap">
        <div class="auth-box">
            <h2 class="auth-title">단어 등록 신청</h2>
            <p style="text-align:center; color:#666; margin-bottom:20px; line-height:1.6;">
                없는 단어가 있나요?<br>
                관리자에게 등록을 요청해 주세요.<br>
                승인되면 포인트 <strong>50 P</strong>를 드립니다!
            </p>

            <!-- ✅ 단어 등록 신청 폼 -->
            <form action="<%= ctx %>/request.apply" method="post" class="auth-form">
                <div class="input-group">
                    <label class="input-label" for="word">단어 (한자/히라가나)</label>
                    <input type="text" id="word" name="word"
                           class="input-field"
                           placeholder="예: 勉強" required>
                </div>

                <div class="input-group">
                    <label class="input-label" for="doc">요미가나 (읽는 법)</label>
                    <input type="text" id="doc" name="doc"
                           class="input-field"
                           placeholder="예: べんきょう" required>
                </div>

                <div class="input-group">
                    <label class="input-label" for="korean">뜻 (한국어)</label>
                    <input type="text" id="korean" name="korean"
                           class="input-field"
                           placeholder="예: 공부" required>
                </div>

                <div class="input-group">
                    <label class="input-label" for="jlpt">JLPT 급수</label>
                    <select id="jlpt" name="jlpt" class="input-field" required>
                        <option value="">선택하세요</option>
                        <option value="N5">N5 (기초)</option>
                        <option value="N4">N4</option>
                        <option value="N3">N3</option>
                        <option value="N2">N2</option>
                        <option value="N1">N1</option>
                        <option value="기타">해당 없음 / 기타</option>
                    </select>
                </div>

                <button type="submit" class="btn-submit" style="margin-top: 20px;">
                    신청하기
                </button>
            </form>
        </div>
    </div>

</body>
</html>
