<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>새 프로필 등록 신청</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/user.css">

    <style>
        .file-box {
            margin: 20px 0;
        }
        .file-box input[type="file"] {
            width: 100%;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #ddd;
            background: #fafafa;
        }
        .preview {
            margin-top: 15px;
            text-align: center;
        }
        .preview img {
            max-width: 150px;
            max-height: 150px;
            border-radius: 50%;
            border: 3px solid #eee;
        }
    </style>
</head>
<body>
    <jsp:include page="/include/header.jsp" />

    <div class="auth-wrap">
        <div class="auth-box">
            <h2 class="auth-title">새 프로필 등록 신청</h2>

            <p style="text-align:center; color:#e53935; margin-bottom:10px; font-weight:bold;">
                ※ 신청이 승인되면 50 P가 차감됩니다.
            </p>
            <p style="text-align:center; color:#666; margin-bottom:20px; line-height:1.6;">
                변경하고 싶은 프로필 이미지를 업로드해 주세요.<br>
                관리자가 검토 후 승인하면 프로필이 변경됩니다.
                사진의 크기는 20mb이하여야합니다.
            </p>

            <!-- ✅ 파일 업로드를 위한 multipart/form-data 필수 -->
            <form action="${pageContext.request.contextPath}/request_profile.do"
                  method="post"
                  enctype="multipart/form-data"
                  class="auth-form"
                  onsubmit="return validateProfileUpload();">

                <div class="file-box">
                    <label class="input-label" for="profileImage">프로필 이미지 파일</label>
                    <input type="file" id="profileImage" name="profileImage"
                           accept="image/*" required>
                </div>

                <div class="preview" id="previewBox" style="display:none;">
                    <p style="font-size:12px; color:#777; margin-bottom:8px;">미리보기</p>
                    <img id="previewImg" src="#" alt="미리보기">
                </div>

                <div class="input-group" style="margin-top:20px;">
                    <label class="input-label" for="comment">신청 메모 (선택)</label>
                    <textarea id="comment" name="comment" class="input-edit"
                              style="height:80px; resize:none;"></textarea>
                </div>

                <button type="submit" class="btn-submit" style="margin-top: 20px; width:100%;">
                    새 프로필 이미지 신청하기
                </button>
            </form>
        </div>
    </div>

    <script>
        function validateProfileUpload() {
            var file = document.getElementById('profileImage').files[0];
            if (!file) {
                alert('프로필 이미지를 선택해 주세요.');
                return false;
            }
            return true;
        }

        // 미리보기
        const input = document.getElementById('profileImage');
        const previewBox = document.getElementById('previewBox');
        const previewImg = document.getElementById('previewImg');

        input.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    previewBox.style.display = 'block';
                };
                reader.readAsDataURL(this.files[0]);
            } else {
                previewBox.style.display = 'none';
            }
        });
    </script>
</body>
</html>
