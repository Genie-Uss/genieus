local res = {}

for i, key in ipairs(KEYS) do
    local hash = redis.call("HGETALL", key)
    local map = {}
    for j = 1, #hash, 2 do
        map[hash[j]] = hash[j + 1]
    end

    local json = string.format(
            '{"productId":"%s","orderId":"%s","quantity":%s,"type":"%s","status":"%s","timestamp":%s}',
            map["productId"], map["orderId"], map["quantity"],
            map["type"], map["status"], map["timestamp"]
    )

    res[i] = json
end

return res
