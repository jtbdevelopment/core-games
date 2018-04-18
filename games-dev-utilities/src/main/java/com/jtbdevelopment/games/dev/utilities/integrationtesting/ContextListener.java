package com.jtbdevelopment.games.dev.utilities.integrationtesting;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Date: 11/16/2014
 * Time: 9:12 PM
 */
@Component
public class ContextListener implements ApplicationContextAware {

  @SuppressWarnings("unused")
  public void ContainerListener() {
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    AbstractGameIntegration.setApplicationContext(applicationContext);
  }

}
