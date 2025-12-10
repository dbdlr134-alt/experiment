<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ page import="com.mjdi.user.UserDTO" %>

<%
    // 1. 세션에서 유저 정보 가져오기
    UserDTO myUser = (UserDTO)session.getAttribute("sessionUser");
    String ctx = request.getContextPath();

    // 2. 현재 테마 결정
    String currentTheme = "default";
    if (myUser != null && myUser.getJdi_theme() != null && !myUser.getJdi_theme().trim().isEmpty()) {
        currentTheme = myUser.getJdi_theme();
    }

    // 3. 공통/디자인/테마 CSS 경로
    String baseCss   = ctx + "/style/style.css";      // 공통 레이아웃
    String designCss = ctx + "/style/design.css";     // 관리자/테이블/공통 서브 레이아웃
    String themeCss  = null;                          // 테마(있을 때만)

    if (!"default".equals(currentTheme)) {
        themeCss = ctx + "/style/" + currentTheme + "/style.css";
    }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>${q.title} - Q&amp;A</title>

    <link rel="stylesheet" href="<%= baseCss %>">
    <link rel="stylesheet" href="<%= designCss %>">
    <% if (themeCss != null) { %>
        <link rel="stylesheet" href="<%= themeCss %>">
    <% } %>

    <style>
        .qna-detail-wrap {
            max-width: 800px;
            margin: 0 auto 60px;
        }

        .qna-detail-title {
            font-size: 22px;
            font-weight: 700;
            color: var(--mnu-blue);
            margin-bottom: 10px;
        }

        .qna-detail-meta {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            justify-content: space-between;
            font-size: 13px;
            color: #777;
            margin-bottom: 16px;
        }

        .qna-question-body {
            padding: 18px 20px;
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            background: #f9fafb;
            line-height: 1.7;
            font-size: 14px;
            margin-bottom: 24px;
            min-height: 150px;
        }

        .qna-answer-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 10px;
            color: #444;
            border-top: 1px solid #eee;
            padding-top: 30px;
        }

        .answer-box {
            background: #ffffff;
            padding: 15px 18px;
            margin-top: 10px;
            border-radius: 10px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 10px rgba(15, 23, 42, 0.05);
        }

        .answer-header {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 13px;
            color: #555;
        }

        .answer-meta-date {
            color: #888;
            font-size: 12px;
        }

        .admin-badge {
            background: var(--mnu-green);
            color: #fff;
            padding: 2px 6px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 600;
        }

        .answer-content {
            margin-top: 8px;
            font-size: 14px;
            line-height: 1.7;
        }

        .qna-empty-answer {
            color: #999;
            font-size: 14px;
            margin-top: 8px;
            padding: 20px;
            text-align: center;
            background: #f8f9fa;
            border-radius: 8px;
        }

        .qna-back-area {
            text-align: center;
            margin-top: 30px;
        }

        .qna-back-btn {
            display: inline-block;
            padding: 8px 18px;
            border-radius: 999px;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            background: #e5e7eb;
            color: #111827;
            transition: 0.16s ease;
        }

        .qna-back-btn:hover {
            background: #d1d5db;
        }
        
        /* 삭제 버튼 스타일 */
        .btn-delete {
            font-size: 12px; 
            color: #d32f2f; 
            text-decoration: underline; 
            cursor: pointer;
            border: none;
            background: none;
            padding: 0;
        }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">
        <div class="table-section qna-detail-wrap">
            <div class="section-title">
                <span>❓ Q&amp;A</span>
                <a href="<%= ctx %>/QnAController?cmd=qna_list" class="btn-home">목록으로 돌아가기</a>
            </div>

            <div class="qna-detail">
                <h2 class="qna-detail-title">
                    Q. ${q.title}
                </h2>

                <div class="qna-detail-meta">
                    <span>작성자: <strong>${q.writer_name}</strong></span>
                    <span>
                        작성일: ${q.created_at}
                        &nbsp;|&nbsp;
                        조회수: ${q.view_count}
                    </span>
                </div>

                <div class="qna-question-body">
                    ${fn:replace(q.content, newLine, "<br>")}
                </div>
                
                <c:if test="${sessionScope.sessionUser != null && (sessionScope.sessionUser.jdi_user == q.writer_user || sessionScope.sessionUser.jdi_role == 'ADMIN')}">
                    <div style="text-align: right; margin-bottom: 20px;">
                        <form action="<%= ctx %>/QnAController" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                            <input type="hidden" name="cmd" value="qna_delete">
                            <input type="hidden" name="q_id" value="${q.q_id}">
                            <button type="submit" class="btn-delete">삭제하기</button>
                        </form>
                    </div>
                </c:if>

                <h3 class="qna-answer-title">A. 답변</h3>

                <c:if test="${empty aList}">
                    <p class="qna-empty-answer">아직 등록된 답변이 없습니다.</p>
                </c:if>
                
                <c:forEach var="ans" items="${aList}">
                    <div class="answer-box">
                        <div class="answer-header">
                            <span class="admin-badge">답변</span>
                            <b>${ans.writer_name}</b>
                            <span class="answer-meta-date">(${ans.created_at})</span>
                        </div>
                        <div class="answer-content">
                            ${fn:replace(ans.content, newLine, "<br>")}
                        </div>
                    </div>
                </c:forEach>

                <div class="qna-back-area">
                    <a href="<%= ctx %>/QnAController?cmd=qna_list" class="qna-back-btn">목록으로 돌아가기</a>
                </div>
            </div>
        </div>
    </div>

</body>
</html>