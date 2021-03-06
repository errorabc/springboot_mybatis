package com.example.demo;

import com.example.demo.run.Dao.Accesstoken_Dao;
import com.example.demo.run.Dao.Sys_Cofig_Dao;
import com.example.demo.run.Entity.AccessToken;
import com.example.demo.run.Entity.sys_config;
import com.example.demo.run.Service.Accesstoken_Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.example.demo.run.Untils.WeixinUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    private Sys_Cofig_Dao sys_cofig_dao;
    @Autowired
    private Accesstoken_Dao accesstoken_dao;


    @Test
    public void contextLoads() {
        sys_config sys_config = new sys_config();
        try {

            AccessToken accessToken = new AccessToken();
            accessToken = accesstoken_dao.find_Accesstoken(accessToken);
            if (null == accessToken) {
             /*
             如果数据库中没有accessToken,就重新生成一个
              */
                sys_config.setName("AppID");
                sys_config = sys_cofig_dao.get(sys_config);
                String name = sys_config.getParameter();

                sys_config.setName("AppSecret");
                sys_config = sys_cofig_dao.get(sys_config);
                String password = sys_config.getParameter();

                AccessToken accessToken2 = new AccessToken();
                accessToken2 = WeixinUtil.getAccessToken(name.trim(), password.trim());

                if (accesstoken_dao.Add_Accesstoken(accessToken2)) {
                    System.out.println("保存成功");
                } else {
                    System.out.println("保存失败");
                }
            } else {
                /*
                数据库中有accessToken
            */

                Date endtime = accessToken.getEnd_time();  //取出数据库中的token的失效时间
                if (new Date().getTime() > endtime.getTime()) {
                    /*
                    token失效了重新获取
                     */
                    sys_config.setName("AppID");
                    sys_config = sys_cofig_dao.get(sys_config);
                    String name = sys_config.getParameter();
                    sys_config.setName("AppSecret");
                    sys_config = sys_cofig_dao.get(sys_config);
                    String password = sys_config.getParameter();

                    AccessToken accessToken2 = new AccessToken();
                    accessToken2 = WeixinUtil.getAccessToken(name.trim(), password.trim());
                    if (accesstoken_dao.update_token(accessToken2)) {
                        System.out.println("更新成功");
                    } else {
                        System.out.println("更新失败");
                    }

                } else {
                    /*
                    token没有失效
                     */
                    System.out.println("token没有失效可以使用");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
