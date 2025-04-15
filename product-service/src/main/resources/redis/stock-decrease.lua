-- 파라미터:
-- KEYS: 상품 ID 목록
-- ARGV: [총재고접두사, 사용재고접두사, 차감수량1, 차감수량2, ...]

local results = {}
local toUpdate = {}
local totalPrefix = ARGV[1]
local usedPrefix = ARGV[2]

-- 1단계: 모든 상품의 재고 확인 (어떤 차감도 실행하지 않음)
for i = 1, #KEYS do
    local productId = KEYS[i]
    local amount = tonumber(ARGV[i + 2])

    -- 차감 개수 유효성 검사
    if amount <= 0 then
        return redis.error_reply('Invalid amount. Must be positive for product: ' .. productId)
    end

    local totalKey = totalPrefix .. productId
    local usedKey = usedPrefix .. productId

    -- 총 재고 조회: TODO) 문제점 - 총 재고량 캐싱이 없을 경우 문제 발생 -> meta 정보와 함께 캐시 무효화, 재세팅 전략이 필요할듯
    local totalStock = tonumber(redis.call('GET', totalKey) or '0')
    if totalStock == 0 then
        return redis.error_reply('Total stock is 0. Operation aborted for product: ' .. productId)
    end

    -- 사용 재고 조회
    local usedStock = tonumber(redis.call('GET', usedKey) or '0')

    -- 재고 확인
    if usedStock + amount > totalStock then
        return redis.error_reply('Insufficient stock. Operation aborted for product: ' .. productId)
    end

    -- 차감 정보 저장
    table.insert(toUpdate, usedKey)
    table.insert(toUpdate, usedStock + amount)
    table.insert(results, productId)
    table.insert(results, tostring(usedStock + amount))
end

-- 2단계: 모든 상품 재고 차감 실행 (TTL 없이 저장)
for i = 1, #toUpdate, 2 do
    local key = toUpdate[i]
    local value = toUpdate[i + 1]
    redis.call('SET', key, value)
end

return results