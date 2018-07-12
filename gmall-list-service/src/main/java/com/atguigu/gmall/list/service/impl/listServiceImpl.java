package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class listServiceImpl implements ListService {

    //使用es，需要一个对象
    @Autowired
    JestClient jestClient;

    // 定义一些 index,type.
    public static final String ES_INDEX="gmall"; //数据库

    public static final String ES_TYPE="SkuInfo";  //table

    //保存到es中的方法实现类
    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {

        //准备放入数据  这里的Index相当于数据库，往数据库中存放skuLsInfo，根据检索的KewWord的Id
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        //执行
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //根据skuLsParams参数查询
    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //制作查询字符串
        String query = makeQueryStringForSearch(skuLsParams);
        //准备执行查询
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult searchResult=null;
        //执行查询
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将查询出来得search语句转换为我们需要得java得SkuLsResult类
        SkuLsResult skuLsResult = makeResultForSearch(skuLsParams,searchResult);

        return  skuLsResult;
    }

    //将查询到得数据转换为我们java需要得SkuLsResult类方法实现
    private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        SkuLsResult skuLsResult = new SkuLsResult();
        //从查询得结果集中取得数据
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        //创建一个新得集合存放查询到得数据
        ArrayList<SkuLsInfo> skuLsInfoArrayList = new ArrayList<>();
        //循环遍历List
        if(hits!=null&&hits.size()>0){
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                //取出每一个得对象
                SkuLsInfo skuLsInfo = hit.source;
                //准备取出高亮
                if (hit.highlight!=null&&hit.highlight.size()>0){
                    //取出一个高亮名字得集合
                    List<String> list = hit.highlight.get("skuName");
                    //将高亮得名字取出赋值给对象
                    String skuNameHi = list.get(0);
                    skuLsInfo.setSkuName(skuNameHi);
                }
                //循环一条增加一条
                skuLsInfoArrayList.add(skuLsInfo);
            }
        }
        //更新skuLsResult中得字段
        skuLsResult.setSkuLsInfoList(skuLsInfoArrayList);

        //总条数
        skuLsResult.setTotal(searchResult.getTotal());
        //总页数
        long pages = (searchResult.getTotal()+skuLsParams.getPageSize()-1)/skuLsParams.getPageSize();
        skuLsResult.setTotalPages(pages);

        //聚合
        //从查询得结果集中找到聚合字段
        MetricAggregation aggregations = searchResult.getAggregations();
        //按照名称取得聚合
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");

        //取得buckets
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();

        //需要高亮得KeyWord是一个集合，先创建一个集合
        ArrayList<String> valuesList = new ArrayList<>();
        //判断buckets有无数据
        if (buckets!=null&&buckets.size()>0){
            for (TermsAggregation.Entry bucket : buckets) {
                String key = bucket.getKey();
                valuesList.add(key);
            }
        }
        skuLsResult.setAttrValueIdList(valuesList);
        return skuLsResult;
    }

    //查询方法实现
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        // 先构建一个查询器 solr
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 创建bool对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //判断关键字，后续会根据SkuName进行查询以及高亮
        if(skuLsParams.getKeyword()!=null&&skuLsParams.getKeyword().length()>0){
            //创建Match， Kibana中得DSL语句中得字段
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            //并添加到BOOL中
            boolQueryBuilder.must(matchQueryBuilder);
            //准备高亮显示，先创建一个高亮器
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //要高亮得字段
            highlightBuilder.field("skuName");
            //添加前缀后缀
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            //显示高亮最后一步
            searchSourceBuilder.highlight(highlightBuilder);
        }

        //Term精确查找attrValueId 以及CataLogsId,先判断二者是否为空
        if (skuLsParams.getCatalog3Id()!=null&&skuLsParams.getCatalog3Id().length()>0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            //将其添加到BOOL中
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if (skuLsParams.getValueId()!=null&&skuLsParams.getValueId().length>0){
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", skuLsParams.getValueId()[i]);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //分页
        int from = (skuLsParams.getPageNo()-1)*skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());

        //排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        //聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);

        //最后 将整个DSL语句拼接执行
        searchSourceBuilder.query(boolQueryBuilder);
        String query = searchSourceBuilder.toString();
        System.out.println("query="+query);
        return query;
    }

}
