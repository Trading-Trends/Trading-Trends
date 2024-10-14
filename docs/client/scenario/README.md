# Jwt 인증 시나리오 


## Step1	사용자 로그인
### (POST http://localhost:19091/api/auth/login)

내용 : accessToken은 header에, refreshToken은 쿠키에 추가

응답 예시 : 
HTTP/1.1 200
Set-Cookie: refreshToken={Refresh Token}; Max-Age=360000; Expires=Fri, 18 Oct 2024 10:32:19 GMT; Path=/; Secure; HttpOnly
Authorization: Bearer {Access Token}

## Step2	Access Token 재발급 성공
### (POST http://localhost:19091/api/auth/refresh-access-token)

내용 : 쿠키에 담긴 refreshToken을 자동으로 확인하고 검증이 완료되면 accessToken을 재발급

응답 예시: 
HTTP/1.1 200
Authorization: Bearer {Access Token}
Date: Mon, 14 Oct 2024 06:40:01 GMT
Content-Length: 0



## Step3	로그아웃
### (POST http://localhost:19091/api/auth/logout)
내용 : 쿠키에 담긴 refreshToken을 제거하고 accessToken 과 refreshToken을 블랙리스트에 추가

## Step4	Access Token 재발급 실패
### (POST http://localhost:19091/api/auth/refresh-access-token)

내용 : 쿠키가 삭제되고 이를 수동으로 추가해도 refreshToken이 블랙리스트에 추가되어 재발급 실패
