-- test user 생성: 이메일: test123@test.com, 비밀번호: Test!123, 권한: MASTER_ADMIN 조건의 로그인 유저 생성
INSERT INTO m_auth_user (user_id, created_at, created_by, deleted_at, deleted_by, updated_at, updated_by, email,
                         is_active, password, role)
VALUES (1, '2025-04-08 22:28:02.000000', 1, null, null, null, null, 'test123@test.com', true,
        '$2a$10$/OuXtJxHEg7aW6ocDRVzbuzxAs85cAzyqB5oGw19Xw2nRshLjXk8u', 'MASTER_ADMIN');