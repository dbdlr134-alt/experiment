<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>답변 작성 - My J-Dic Admin</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
    
    <style>
        /* 상세 페이지 전용 스타일 */
        .view-container {
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            margin-bottom: 20px;
        }

        /* 질문 영역 스타일 */
        .q-header { border-bottom: 2px solid #eee; padding-bottom: 15px; margin-bottom: 20px; }
        .q-title { font-size: 1.5rem; font-weight: bold; color: #333; margin-bottom: 10px; }
        .q-info { color: #888; font-size: 0.9rem; display: flex; gap: 15px; }
        .q-content { font-size: 1.1rem; line-height: 1.6; color: #444; min-height: 100px; }

        /* 답변 리스트 스타일 */
        .ans-section { margin-top: 40px; border-top: 1px solid #ddd; padding-top: 30px; }
        .ans-item { 
            background: #f1f8e9; /* 연한 초록색 배경 */
            border: 1px solid #c5e1a5; 
            border-radius: 8px; 
            padding: 20px; 
            margin-bottom: 15px; 
        }
        .ans-writer { font-weight: bold; color: #2e7d32; margin-bottom: 5px; display: block; }
        .ans-date { font-size: 0.85rem; color: #666; font-weight: normal; }

        /* 답변 작성 폼 스타일 */
        .write-box { 
            background: #fafafa; 
            border: 1px solid #eee; 
            padding: 20px; 
            border-radius: 8px; 
            margin-top: 30px;
        }
        .write-box textarea {
            width: 100%;
            height: 150px;
            padding: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            resize: vertical;
            font-size: 1rem;
            margin-bottom: 15px;
        }
        .btn-submit {
            background-color: var(--mnu-blue); 
            color: white; 
            padding: 10px 25px; 
            border: none; 
            border-radius: 4px; 
            font-weight: bold; 
            cursor: pointer;
            font-size: 1rem;
        }
        .btn-submit:hover { background-color: #1565c0; }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="admin-container">
        
        <div class="table-section">
            <div class="section-title">
                <span>💬 질문 상세 및 답변 작성</span>
                <a href="${pageContext.request.contextPath}/QnAController?cmd=admin_qna_list" class="btn-home">목록으로</a>
            </div>

            <div class="view-container">
                <div class="q-header">
                    <div class="q-title">Q. ${q.title}</div>
                    <div class="q-info">
                        <span>작성자: <strong>${q.writer_user}</strong></span>
                        <span>작성일: ${q.created_at}</span>
                        <span>조회수: ${q.view_count}</span>
                    </div>
                </div>
                <div class="q-content">
                    ${fn:replace(q.content, newLine, "<br>")}
                </div>

                <div class="ans-section">
                    <h3>등록된 답변 <span style="color:var(--mnu-blue); font-size:0.8em;">(${fn:length(aList)})</span></h3>
                    
                    <c:choose>
                        <c:when test="${empty aList}">
                            <p style="color:#999; padding:10px;">아직 등록된 답변이 없습니다.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="ans" items="${aList}">
                                <div class="ans-item">
                                    <span class="ans-writer">
                                        ${ans.writer_user} <span class="ans-date">(${ans.created_at})</span>
                                    </span>
                                    <div>${fn:replace(ans.content, newLine, "<br>")}</div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="write-box">
                    <h4 style="margin-bottom:15px;">✏️ 새 답변 작성하기</h4>
                    
                    <form action="${pageContext.request.contextPath}/QnAController?cmd=admin_answer_write" method="post">
                        <input type="hidden" name="q_id" value="${q.q_id}">
                        
                        <textarea name="content" placeholder="사용자에게 보낼 답변 내용을 입력하세요."></textarea>
                        
                        <div style="text-align: right;">
                            <button type="submit" class="btn-submit">답변 등록</button>
                        </div>
                    </form>
                </div>

            </div>
        </div>
    </div>

</body>
</html>