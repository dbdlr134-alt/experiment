<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>회원가입 - My J-Dic</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">
</head>
<body>

    <jsp:include page="/include/header.jsp" />

    <div class="auth-wrap">
        <div class="auth-box" style="width: 500px;">
            <h2 class="auth-title">JOIN US</h2>
            
             <!-- 서버 금지어 메시지 -->
      		<p id="server-msg" style="color:#e53935; text-align:center; margin-bottom:10px; display:none;">
            	사용할 수 없는 아이디 또는 이름입니다.
      		</p>
         	   
            <% if("fail".equals(request.getParameter("error"))) { %>
                 <p style="color:#e53935; text-align:center; margin-bottom:10px;">가입 중 오류가 발생했습니다.</p>
            <% } %>
            
            <%-- ★ [수정] 절대 경로 적용 --%>
            <form action="${pageContext.request.contextPath}/join.do" method="post" name="joinForm">
                
                <div class="input-group">
                    <label class="input-label">아이디</label>
                    <input type="text" name="id" id="uid" class="input-field" placeholder="영문, 숫자 4자 이상" required>
                    <!-- 아이디 입력창 아래에 금지어 메시지 span 추가 -->
					<p id="id-msg" style="color:red; font-size:12px; margin-top:5px; display:none;"></p>
                </div>
                

                <div class="input-group">
                    <label class="input-label">비밀번호</label>
                    <input type="password" name="pw" id="upw" class="input-field" placeholder="4자 이상" required>
                </div>
                
                <div class="input-group">
                    <input type="password" id="upw_check" class="input-field" placeholder="비밀번호 확인">
                    <p id="pw-msg" style="color:#e53935; font-size:12px; margin-top:5px; display:none;">비밀번호가 일치하지 않습니다.</p>
                </div>

                <div class="input-group">
                    <label class="input-label">이름</label>
                    <input type="text" name="name" id="uname" class="input-field" placeholder="본명 또는 닉네임" required>
                    <p id="name-msg" style="color:red; font-size:12px; margin-top:5px; display:none;"></p>
                </div>

                <div class="input-group">
                    <label class="input-label">이메일</label>
                    <div style="display:flex; gap:5px; align-items:center;">
                        <input type="text" id="emailId" class="input-field" placeholder="이메일">
                        <span style="font-weight:bold; color:#555;">@</span>
                        <select id="emailDomain" class="input-field">
                            <option value="">선택</option>
                            <option value="naver.com">naver.com</option>
                            <option value="gmail.com">gmail.com</option>
                            <option value="daum.net">daum.net</option>
                        </select>
                    </div>
                    <input type="hidden" name="email" id="realEmail">
                </div>
                
                <div class="input-group">
                    <label class="input-label">휴대폰 번호</label>
                    <div style="display:flex; gap:10px;">
                        <input type="text" id="phone" name="phone" class="input-field" placeholder="01012345678 (하이픈 없이)">
                        <button type="button" onclick="sendSms()" class="btn-submit" style="width:100px; font-size:13px; background:#666; margin:0;">인증</button>
                    </div>
                </div>
                
                <div class="input-group" id="auth-box" style="display:none;">
                    <div style="display:flex; gap:10px;">
                        <input type="text" id="authCode" class="input-field" placeholder="인증번호 4자리">
                        <button type="button" onclick="checkSms()" class="btn-submit" style="width:100px; font-size:13px; background:var(--mnu-blue); margin:0;">확인</button>
                    </div>
                    <input type="hidden" id="isVerified" value="false">
                    <p id="msg-auth" style="font-size:12px; margin-top:5px;"></p>
                </div>
                
                <button type="button" onclick="joinSubmit()" class="btn-submit" style="margin-top:20px;">가입 완료</button>
            </form>
        </div>
    </div>

<script>
    // 이메일 합치기
    const emailId = document.getElementById("emailId");
    const emailDomain = document.getElementById("emailDomain");
    const realEmail = document.getElementById("realEmail");

    function updateEmail() {
        if(emailId.value && emailDomain.value) {
            realEmail.value = emailId.value + "@" + emailDomain.value;
        }
    }
    emailId.addEventListener("keyup", updateEmail);
    emailDomain.addEventListener("change", updateEmail);

    // 비밀번호 확인
    const upw = document.getElementById("upw");
    const upwCheck = document.getElementById("upw_check");
    const pwMsg = document.getElementById("pw-msg");
    upwCheck.addEventListener("keyup", function(){
        if(upw.value !== upwCheck.value) {
            pwMsg.style.display = "block";
        } else {
            pwMsg.style.display = "none";
        }
    });

    // SMS 전송
    function sendSms() {
        const phone = document.getElementById("phone").value;
        if(phone.length < 10) { alert("휴대폰 번호를 확인하세요."); return; }
        fetch('${pageContext.request.contextPath}/sendSms.do?phone=' + phone)
            .then(res => res.text())
            .then(txt => {
                if(txt.trim() === 'success') {
                    alert("인증번호가 발송되었습니다.");
                    document.getElementById("auth-box").style.display = "block";
                } else {
                    alert("발송 실패. 다시 시도해주세요.");
                }
            });
    }

    // SMS 확인
    function checkSms() {
        const code = document.getElementById("authCode").value;
        fetch('${pageContext.request.contextPath}/checkSms.do?code=' + code)
            .then(res => res.text())
            .then(txt => {
                if(txt.trim() === 'ok') {
                    document.getElementById("msg-auth").innerText = "인증 성공!";
                    document.getElementById("msg-auth").style.color = "green";
                    document.getElementById("isVerified").value = "true";
                } else {
                    document.getElementById("msg-auth").innerText = "인증번호가 일치하지 않습니다.";
                    document.getElementById("msg-auth").style.color = "red";
                }
            });
    }

    // 실시간 금지어 체크
    let forbiddenWords = [];
    fetch('${pageContext.request.contextPath}/checkForbidden.do')
        .then(res => res.json())
        .then(data => { forbiddenWords = data; });

    const uid = document.getElementById("uid");
    const idMsg = document.getElementById("id-msg");
    const uname = document.getElementById("uname");
    const nameMsg = document.getElementById("name-msg");

    function checkForbidden(inputValue) {
        const value = inputValue.toLowerCase();
        return forbiddenWords.some(word => value.includes(word.toLowerCase()));
    }

    uid.addEventListener("input", function() {
        if(checkForbidden(uid.value)) {
            idMsg.style.display = "block";
            idMsg.innerText = "사용할 수 없는 아이디입니다.";
        } else {
            idMsg.style.display = "none";
            idMsg.innerText = "";
        }
    });

    uname.addEventListener("input", function() {
        if(checkForbidden(uname.value)) {
            nameMsg.style.display = "block";
            nameMsg.innerText = "사용할 수 없는 이름입니다.";
        } else {
            nameMsg.style.display = "none";
            nameMsg.innerText = "";
        }
    });

    // 가입 제출
    function joinSubmit() {
        if(uid.value.length < 4) { alert("아이디를 4자 이상 입력하세요."); return; }
        if(upw.value.length < 4) { alert("비밀번호를 4자 이상 입력하세요."); return; }
        if(upw.value !== upwCheck.value) { alert("비밀번호가 일치하지 않습니다."); return; }
        if(document.getElementById("isVerified").value !== "true") { alert("휴대폰 인증을 완료해주세요."); return; }
        document.joinForm.submit();
    }

    // 서버 forbidden 메시지 처리
    window.addEventListener('DOMContentLoaded', () => {
        const urlParams = new URLSearchParams(window.location.search);
        const error = urlParams.get('error');
        const serverMsg = document.getElementById('server-msg');
        const idVisible = idMsg.style.display === 'block';
        const nameVisible = nameMsg.style.display === 'block';
        if(error === 'forbidden' && !idVisible && !nameVisible) {
            serverMsg.style.display = 'block';
        }
    });
</script>

</body>
</html>