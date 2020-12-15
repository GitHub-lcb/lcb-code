package com.lcb.core.manager;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author lcb
 */
@Component
public class ShutdownManager {
  private static final Logger logger = LoggerFactory.getLogger("sys-user");

  @PreDestroy
  public void destroy() {
    shutdownAsyncManager();
  }

  /**
   * 停止异步执行任务
   */
  private void shutdownAsyncManager() {
    try {
      logger.info("====关闭后台任务任务线程池====");
      AsyncManager.me().shutdown();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}
