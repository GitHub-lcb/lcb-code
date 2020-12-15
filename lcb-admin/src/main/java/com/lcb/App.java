package com.lcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author lcb
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class App {
  public static void main(String[] args) {
    // System.setProperty("spring.devtools.restart.enabled", "false");
    SpringApplication.run(App.class, args);
    System.out.println("==========项目启动成功！==========");
  }
}
