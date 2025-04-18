-- 1. 쿠폰 중복 발급 체크
local setResult = redis.call("SET", KEYS[1], "true", "NX")
if not setResult then
    return -1 -- 이미 발급된 사용자
end

-- 2. 재고 확인
local stock = tonumber(redis.call("GET", KEYS[2]))
if not stock or stock <= 0 then
    return -2
end

-- 3. 재고 차감
redis.call("DECR", KEYS[2])

-- 4. 발급 내역 저장
redis.call("LPUSH", KEYS[3], ARGV[1])

return 1
