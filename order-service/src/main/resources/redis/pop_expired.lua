local ids = redis.call('ZRANGEBYSCORE', KEYS[1], '-inf', ARGV[1])
if next(ids) ~= nil then
  redis.call('ZREM', KEYS[1], unpack(ids))
end
return ids