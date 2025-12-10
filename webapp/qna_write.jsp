<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // Controller(Service)에서 이미 검사했지만, 
    // JSP에서 한 번 더 안전하게 정보를 가져옴
    UserDTO myUser = (UserDTO) session.getAttribute("sessionUser");
    
    // 만약 비정상적인 접근으로 여기까지 왔는데 세션이 풀려있으면 튕겨내기
    if(myUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    // 사용자 아이디 가져오기
    String userId = myUser.getJdi_user();

    // ==== ✅ 테마 기반 CSS 로딩 설정 ====
    String ctx = request.getContextPath();

    String currentTheme = "default";
    if (myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    String baseCss   = ctx + "/style/style.css";   // 공통 레이아웃
    String designCss = ctx + "/style/design.css";  // 관리자 / 테이블 공통
    String themeCss  = null;                       // 테마 (있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>질문 작성하기 - My J-Dic</title>

<!-- ✅ 공통 레이아웃 & 디자인 -->
<link rel="stylesheet" href="<%= baseCss %>">
<link rel="stylesheet" href="<%= designCss %>">
<% if (themeCss != null) { %>
    <link rel="stylesheet" href="<%= themeCss %>">
<% } %>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    function validateForm() {
        if ($("input[name='title']").val().trim() == "") {
            alert("제목을 입력해주세요.");
            return false;
        }
        if ($("textarea[name='content']").val().trim() == "") {
            alert("내용을 입력해주세요.");
            return false;
        }
        return true;
    }
</script>

<style>
    .qna-write-wrap {
        max-width: 800px;
        margin: 0 auto 60px;
    }

    .qna-write-table {
        width: 100%;
        border-collapse: collapse;
        background: #fff;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
    }

    .qna-write-table td {
        border-bottom: 1px solid #e5e7eb;
        padding: 10px 12px;
        font-size: 14px;
    }

    .qna-write-table tr:last-child td {
        border-bottom: none;
    }

    .qna-write-label {
        background-color: #f3f4f6;
        width: 150px;
        text-align: center;
        font-weight: 600;
        color: #374151;
    }

    .qna-write-input {
        width: 100%;
        padding: 8px 10px;
        border-radius: 6px;
        border: 1px solid #d1d5db;
        font-size: 14px;
    }

    .qna-write-input[readonly] {
        background-color: #f9fafb;
        color: #6b7280;
    }

    .qna-write-textarea {
        width: 100%;
        padding: 8px 10px;
        border-radius: 6px;
        border: 1px solid #d1d5db;
        font-size: 14px;
        resize: vertical;
    }

    .qna-write-btn-area {
        margin-top: 20px;
        text-align: center;
        display: flex;
        justify-content: center;
        gap: 10px;
    }

    .qna-btn-primary,
    .qna-btn-secondary {
        padding: 9px 20px;
        border-radius: 999px;
        border: none;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: 0.16s ease;
    }

    .qna-btn-primary {
        background: var(--mnu-blue);
        color: #fff;
    }
    .qna-btn-primary:hover {
        filter: brightness(0.95);
    }

    .qna-btn-secondary {
        background: #e5e7eb;
        color: #111827;
    }
    .qna-btn-secondary:hover {
        background: #d1d5db;
    }
</style>
</head>
<body>

    <!-- 공통 헤더 -->
    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">
        <div class="table-section qna-write-wrap">
            <div class="section-title">
                <span>📝 Q&amp;A 질문 작성</span>
                <a href="<%= ctx %>/QnAController?cmd=qna_list" class="btn-home">목록으로 돌아가기</a>
            </div>

            <form action="<%= ctx %>/QnAController?cmd=qna_write_pro" method="post" onsubmit="return validateForm()">
                
                <table class="req-table qna-write-table">
                    <tr>
                        <td class="qna-write-label">작성자</td>
                        <td>
                            <input type="text" name="writer_id" value="<%= userId %>" 
                                   class="qna-write-input" readonly>
                        </td>
                    </tr>

                    <tr>
                        <td class="qna-write-label">제목</td>
                        <td>
                            <input type="text" name="title" class="qna-write-input">
                        </td>
                    </tr>

                    <tr>
                        <td class="qna-write-label">내용</td>
                        <td>
                            <textarea name="content" rows="15" class="qna-write-textarea"></textarea>
                        </td>
                    </tr>
                </table>

                <div class="qna-write-btn-area">
                    <button type="submit" class="qna-btn-primary">등록하기</button>
                    <button type="button" onclick="history.back()" class="qna-btn-secondary">취소</button>
                </div>
            </form>
        </div>
    </div>

</body>
</html>
