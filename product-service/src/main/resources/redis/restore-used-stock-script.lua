-- 주문만료 (예약취소), 이벤트 저장 및 품절 상태 해제 스크립트
-- KEYS: [상품ID1, 상품ID2, ...]
-- ARGV: [usedPrefix, statusPrefix, soldOutStatus, onSaleStatus, 수량1, 수량2, ...]

-- 에러 메시지 상수
local ERR_NONPOSITIVE_QTY = 'Quantity must be positive for product: '
local ERR_MISSING_QTY = 'Quantity argument missing for product: '
local ERR_OVER_RESTORE = 'Trying to restore more than used stock for product: '

-- 상수 정의
local usedPrefix = ARGV[1]
local statusPrefix = ARGV[2]
local soldOutStatus = ARGV[3]
local onSaleStatus = ARGV[4]

-- 결과 및 처리 데이터 초기화
local resultString = "[재고 예약 취소 결과]"
local statusChanges = {}
local decreaseOperations = {}

-- 1단계: 모든 상품의 유효성 검증 및 처리할 작업 수집
for i = 1, #KEYS do
    local productId = KEYS[i]
    local quantityIndex = i + 4

    -- 수량 유효성 검사
    local quantityStr = ARGV[quantityIndex]
    if quantityStr == nil then
        return redis.error_reply(ERR_MISSING_QTY .. productId)
    end
    local amount = tonumber(quantityStr)
    if amount <= 0 then
        return redis.error_reply(ERR_NONPOSITIVE_QTY .. productId)
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
    if currentStatus == soldOutStatus then
        table.insert(statusChanges, { statusKey, onSaleStatus })
    end

    -- 감소 작업 정보 저장
    table.insert(decreaseOperations, { usedKey, amount, productId, quantityStr })
end

-- 2단계: 예약 재고 감소 작업 실행 및 결과 메시지 생성
for i, op in ipairs(decreaseOperations) do
    local usedKey = op[1]
    local amount = op[2]
    local productId = op[3]
    local quantityStr = op[4]

    -- 재고 복구 실행
    local newUsed = redis.call('DECRBY', usedKey, amount)

    -- 결과 메시지 생성 및 문자열에 추가
    local resultMsg = string.format("상품 ID %s 예약 재고 취소 완료. 취소된 예약 재고량 : %s, 잔여 예약된 재고량 %s",
            productId, quantityStr, newUsed)

    resultString = resultString .. "\n" .. resultMsg
end

-- 3단계: 상태 변경 (품절 -> 판매중)
for _, statusChange in ipairs(statusChanges) do
    local statusKey = statusChange[1]
    local newStatus = statusChange[2]

    -- 상태 변경
    redis.call('SET', statusKey, newStatus)
end

return { resultString }