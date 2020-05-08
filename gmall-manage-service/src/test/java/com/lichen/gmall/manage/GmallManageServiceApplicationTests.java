package com.lichen.gmall.manage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

    /*//mock api 模拟http请求
    private MockMvc mockMvc;

    //初始化工作
    @Before
    public void setUp() {
        //独立安装测试
        mockMvc = MockMvcBuilders.standaloneSetup(new JsonCatalogController()).build();
        //集成Web环境测试（此种方式并不会集成真正的web环境，而是通过相应的Mock API进行模拟测试，无须启动服务器）
        //mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    //测试
    @Test
    public void index() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getJson")
                .accept(MediaType.APPLICATION_JSON));
    }*/
  /*  @Autowired
    JsonCatalogService catalogService;

    @Test
    public void contextLoads() {
        catalogService.getJson();
    }*/

}
