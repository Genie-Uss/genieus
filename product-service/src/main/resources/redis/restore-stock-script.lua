-- 재고 복구, 이벤트 저장 및 품절 상태 해제 스크립트
-- KEYS: [상품ID1, 상품ID2, ...]
-- ARGV: [statusPrefix, totalPrefix, usedPrefix, SOLD_OUT, ON_SALE, orderId, timestamp,
--       eventIdCounterKey, processingQueueKey, eventType, eventStatus, dedupPrefix, dedupTTL, 수량1, 수량2, ...]

-- 에러 메시지 상수
local ERR_NONPOSITIVE_QTY = 'Quantity must be positive for product: '
local ERR_TOTAL_NOT_SET = 'Total stock not set for product: '
local ERR_MISSING_QTY = 'Quantity argument missing for product: '
local ERR_OVER_RESTORE = 'Trying to restore more than used stock for product: '

-- 상수 정의
local statusPrefix = ARGV[1]
local totalPrefix = ARGV[2]
local usedPrefix = ARGV[3]
local soldOutStatus = ARGV[4]
local onSaleStatus = ARGV[5]
local orderId = ARGV[6]
local timestamp = ARGV[7]

-- 이벤트 관련 키 및 상태값
local eventIdCounterKey = ARGV[8]
local processingQueueKey = ARGV[9]
local eventType = ARGV[10]
local eventStatus = ARGV[11]
local dedupPrefix = ARGV[12]
local dedupTTL = tonumber(ARGV[13])

-- 결과 및 처리 데이터 초기화
local results = {}
local statusChanges = {}

-- 1단계: 모든 상품의 복구 가능성 확인
for i = 1, #KEYS do
    local productId = KEYS[i]
    local quantityIndex = i + 13

    -- 수량 유효성 검사
    local quantityStr = ARGV[quantityIndex]
    if quantityStr == nil then
        return redis.error_reply(ERR_MISSING_QTY .. productId)
    end
    local amount = tonumber(quantityStr)
    if amount <= 0 then
        return redis.error_reply(ERR_NONPOSITIVE_QTY .. productId)
    end

    -- 총 재고 확인
    local totalKey = totalPrefix .. productId
    local totalStockStr = redis.call('GET', totalKey)
    if not totalStockStr then
        return redis.error_reply(ERR_TOTAL_NOT_SET .. productId)
    end

    -- 사용 재고 및 복구 가능 여부 확인
    local usedKey = usedPrefix .. productId
    local usedStock = tonumber(redis.call('GET', usedKey) or '0')

    -- 사용한 재고보다 많이 복구하려는 경우 방지
    if amount > usedStock then
        return redis.error_reply(ERR_OVER_RESTORE .. productId)
    end

    -- 상태 키 및 현재 상태 확인
    local statusKey = statusPrefix .. productId
    local currentStatus = redis.call('GET', statusKey)

    -- 상태 변경 필요성 확인 (품절->판매중)
    local totalStock = tonumber(totalStockStr)
    local newUsed = usedStock - amount
    if currentStatus == soldOutStatus and newUsed < totalStock then
        table.insert(statusChanges, { statusKey, onSaleStatus })
    end

    -- 중복 이벤트 확인용 키 생성
    local deduplicationKey = dedupPrefix .. productId .. ':' .. orderId

    -- 이미 처리된 이벤트인지 확인
    local isDuplicate = redis.call('EXISTS', deduplicationKey)

    if isDuplicate == 0 then
        -- 새로운 이벤트인 경우만 처리
        -- 이벤트 ID 생성 및 저장
        local eventIdCounter = redis.call('INCR', eventIdCounterKey)
        local eventId = productId .. ':' .. orderId .. ':' .. timestamp .. ':' .. eventIdCounter

        -- 이벤트 데이터 해시로 저장
        redis.call('HSET', 'event:' .. eventId,
                'productId', productId,
                'orderId', orderId,
                'quantity', amount,
                'type', eventType,
                'status', eventStatus,
                'timestamp', timestamp)

        -- 상품별 이벤트 리스트에 추가
        redis.call('LPUSH', 'product:' .. productId .. ':events', eventId)

        -- 처리 대기 큐에 추가
        redis.call('ZADD', processingQueueKey, timestamp, eventId)

        -- 중복 처리 방지를 위한 키 설정 (TTL 설정)
        redis.call('SET', deduplicationKey, timestamp)
        redis.call('EXPIRE', deduplicationKey, dedupTTL)

        -- 재고 복구 실행
        local newUsed = redis.call('DECRBY', usedKey, amount)

        -- 결과 추가
        table.insert(results, productId)
        table.insert(results, tostring(newUsed))
    else
        -- 이미 처리된 이벤트인 경우 현재 사용량만 가져옴
        local currentUsed = redis.call('GET', usedKey) or '0'

        -- 결과 추가
        table.insert(results, productId)
        table.insert(results, currentUsed)
    end
end

-- 상태 변경 (품절 -> 판매중)
for _, statusChange in ipairs(statusChanges) do
    local statusKey = statusChange[1]
    local newStatus = statusChange[2]

    -- 상태 변경
    redis.call('SET', statusKey, newStatus)
end

return results