-- 통합 검증, 재고 차감 및 메타 데이터 반환 스크립트
-- KEYS: [상품ID1, 상품ID2, ...]
-- ARGV: [statusPrefix, totalPrefix, usedPrefix, metaPrefix, ON_SALE, 상태값, 수량1, 수량2, ...]

-- 에러 메시지 상수
local ERR_NONPOSITIVE_QTY = 'Quantity must be positive for product: '
local ERR_NOT_ON_SALE = 'Product not available for sale: '
local ERR_NO_TOTAL = 'Total stock is not set for product: '
local ERR_INSUFFICIENT_STOCK = 'Insufficient stock for product: '

local results = {}
local toUpdate = {}
local metaKeys = {}

local statusPrefix = ARGV[1]
local totalPrefix = ARGV[2]
local usedPrefix = ARGV[3]
local metaPrefix = ARGV[4]
local onSaleStatus = ARGV[5]

-- 1단계: 모든 상품의 재고 확인 (어떤 차감도 실행하지 않음)
for i = 1, #KEYS do
    local productId = KEYS[i]
    local quantityIndex = i + 5

    -- 차감 개수 유효성 검사
    local quantityStr = ARGV[quantityIndex]
    local amount = tonumber(quantityStr)
    if amount <= 0 then
        return redis.error_reply(ERR_NONPOSITIVE_QTY .. productId)
    end

    -- 상품 상태 검사
    local statusKey = statusPrefix .. productId
    local status = redis.call('GET', statusKey)
    if status ~= onSaleStatus then
        return redis.error_reply(ERR_NOT_ON_SALE .. productId)
    end

    -- 차감 개수 유효성 검사
    local totalKey = totalPrefix .. productId
    local totalStock = tonumber(redis.call('GET', totalKey) or '0')
    if totalStock == 0 then
        return redis.error_reply(ERR_NO_TOTAL .. productId)
    end

    -- 재고 확인
    local usedKey = usedPrefix .. productId
    local usedStock = tonumber(redis.call('GET', usedKey) or '0')
    if usedStock + amount > totalStock then
        return redis.error_reply(ERR_INSUFFICIENT_STOCK .. productId)
    end

    -- 차감할 정보 저장
    table.insert(toUpdate, { usedKey, amount })
    table.insert(metaKeys, metaPrefix .. productId)
end

-- 2단계: 모든 상품 재고 차감 실행
local usedValues = {}
for i = 1, #toUpdate do
    local data = toUpdate[i]
    local key = data[1]
    local amount = data[2]
    local newUsed = redis.call('INCRBY', key, amount)
    table.insert(usedValues, newUsed)
end

-- 3단계: 메타 정보 조회 및 결과 구성
for i = 1, #KEYS do
    local productId = KEYS[i]
    local metaInfo = redis.call('GET', metaKeys[i])

    table.insert(results, productId)
    table.insert(results, tostring(usedValues[i]))
    table.insert(results, metaInfo or "")
end

return results