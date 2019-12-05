package com.lichen.gmall.list;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListWebApplicationTests {

    public static void main(String[] args) {
        File file = new File("");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write("json".getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contextLoads() {
    }

}
