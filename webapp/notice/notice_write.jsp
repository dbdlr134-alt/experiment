<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mjdi.user.UserDTO" %>
<%
    UserDTO adminUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    // 관리자 아닌 경우 접근 차단
    if(adminUser == null || !"ADMIN".equals(adminUser.getJdi_role())) {
        response.sendRedirect(ctx + "/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항 작성 - 관리자</title>

    <!-- 공통 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">

    <style>
        /* 작성 페이지 전용 스타일 */
        .notice-write-box {
            max-width: 800px;
            margin: auto;
            background: #fff;
            border-radius: 10px;
            padding: 25px;
            border: 1px solid #ddd;
        }
        .notice-write-box input,
        .notice-write-box textarea {
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 15px;
        }
        .notice-write-box label {
            font-weight: bold;
            color: #0C4DA1;
        }

        /* 버튼 스타일 통일 */
        .btn-submit {
            padding: 10px 25px;
            background: #0C4DA1;
            color: white;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-size: 15px;
        }
        .btn-cancel {
            padding: 10px 25px;
            background: #ccc;
            color: #333;
            border-radius: 6px;
            text-decoration: none;
            margin-left: 8px;
            font-size: 15px;
        }
    </style>
</head>
<body>

    <!-- 상단 공통 헤더 -->
    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">

        <div class="table-section" style="max-width:800px; margin:auto;">
            <div class="section-title">
                <span>📢 공지사항 작성</span>
                 <a href="${pageContext.request.contextPath}/adminMain.apply" class="btn-home">
                    목록으로 돌아가기
                </a>
            </div>

            <div class="notice-write-box">
                <form action="<%= ctx %>/NoticeController" method="post">
                    <input type="hidden" name="cmd" value="notice_write_pro">

                    <!-- 제목 -->
                    <div style="margin-bottom:15px;">
                        <label for="notice_title">제목</label>
                        <input type="text" id="notice_title" name="title" required
                               style="width:100%; padding:12px; margin-top:6px;">
                    </div>

                    <!-- 내용 -->
                    <div style="margin-bottom:15px;">
                        <label for="notice_content">내용</label>
                        <textarea id="notice_content" name="content" required rows="12"
                                  style="width:100%; padding:12px; margin-top:6px;"></textarea>
                    </div>

                    <!-- 버튼 -->
                    <div style="margin-top:20px; text-align:right;">
                        <button type="submit" class="btn-submit">등록</button>
                         <a href="${pageContext.request.contextPath}/adminMain.apply" class="btn-home">취소</a>
                    </div>
                </form>
            </div>

        </div>
    </div>

</body>
</html>
