<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>신규 단어 등록 관리</title>

    <!-- 공통 레이아웃 & 관리자 테마 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/design.css">
</head>
<body>

    <!-- 공통 헤더 -->
    <jsp:include page="/include/header.jsp" />

    <!-- ✅ CSS에 맞춘 레이아웃 클래스 사용 -->
    <div class="admin-container">

        <!-- (원하면 여기 admin-header / dashboard-grid 같은 영역을 둘 수도 있음) -->
        <!--
        <div class="admin-header">
            <div class="admin-title">관리자 페이지 - 신규 단어 대기열</div>
            <div class="admin-info">
                <span>등록 대기 중인 단어들을 관리할 수 있습니다.</span>
            </div>
        </div>
        -->

        <div class="table-section">
            <div class="section-title">
                <span>📢 신규 단어 등록 대기열</span>
                <a href="${pageContext.request.contextPath}/adminMain.apply" class="btn-home">관리자 홈</a>
            </div>

            <!-- ✅ 테이블 클래스 이름을 req-table 로 변경 -->
            <table class="req-table">
                <thead>
                    <tr>
                        <th>요청번호</th>
                        <th>요청자(ID)</th>
                        <th>단어</th>
                        <th>요미가나</th>
                        <th>한국어 뜻</th>
                        <th>JLPT</th>
                        <th>요청일</th>
                        <th>처리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty list}">
                            <c:forEach var="dto" items="${list}">
                                <tr>
                                    <td>${dto.reqId}</td>
                                    <td>${dto.jdiUser}</td>
                                    <td style="font-weight:bold; color:var(--mnu-blue);">
                                        ${dto.word}
                                    </td>
                                    <td>${dto.doc}</td>
                                    <td>${dto.korean}</td>
                                    <td>
                                        <span style="background:#eef3ff; padding:3px 6px; border-radius:4px; font-size:11px; font-weight:bold;">
                                            ${dto.jlpt}
                                        </span>
                                    </td>
                                    <td>${dto.regDate}</td>
                                    <td>
                                        <!-- ✅ CSS에 맞춘 버튼 클래스(btn-ok / btn-no) 사용 -->
                                        <button class="btn-ok"
                                                onclick="if (confirm('이 단어를 사전에 반영하시겠습니까?')) 
                                                         location.href='${pageContext.request.contextPath}/approve.apply?id=${dto.reqId}'">
                                            승인
                                        </button>
                                        <button class="btn-no"
                                                onclick="if (confirm('정말 거절하시겠습니까?')) 
                                                         location.href='${pageContext.request.contextPath}/reject.apply?id=${dto.reqId}'">
                                            거절
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" style="padding: 40px; color: #999; text-align:center;">
                                    대기 중인 신청이 없습니다.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>

    </div>

</body>
</html>
