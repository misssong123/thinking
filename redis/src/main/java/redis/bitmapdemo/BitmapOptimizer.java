package redis.bitmapdemo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.BitPosParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BitmapOptimizer {
    private Jedis jedis;

    public BitmapOptimizer(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 批量设置位
     */
    public void batchSetBits(String key, List<Long> offsets, boolean value) {
        // 使用管道批量操作
        try (Pipeline pipeline = jedis.pipelined()) {
            for (Long offset : offsets) {
                pipeline.setbit(key, offset, value);
            }
            pipeline.sync();
        }
    }

    /**
     * 批量获取位
     */
    public List<Boolean> batchGetBits(String key, List<Long> offsets) {
        try (Pipeline pipeline = jedis.pipelined()) {
            List<Response<Boolean>> responses = new ArrayList<>();

            for (Long offset : offsets) {
                responses.add(pipeline.getbit(key, offset));
            }

            pipeline.sync();

            return responses.stream()
                    .map(Response::get)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 压缩稀疏的Bitmap
     */
    public String compressSparseBitmap(String compressedKey, String destKey) {
        // 获取所有为1的位
        List<Long> bitPositions = new ArrayList<>();

        // 使用BITPOS遍历所有为1的位
        long pos = 0;
        while (true) {
            long nextPos = jedis.bitpos(compressedKey, true,new BitPosParams(pos));
            if (nextPos < 0 || nextPos < pos) {
                break;
            }
            bitPositions.add(nextPos);
            pos = nextPos + 1;
        }

        // 创建压缩后的Bitmap（使用更紧凑的偏移量）
        if (!bitPositions.isEmpty()) {
            long minOffset = bitPositions.get(0);
            try (Pipeline pipeline = jedis.pipelined()) {
                for (Long originalOffset : bitPositions) {
                    long compressedOffset = originalOffset - minOffset;
                    pipeline.setbit(destKey, compressedOffset, true);
                }
                pipeline.sync();
            }

            // 存储压缩信息
            Map<String, String> hash = new HashMap<>();
            hash.put("min_offset", String.valueOf(minOffset));
            hash.put("compressed_count", String.valueOf(bitPositions.size()));
            jedis.hset(destKey + ":meta", hash);
        }

        return destKey;
    }

    /**
     * 恢复压缩的Bitmap
     */
    public void decompressBitmap(String compressedKey, String destKey) {
        // 获取压缩信息
        Map<String, String> meta = jedis.hgetAll(compressedKey + ":meta");
        if (meta.isEmpty()) {
            return;
        }
        long minOffset = Long.parseLong(meta.get("min_offset"));
        // 获取所有位并恢复
        List<Long> compressedOffsets = new ArrayList<>();
        long pos = 0;
        while (true) {
            long nextPos = jedis.bitpos(compressedKey, true,new BitPosParams(pos));
            if (nextPos < 0 || nextPos < pos) {
                break;
            }
            compressedOffsets.add(nextPos);
            pos = nextPos + 1;
        }

        // 恢复原始偏移量
        try (Pipeline pipeline = jedis.pipelined()) {
            for (Long compressedOffset : compressedOffsets) {
                long originalOffset = compressedOffset + minOffset;
                pipeline.setbit(destKey, originalOffset, true);
            }
            pipeline.sync();
        }
    }
}
