package com.qfedu.demo.Redis;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Random;

@RestController
public class UserController {
    @RequestMapping("/sendCode")
    public String sendCode(String phone) {
        if (phone == null) {
            return "error";
        }
//       1. 生成验证码
        String verifyCode = genCode(4);
        Jedis jedis = new Jedis("47.98.225.173", 6379);
//        2. 存储验证码
        String phoneKey = "phone_num:" + phone;
        jedis.setex(phoneKey, 30, verifyCode);

        String num = jedis.get("num:" + phone);
        if (num == null) {
            jedis.setex("num" + phone, 3600*24, "3");
        } else if (num != null && !num.equals("1")) {
            jedis.decr("num:" + phone);
            System.out.println(jedis.get("num:" + phone));
        } else if (jedis.get("num:" + phone).equals("1")) {
            System.out.println(jedis.get("num:" + phone).equals("1"));
            return "num";
        }

        jedis.close();
//        3. 发送验证码
        System.out.println(verifyCode);
//        4. 返回
        return "success";

    }

    public String genCode(int code_length) {
        String code = "";
        for (int i = 0; i < code_length; i++) {
            int num = new Random().nextInt(10);
            code += num;
        }
        return code;
    }
    @RequestMapping("/verifiCode")
    public String verifiCode(String phone, String verify_code) {
//        判断参数
        if (verify_code == null) {
            return "error";
        }
//        验证
        Jedis jedis = new Jedis("47.98.225.173", 6379);
        String phoneKey = jedis.get("phone_num:" + phone);

        System.out.println(phoneKey);

        if (verify_code.equals(phoneKey)) {
            return "success";
        }

        jedis.close();
        return "error";
    }
}
