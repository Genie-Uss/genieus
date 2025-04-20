-- KEYS: 상품 ID 목록
-- ARGV: 총재고접두사, 사용재고접두사, 판매상태접두사, 이벤트처리큐접두사
-- ARGV: productId1, orderId1, quantity1, timestamp1,
--       productId2, orderId2, quantity2, timestamp2,
--       ...
local results = {}
local totalPrefix = ARGV[1]
local usedPrefix = ARGV[2]
local statusPrefix = ARGV[3]
local eventQueuePrefix = ARGV[4]

local i = 5
while i <= #ARGV do
  local productId = ARGV[i]
  local orderId = ARGV[i + 1]
  local quantity = tonumber(ARGV[i + 2])
  local timestamp = ARGV[i + 3]

  -- 유니크 이벤트 ID
  local eventIdCounter = redis.call('INCR', 'event_id_counter')
  local eventId = productId .. ':' .. orderId .. ':' .. timestamp .. ':' .. eventIdCounter

  -- 이벤트 해시 저장
  redis.call('HSET', 'event:' .. eventId,
    'productId', productId,
    'orderId', orderId,
    'quantity', quantity,
    'eventType', 'DECREASE',
    'eventStatus', 'PENDING',
    'completedAt', timestamp
  )

  -- 이벤트 리스트에 추가
  redis.call('LPUSH', 'product:' .. productId .. ':events', eventId)

  -- 총 재고 감소
  local stockKey = totalPrefix .. productId
  local currentStock = tonumber(redis.call('GET', stockKey)) or 0
  local updatedStock = currentStock - quantity

  redis.call('SET', stockKey, updatedStock)

  if updatedStock <= 0 then
    redis.call('SET', statusPrefix .. productId, 'SOLD_OUT')
  end

  -- 사용 재고 초기화
  redis.call('SET', usedPrefix .. productId, 0)

  -- ZADD 처리 대기 큐
  redis.call('ZADD', eventQueuePrefix, tonumber(timestamp), eventId)

  i = i + 4

  -- 결과 정보 저장
  local line = 'productId=' .. productId .. ', updatedStock=' .. updatedStock
  table.insert(results, line)
end

return results
