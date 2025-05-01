-- KEYS: 중복키, 이벤트카운터
-- KEYS: dedupKey, eventIdCounterKey

-- ARGV: 총재고접두사, 사용재고접두사, 판매상태접두사, 이벤트처리큐접두사, 중복키만료시간
-- ARGV: productId1, orderId1, quantity1, timestamp1,
--       productId2, orderId2, quantity2, timestamp2,
--       ...

local totalPrefix = ARGV[1]
local usedPrefix = ARGV[2]
local statusPrefix = ARGV[3]
local eventQueuePrefix = ARGV[4]
local dedupTTL = ARGV[5]

local dedupKey = KEYS[1]
local eventIdCounter = KEYS[2]

local dupValue = ARGV[7] .. ':' .. ARGV[9]

-- item으로 변환
local items = {}
for i = 6, #ARGV, 4 do
    table.insert(items, {
        productId = ARGV[i],
        orderId = ARGV[i + 1],
        quantity = tonumber(ARGV[i + 2]),
        timestamp = ARGV[i + 3],
    }
    )
end

-- 유효성 검증 순회
for _, item in ipairs(items) do
    if not item.productId or not item.orderId or not item.quantity or not item.timestamp then
        return redis.error_reply('invalid parameter: ' .. tostring(item))
    end

    local stockKey = totalPrefix .. item.productId
    local currentStock = tonumber(redis.call('GET', stockKey)) or 0
    local updatedStock = currentStock - item.quantity

    local usedStockKey = usedPrefix .. item.productId
    local usedStock = tonumber(redis.call('GET', usedStockKey)) or 0
    local updatedUsedStock = usedStock - item.quantity

    -- 총재고 존재 검증
    if not currentStock then
        return redis.error_reply('totalStock NOT_FOUND: productId=' .. item.productId)
    end

    -- 사용량 존재 검증
    if not usedStock then
        return redis.error_reply('usedStock NOT_FOUND: productId=' .. item.productId)
    end

    -- 총재고 차감 수량 검증
    if currentStock < item.quantity then
        return redis.error_reply('totalStock shortage: productId=' .. item.productId .. ' quantity=' .. item.quantity .. ' totalStock=' .. currentStock)
    end

    -- 사용량 차감 수량 검증
    if usedStock < item.quantity then
        return redis.error_reply('usedStock shortage: productId=' .. item.productId .. ' quantity=' .. item.quantity .. ' usedStock=' .. usedStock)
    end

    -- 원래 총재고, 원래 사용량, 변경 총재고, 변경 사용량 추가
    item.currentStock = currentStock
    item.updatedStock = updatedStock
    item.usedStock = usedStock
    item.updatedUsedStock = updatedUsedStock
end

-- -- 실제 재고 차감 처리
local results = {}
for _, item in ipairs(items) do
    -- 유니크 이벤트 ID
    local updateEventIdCounter = redis.call('INCR', eventIdCounter)
    local eventId = item.productId .. ':' .. item.orderId .. ':' .. item.timestamp .. ':' .. updateEventIdCounter

    -- 이벤트 해시 저장
    redis.call('HSET', 'event:' .. eventId,
            'productId', item.productId,
            'orderId', item.orderId,
            'quantity', item.quantity,
            'type', 'DECREASE',
            'status', 'PENDING',
            'timestamp', item.timestamp
    )

    -- 고유 score 생성
    local timestampBase = tonumber(item.timestamp) * 1000
    local productIdNumeric = tonumber(item.productId)
    if not productIdNumeric then
        return redis.error_reply('invalid productId, must be numeric: ' .. tostring(item.productId))
    end
    local score = timestampBase + productIdNumeric

    -- ZADD 처리 대기 큐
    redis.call('ZADD', eventQueuePrefix, score, eventId)

    local stockKey = totalPrefix .. item.productId
    local usedStockKey = usedPrefix .. item.productId

    -- 총 재고 감소
    redis.call('SET', stockKey, item.updatedStock)

    -- 사용량 감소
    redis.call('SET', usedStockKey, item.updatedUsedStock)

    if item.updatedStock <= 0 then
        redis.call('SET', statusPrefix .. item.productId, 'SOLD_OUT')
    end

    -- 결과 정보 저장
    table.insert(results, string.format(
            '상품ID: %s 재고: %d->%d 사용량: %d->%d',
            item.productId, item.currentStock, item.updatedStock, item.usedStock, item.updatedUsedStock)
    )
end

-- 중복 처리 방지를 위한 키 설정
redis.call('SADD', dedupKey, dupValue)
local ttl = redis.call('TTL', dedupKey)
if ttl < 0 then
    redis.call('EXPIRE', dedupKey, dedupTTL)
end

return results
