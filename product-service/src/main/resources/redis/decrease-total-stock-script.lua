-- 주문취소(총 재고 복구), 이벤트 저장 및 품절 상태 해제 스크립트
-- KEYS: [dedupKey, eventIdCounterKey, processingQueueKey, 상품ID1, 상품ID2, ...]
-- ARGV: [statusPrefix, totalPrefix, SOLD_OUT, ON_SALE, orderId, timestamp, eventType, eventStatus, dedupTTL, 수량1, 수량2, ...]

-- 에러 메시지 상수
local ERR_NONPOSITIVE_QTY = 'Quantity must be positive for product: '
local ERR_TOTAL_NOT_SET = 'Total stock not set for product: '
local ERR_MISSING_QTY = 'Quantity argument missing for product: '

-- 키 정의
local dedupKey = KEYS[1]
local eventIdCounterKey = KEYS[2]
local processingQueueKey = KEYS[3]

-- 상수 정의
local statusPrefix = ARGV[1]
local totalPrefix = ARGV[2]
local soldOutStatus = ARGV[3]
local onSaleStatus = ARGV[4]
local orderId = ARGV[5]
local timestamp = ARGV[6]
local eventType = ARGV[7]
local eventStatus = ARGV[8]
local dedupTTL = tonumber(ARGV[9])

-- 결과 및 처리 데이터 초기화
local resultString = "[총 재고 복구 결과]"
local eventOperations = {}

-- 1단계: 모든 상품의 유효성 검증 및 처리할 작업 수집
for i = 4, #KEYS do
    local productId = KEYS[i]
    local quantityIndex = i + 6

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
    local totalStock = tonumber(totalStockStr)

    -- 상태 키 및 현재 상태 확인
    local statusKey = statusPrefix .. productId
    local currentStatus = redis.call('GET', statusKey)

    -- 처리 정보 수집
    table.insert(eventOperations, {
        productId = productId,
        orderId = orderId,
        amount = amount,
        totalKey = totalKey,
        totalStock = totalStock,
        statusKey = statusKey,
        currentStatus = currentStatus
    })
end

-- 2단계: 이벤트 저장 및 총재고 복구 작업 실행
for _, op in ipairs(eventOperations) do
    local productId = op.productId
    local orderId = op.orderId
    local amount = op.amount
    local totalKey = op.totalKey
    local totalStock = op.totalStock
    local statusKey = op.statusKey
    local currentStatus = op.currentStatus

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

    -- 총재고 증가 실행
    local newTotal = redis.call('INCRBY', totalKey, amount)

    -- 상태 변경 처리 (품절->판매중)
    if currentStatus == soldOutStatus and newTotal > 0 then
        redis.call('SET', statusKey, onSaleStatus)
    end

    -- 결과 메시지 생성
    local resultMsg = string.format("상품 ID %s 주문 취소 완료. 복구된 재고량: %s, 새로운 총 재고량: %s",
            productId, amount, newTotal)

    resultString = resultString .. "\n" .. resultMsg
end

-- 3단계: 중복 처리 방지를 위한 키 설정
redis.call('SET', dedupKey, "0")
redis.call('EXPIRE', dedupKey, dedupTTL)

return { resultString }