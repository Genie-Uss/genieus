-- 사용자 ID로 세션 ID 조회
local sessionId = redis.call('GET', KEYS[1])

-- 세션 ID가 없으면 nil 반환
if not sessionId then
    return nil
end

-- 세션 ID로 패스포트 데이터 조회
local passportJson = redis.call('GET', KEYS[2] .. sessionId)

-- 결과 반환
return passportJson