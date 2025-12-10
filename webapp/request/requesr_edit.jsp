<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.word.WordDAO, com.mjdi.word.WordDTO, com.mjdi.user.UserDTO" %>

<%
    // 로그인 체크
    if(session.getAttribute("sessionUser") == null) {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<script>alert('로그인이 필요한 서비스입니다.'); location.href='../login.jsp';</script>");
        return;
    }

    // 파라미터로 넘어온 word_id 확인
    String wordIdStr = request.getParameter("word_id");
    if(wordIdStr == null || wordIdStr.isEmpty()) {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<script>alert('잘못된 접근입니다.'); history.back();</script>");
        return;
    }

    int wordId = Integer.parseInt(wordIdStr);
    WordDAO dao = WordDAO.getInstance();
    WordDTO word = dao.wordSelect(wordId);
    
    if(word == null) {
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().write("<script>alert('존재하지 않는 단어입니다.'); history.back();</script>");
        return;
    }

    // ==== ✅ 테마 기반 CSS 로딩 설정 ====
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    String baseCss   = ctx + "/style/style.css";     // 공통 레이아웃
    String userCss   = ctx + "/style/user.css";      // 회원/폼 공통
    String reqCss    = ctx + "/style/request.css";   // 신청/요청 폼 전용
    String themeCss  = null;                         // 테마 (있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>단어 수정 신청 - My J-Dic</title>

    <!-- ✅ 공통 레이아웃 & 폼 스타일 -->
    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= userCss %>">
    <link rel="stylesheet" href="<%= reqCss %>">

    <!-- ✅ 테마 CSS (default가 아닌 경우만) -->
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>
</head>
<body>

    <!-- ✅ 공통 상단 헤더 -->
    <jsp:include page="/include/header.jsp" />

    <div class="auth-wrap">
        <div class="auth-box">
            <h2 class="auth-title">단어 수정 신청</h2>
            <p style="text-align:center; color:#666; margin-bottom:20px; line-height:1.6;">
                사전에 등록된 단어 정보가 잘못되었나요?<br>
                아래에 올바른 정보를 입력해 주세요.<br>
                관리자가 검토 후 반영합니다.
            </p>

            <!-- ✅ 단어 수정 신청 폼 -->
            <form action="<%= ctx %>/requestEdit.apply" method="post" class="auth-form">
                
                <!-- 수정 대상 단어 ID (수정 불가, hidden) -->
                <input type="hidden" name="word_id" value="<%= word.getWord_id() %>">

                <div class="form-group">
                    <label class="form-label">단어 (한자/히라가나)</label>
                    <input type="text" name="word" class="form-input" 
                           value="<%= word.getWord() %>" required>
                </div>

                <div class="form-group">
                    <label class="form-label">요미가나 (읽는 법)</label>
                    <input type="text" name="doc" class="form-input" 
                           value="<%= word.getDoc() %>" required>
                </div>

                <div class="form-group">
                    <label class="form-label">뜻 (한국어)</label>
                    <input type="text" name="korean" class="form-input" 
                           value="<%= word.getKorean() %>" required>
                </div>

                <div class="form-group">
                    <label class="form-label">JLPT 급수</label>
                    <select name="jlpt" class="form-select" required>
                        <option value="N5" <%= "N5".equals(word.getJlpt()) ? "selected" : "" %>>N5 (기초)</option>
                        <option value="N4" <%= "N4".equals(word.getJlpt()) ? "selected" : "" %>>N4</option>
                        <option value="N3" <%= "N3".equals(word.getJlpt()) ? "selected" : "" %>>N3</option>
                        <option value="N2" <%= "N2".equals(word.getJlpt()) ? "selected" : "" %>>N2</option>
                        <option value="N1" <%= "N1".equals(word.getJlpt()) ? "selected" : "" %>>N1 (고급)</option>
                        <option value="ETC" <%= !"N5".equals(word.getJlpt()) && !"N4".equals(word.getJlpt()) && !"N3".equals(word.getJlpt()) && !"N2".equals(word.getJlpt()) && !"N1".equals(word.getJlpt()) ? "selected" : "" %>>기타</option>
                    </select>
                </div>

                <button type="submit" class="btn-submit" style="margin-top: 20px;">
                    수정 신청 제출
                </button>
            </form>
        </div>
    </div>

</body>
</html>
