<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>아이디/비밀번호 찾기</title>
    
    <!-- ✅ 공통 스타일 (헤더 및 기본 폰트) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <!-- ✅ 유저 폼 공통 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
    
    <style>
        /* 이 페이지 전용 탭 스타일 */
        .find-container { 
            max-width: 500px; 
            margin: 80px auto; 
            background: #fff; 
            border-radius: 20px; 
            box-shadow: 0 5px 20px rgba(0,0,0,0.1); 
            overflow: hidden; 
        }
        
        /* 탭 버튼 영역 */
        .tab-header { display: flex; background: #f0f2f5; }
        .tab-btn { 
            flex: 1; padding: 20px; text-align: center; cursor: pointer; 
            font-weight: bold; color: #888; 
            border-bottom: 3px solid transparent; 
            transition: 0.3s; 
        }
        .tab-btn:hover { background: #eee; }
        
        /* 활성화된 탭 */
        .tab-btn.active { 
            background: #fff; 
            color: #0C4DA1; /* MNU Blue */
            border-bottom: 3px solid #0C4DA1; 
        }
        
        /* 탭 내용 영역 */
        .tab-content { padding: 40px; display: none; }
        .tab-content.active { display: block; }
        
        /* 버튼 스타일 재정의 */
        .btn-find { 
            width: 100%; padding: 15px; 
            background: #0C4DA1; color: #fff; 
            border: none; border-radius: 30px; 
            font-size: 16px; font-weight: bold; cursor: pointer; 
            margin-top: 20px; transition: 0.3s;
        }
        .btn-find:hover { background: #093b7c; }
        
        /* 입력창 간격 */
        .input-group { margin-bottom: 15px; }
    </style>
</head>
<body>
    <!-- 공통 헤더 포함 -->
    <jsp:include page="/include/header.jsp" />

    <div class="find-container">
        <!-- 탭 버튼 -->
        <div class="tab-header">
            <div class="tab-btn active" onclick="openTab('findId', event)">아이디 찾기</div>
            <div class="tab-btn" onclick="openTab('findPw', event)">비밀번호 찾기</div>
        </div>

        <!-- 1. 아이디 찾기 폼 -->
        <div id="findId" class="tab-content active">
            <h3 style="text-align:center; margin-bottom:20px; color:#555;">이름과 이메일로 찾기</h3>
            
            <form action="${pageContext.request.contextPath}/findId.do" method="post">
                <div class="input-group">
                    <input type="text" name="name" class="input-field" placeholder="이름" required>
                </div>
                <div class="input-group">
                    <input type="email" name="email" class="input-field" placeholder="가입한 이메일" required>
                </div>
                <button type="submit" class="btn-find">아이디 찾기</button>
            </form>
        </div>

        <!-- 2. 비밀번호 찾기 폼 -->
        <div id="findPw" class="tab-content">
            <h3 style="text-align:center; margin-bottom:10px; color:#555;">임시 비밀번호 발급</h3>
            <p style="font-size:13px; color:#999; text-align:center; margin-bottom:25px; line-height:1.4;">
                가입된 정보를 입력하시면<br>휴대폰 문자로 임시 비밀번호를 전송합니다.
            </p>
            
            <form action="${pageContext.request.contextPath}/findPw.do" method="post">
                <div class="input-group">
                    <input type="text" name="id" class="input-field" placeholder="아이디" required>
                </div>
                <div class="input-group">
                    <input type="text" name="name" class="input-field" placeholder="이름" required>
                </div>
                
                <div class="input-group">
                    <input type="text" name="phone" class="input-field" placeholder="가입한 전화번호 (010...)" required>
                </div>
                
                <button type="submit" class="btn-find">비밀번호 재설정</button>
            </form>
        </div>
    </div>

    <script>
        // 탭 전환 스크립트
        function openTab(tabName, evt) {
            // 1. 모든 탭 내용 숨김
            var contents = document.getElementsByClassName("tab-content");
            for (var i = 0; i < contents.length; i++) {
                contents[i].classList.remove("active");
                contents[i].style.display = "none"; // 확실하게 숨김
            }
            
            // 2. 모든 탭 버튼 비활성화
            var btns = document.getElementsByClassName("tab-btn");
            for (var i = 0; i < btns.length; i++) {
                btns[i].classList.remove("active");
            }
            
            // 3. 선택한 탭 내용 보이기
            document.getElementById(tabName).style.display = "block";
            setTimeout(() => { // 약간의 딜레이로 애니메이션 효과 처럼 보이게 (선택사항)
                 document.getElementById(tabName).classList.add("active");
            }, 10);
           
            // 4. 클릭한 버튼 활성화
            evt.currentTarget.classList.add("active");
        }
    </script>
</body>
</html>