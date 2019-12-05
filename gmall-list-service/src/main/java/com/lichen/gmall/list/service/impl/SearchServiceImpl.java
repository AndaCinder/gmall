package com.lichen.gmall.list.service.impl;

import com.lichen.gmall.bean.SkuLsInfo;
import com.lichen.gmall.bean.SkuLsParam;
import com.lichen.gmall.list.util.MatchQueryBuilderLiChen;
import com.lichen.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 搜索模块Service 注入JestClient ES来处理业务
 * @author 李琛
 * 2019/7/23 - 9:57
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public List<SkuLsInfo> search(SkuLsParam skuLsParam) {
        //es查询
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        Search search = new Search.Builder(getMyDsl(skuLsParam)).addIndex("gmall").addType("SkuLsInfo").build();;
        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo source = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                List<String> list = highlight.get("skuName");
                String s = list.get(0);
                source.setSkuName(s);
                skuLsInfos.add(source);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return skuLsInfos;
    }


    /**
     * 通过SearchSourceBuilder等Builder类拼接查询语句
     * @return 返回dsl查询语句
     */
    public String getMyDsl(SkuLsParam skuLsParam){

        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();
        String catalog3Id = skuLsParam.getCatalog3Id();

        //创造查询的dsl语句
        SearchSourceBuilder dsl = new SearchSourceBuilder();
        //创建查询query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder t = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(t);
        }
        //query对象过滤语句
        if (null!=valueId&&valueId.length > 0){
            for (int i = 0; i < valueId.length; i++) {
                TermQueryBuilder t = new TermQueryBuilder("skuAttrValueList.valueId",valueId[i]);
                boolQueryBuilder.filter(t);
            }
        }
//        //创建过滤语句
//        TermQueryBuilder t1 = new TermQueryBuilder("skuAttrValueList.valueId","45");
//        boolQueryBuilder.filter(t1);
//        TermQueryBuilder t2 = new TermQueryBuilder("skuAttrValueList.valueId","1");
//        boolQueryBuilder.filter(t2);
//        TermQueryBuilder t3 = new TermQueryBuilder("skuAttrValueList.valueId","5");
//        boolQueryBuilder.filter(t3);


        //创建匹配语句
        if (StringUtils.isNotBlank(keyword)){
            MatchQueryBuilderLiChen matchQueryBuilder = new MatchQueryBuilderLiChen("skuName",keyword);
            matchQueryBuilder.autoGenerateSynonymsPhraseQuery(false);
            boolQueryBuilder.must(matchQueryBuilder);
        }


        dsl.query(boolQueryBuilder);
        /**
         * 设置高亮字段
         */
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("skuName");
        //这可以替换高亮字体的前面和后面<em></em>，
        highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
        highlightBuilder.postTags("</span>");
        dsl.highlighter(highlightBuilder);
        dsl.size(100);
        dsl.from(0);

        System.out.println(dsl.toString());
        return dsl.toString();
    }
}
