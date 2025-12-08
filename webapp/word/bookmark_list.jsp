<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>나만의 단어장 - My J-Dic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
</head>
<body class="bookmark-page">
    <jsp:include page="/include/header.jsp" />

    <div class="bookmark-wrap">
        <div class="bookmark-header">
            <div>
                <h2 class="bookmark-title">📔 나만의 즐겨찾기 단어장</h2>
                <p class="bookmark-count">
				    <c:choose>
				        <c:when test="${not empty bookmarkList}">
				            총 <strong>${bookmarkList.size()}</strong>개의 즐겨찾기 단어가 있습니다.
				        </c:when>
				        <c:otherwise>
				            아직 즐겨찾기한 단어가 없습니다.
				        </c:otherwise>
				    </c:choose>
				</p>
            </div>
        </div>

        <ul class="bookmark-list">
            <c:choose>
                <c:when test="${not empty bookmarkList}">
                    <c:forEach var="w" items="${bookmarkList}">
                        <li class="bookmark-item">
                            <a class="bookmark-link"
                               href="${pageContext.request.contextPath}/WordController?cmd=word_view&word_id=${w.word_id}">
                                <div class="bookmark-row-top">
                                    <div class="bookmark-word-box">
                                        <span class="word">${w.word}</span>
                                        <span class="doc">[${w.doc}]</span>
                                    </div>
                                    <span class="jlpt-badge">${w.jlpt}</span>
                                </div>
                                <div class="bookmark-row-bottom">
                                    <span class="korean">${w.korean}</span>
                                </div>
                            </a>
                            <a href="${pageContext.request.contextPath}/WordController?cmd=bookmark_toggle&word_id=${w.word_id}" 
                               onclick="return confirm('즐겨찾기에서 삭제하시겠습니까?');"
                               class="bookmark-remove">
                               ✕
                            </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-bookmark">
                        <p>즐겨찾기한 단어가 아직 없습니다.</p>
                        <a href="${pageContext.request.contextPath}/WordController?cmd=main" class="btn-bookmark-main">
                            단어 찾아보기
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </ul>
        
        <div class="bookmark-footer">
            <a href="${pageContext.request.contextPath}/mypage.jsp" class="btn-bookmark-back">
                메인으로 돌아가기
            </a>
        </div>
    </div>
</body>
</html>
