package com.mg.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    OSSClient ossClient;

    @Test
    void contextLoads() {
        String bucketName = "while-false";
        String objectName = "u=770518518,460399405&fm=26&gp=0.jpg";
        String filePath= "C:\\Users\\34634\\Pictures\\u=770518518,460399405&fm=26&gp=0.jpg";
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));

        // 上传文件。
        ossClient.putObject(putObjectRequest);

        System.out.println("上传文件成功！");
    }

}
