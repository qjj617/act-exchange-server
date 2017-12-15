package com.achain.job;

import com.achain.conf.Config;
import com.achain.domain.dto.TransactionDTO;
import com.achain.service.IBlockchainService;
import com.alibaba.fastjson.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yujianjian
 * @since 2017-12-15 下午4:18
 */
@Component
@Slf4j
public class TransactionJob {


    @Autowired
    private Config config;
    @Autowired
    private IBlockchainService blockchainService;

    @Scheduled(fixedDelay = 10 * 1000)
    public void doTransactionJob() {
        log.info("doTransactionJob|开始|HeaderBlockNum={}", config.headerBlockCount);
        long headerBlockCount = blockchainService.getBlockCount();
        if (headerBlockCount <= config.headerBlockCount) {
            log.info("doTransactionJob|最大块号为[{}],不需要进行扫块", headerBlockCount);
            return;
        }
        for (long blockCount = config.headerBlockCount + 1; blockCount <= headerBlockCount; ++blockCount) {
            Map<String, JSONArray> map = blockchainService.saveActBlock(Long.toString(blockCount));
            if (!CollectionUtils.isEmpty(map)) {
                try {
                    blockchainService.saveTransactions(map);
                } catch (Exception e) {
                    log.error("doTransactionJob|本次任务执行出现异常", e);
                    continue;
                }
            } else {
                break;
            }

        }
        config.headerBlockCount = headerBlockCount;
        log.info("doTransactionJob|结束|nowHeaderBlockNum={}", config.headerBlockCount);
    }

    /**
     * 扫块数据入库
     *
     * @param transactionDTO 数据
     */
    private void saveTransaction(TransactionDTO transactionDTO) {
//        BlockchainRecord blockchainRecord = new BlockchainRecord();
//        blockchainRecord.setTrxId(transactionDTO.getTrxId());
//        blockchainRecord.setTrxTime(transactionDTO.getTrxTime());
//        blockchainRecord.setContractId(transactionDTO.getContractId());
//        blockchainRecord.setBlockNum(transactionDTO.getBlockNum());
//        blockchainRecordService.insert(blockchainRecord);
    }

}
