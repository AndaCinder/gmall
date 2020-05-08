package com.lichen.gmall.ware.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lichen.gmall.bean.WareSku;
import com.lichen.gmall.service.WareSkuService;
import com.lichen.gmall.vo.StockDelVo;
import com.lichen.gmall.ware.mapper.WareSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 李琛
 * 2020/4/30 - 23:15
 */
@Service
public class WareSkuServiceImpl implements WareSkuService {

    @Autowired
    private WareSkuMapper wareSkuMapper;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 减库存
     * @param stockDelVos
     */
    @Override
    public void deleteByUserIds(List<StockDelVo> stockDelVos) {
        stockDelVos.parallelStream().forEach(vo -> {
            CompletableFuture<WareSku> wareSkuFuture = CompletableFuture.supplyAsync(() -> {
                List<WareSku> wareSkus = this.wareSkuMapper.select(new WareSku().setSkuId(Long.parseLong(vo.getSkuId())));
                WareSku wareSku = wareSkus.get(0);
                wareSku.setStock(wareSku.getStock() - vo.getSkuNum());
                return wareSku;
            }, threadPoolExecutor);
            wareSkuFuture.thenAcceptAsync(wareSku ->{
                this.wareSkuMapper.updateByPrimaryKeySelective(wareSku);
            },threadPoolExecutor);
        });
    }
}
