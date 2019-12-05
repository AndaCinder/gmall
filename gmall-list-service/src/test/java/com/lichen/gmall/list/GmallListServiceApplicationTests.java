package com.lichen.gmall.list;

import com.lichen.gmall.bean.SkuInfo;
import com.lichen.gmall.bean.SkuLsInfo;
import com.lichen.gmall.list.util.MatchQueryBuilderLiChen;
import com.lichen.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    JestClient jestClient;

    @Reference
    SkuService skuService;


    /**
     * 通过SearchSourceBuilder等Builder类拼接查询语句
     * @return 返回dsl查询语句
     */
    public static String getMyDsl(){
        //创造查询的dsl语句
        SearchSourceBuilder dsl = new SearchSourceBuilder();
        //创建查询query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //创建过滤语句
        TermQueryBuilder t1 = new TermQueryBuilder("skuAttrValueList.valueId","45");
        boolQueryBuilder.filter(t1);
        TermQueryBuilder t2 = new TermQueryBuilder("skuAttrValueList.valueId","1");
        boolQueryBuilder.filter(t2);
        TermQueryBuilder t3 = new TermQueryBuilder("skuAttrValueList.valueId","5");
        boolQueryBuilder.filter(t3);


        //创建匹配语句
        MatchQueryBuilderLiChen matchQueryBuilder = new MatchQueryBuilderLiChen("skuName","XR");
        matchQueryBuilder.autoGenerateSynonymsPhraseQuery(false);
        boolQueryBuilder.must(matchQueryBuilder);

        dsl.query(boolQueryBuilder);
        dsl.size(20);
        dsl.from(0);
        return dsl.toString();
    }


    /**
     * 查询ES中索引为gmall，type为SkuLsInfo的匹配数据
     */
    @Test
    public void Test1(){
        //es查询
        List<SkuLsInfo> skuLsInfos  = new ArrayList<>();
        String query = getMyDsl();
        Search search = new Search.Builder(query).addIndex("gmall").addType("SkuLsInfo").build();

        try {
            //执行ES查询语句获得hits（命中的结果）
            SearchResult searchResult = jestClient.execute(search);
            if (!searchResult.isSucceeded()) {
                System.out.println("查询出错了，请换种关键词，再输一次！");
                return;
            }
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                //获得结果
                SkuLsInfo skuLsInfo = hit.source;
                skuLsInfos.add(skuLsInfo);
            }
            System.out.println(skuLsInfos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将数据存入ES中，从数据库中查出SkuInfo及其SkuAttrValueList（平台属性）
     * 将数据存入ES索引中，type为SkuLsInfo
     */
    @Test
    public void contextLoads() {
        String catalog3Id = "1";
        //查询mysql中的sku信息
        List<SkuInfo> skuInfos = skuService.getSkuListByCatalog3Id(catalog3Id);

        // 转化ES中的sku信息
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();

        for (SkuInfo skuInfo : skuInfos) {
            SkuLsInfo skuLsInfo = new SkuLsInfo();
            try {
                BeanUtils.copyProperties(skuLsInfo,skuInfo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            skuLsInfos.add(skuLsInfo);
        }

        // 导入到ES中
        for (SkuLsInfo skuLsInfo : skuLsInfos) {
            Index index = new Index.Builder(skuLsInfo).index("gmall").type("SkuLsInfo").id(skuLsInfo.getId()).build();
//            System.out.println(index.toString());
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
