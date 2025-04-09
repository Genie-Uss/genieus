-- 토큰 저장
redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])

-- 사용자 ID와 토큰 ID 매핑 정보 저장 (Set 자료구조 사용)
redis.call('SADD', KEYS[2], ARGV[3])
redis.call('PEXPIRE', KEYS[2], ARGV[2])

return 1