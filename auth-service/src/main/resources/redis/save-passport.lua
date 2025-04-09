-- 패스포트 데이터 저장
redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])

-- 사용자 ID와 세션 ID 매핑 정보 저장 (단일 값 사용)
redis.call('SET', KEYS[2], ARGV[3], 'PX', ARGV[2])

return 1