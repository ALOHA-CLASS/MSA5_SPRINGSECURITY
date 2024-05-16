
DROP TABLE IF EXISTS user_social;

CREATE TABLE user_social (
    no INT PRIMARY KEY AUTO_INCREMENT,
    client_id VARCHAR(50) NOT NULL,
    social_type VARCHAR(50) NOT NULL,
    user_id VARCHAR(100)  NOT NULL,
    access_token VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) ,
    expires_at TIMESTAMP  
);


# client_id : 소셜 계정을 식별한 위한 컬럼
# - kakao_${kakao_account.id}
# - naver_${naver_account.id}
# - google_${google_account.id}


SELECT * 
FROM user_social
;


# 소셜 회원 가입 여부 조회
SELECT * 
FROM user_social
WHERE social_type = #{socialType} 
  AND client_id = #{clientId}
;




TRUNCATE user_social;


