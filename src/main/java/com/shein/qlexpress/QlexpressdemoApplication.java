package com.shein.qlexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//        (exclude = {ElasticJobAutoConfiguration.class})
public class QlexpressdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(QlexpressdemoApplication.class, args);
    }
}
