<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>나만의 오답노트 - My J-Dic</title>
    <link rel="stylesheet" href="style/style.css?ver=3">
    <style>
        /* 오답노트 전용 스타일 추가 */
        .wrong-badge {
            background-color: #ffebee; color: #e53935;
            padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold;
            margin-left: 10px;
        }
        .wrong-date {
            font-size: 12px; color: #999; float: right; margin-top: 5px;
        }
    </style>
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <section class="daily-section">
        <div class="inner center-box">
            
            <div style="width:100%; max-width:800px;">
                <h3 style="margin-bottom: 20px; color:#555; border-bottom: 2px solid #eee; padding-bottom: 10px;">
                    📝 나만의 오답노트
                </h3>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
				    <span style="color:#666; font-size:14px;">
				        총 <strong>${noteList.size()}</strong>개의 오답이 있습니다.
				    </span>
				    
				    <%-- ★ [추가] 복습 버튼 --%>
				    <a href="QuizController?cmd=quiz_retry" class="btn-action peri" 
				       style="padding: 8px 16px; font-size:14px; background:${noteList.size() >= 10 ? '#9EADFF' : '#ccc'}; cursor:${noteList.size() >= 10 ? 'pointer' : 'not-allowed'};">
				       🔄 오답 복습하기 (10개↑)
				    </a>
				</div>
                <c:choose>
                    <%-- 오답 기록이 있을 때 --%>
                    <c:when test="${not empty noteList}">
                        <ul class="result-list">
                            <c:forEach var="i" items="${noteList}">
                                <li class="result-item">
                                    <a href="WordController?cmd=word_view&word_id=${i.word_id}" style="display:block; text-decoration:none;">
                                        <div style="display:flex; align-items:center; flex-wrap:wrap;">
                                            <span class="word">${i.word}</span>
                                            <span class="doc">[${i.doc}]</span>
                                            
                                            <span class="wrong-badge">
                                                ${i.wrong_count}번 틀림
                                            </span>
                                        </div>
                                        
                                        <div style="margin-top:8px;">
                                            <span class="korean">${i.korean}</span>
                                            <span class="wrong-date">최근 오답일: ${i.wrong_date}</span>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>

                    <%-- 오답 기록이 없을 때 --%>
                    <c:otherwise>
                        <div class="no-result" style="background:#fff; border-radius:15px; box-shadow:0 2px 5px rgba(0,0,0,0.05);">
                            <p style="font-size:18px;">틀린 문제가 없습니다! 완벽해요 🎉</p>
                            <a href="QuizController?cmd=word_quiz" class="btn-action peri" style="margin-top:20px;">퀴즈 풀러 가기</a>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div style="text-align:center; margin-top:30px;">
                    <a href="WordController?cmd=main" class="btn-action" style="background:#eee; color:#555;">메인으로 돌아가기</a>
                </div>

            </div>
        </div>
    </section>

</body>
</html>