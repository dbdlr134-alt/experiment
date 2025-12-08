<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 확인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <style>
        .check-wrap { display: flex; justify-content: center; padding-top: 100px; }
        .check-box { 
            background: #fff; width: 400px; padding: 40px; text-align: center;
            border-radius: 20px; box-shadow: 0 10px 30px rgba(158,173,255,0.2);
        }
        .input-pw { width: 100%; height: 50px; border: 2px solid #ddd; border-radius: 10px; padding: 0 15px; margin: 20px 0; font-size: 16px; }
        .btn-check {width: 100%;
            padding: 15px; 
            /* ★ [수정] var(--main-color) 대신 고정된 파란색 계열 색상 적용 */
            background: #0C4DA1; 
            color: #fff; 
            border: none; border-radius: 30px; 
            font-size: 16px; font-weight: bold; cursor: pointer; }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />
    
    <div class="inner check-wrap">
        <div class="check-box">
            <h3>보안 확인</h3>
            <p style="color:#888; font-size:14px; margin-top:10px;">
                정보를 수정하려면 비밀번호를 입력하세요.
            </p>
            
            <form action="checkPass.do" method="post">
                <input type="password" name="pw" class="input-pw" placeholder="비밀번호 입력" required>
                <button type="submit" class="btn-check">확인</button>
            </form>
        </div>
    </div>
</body>
</html>