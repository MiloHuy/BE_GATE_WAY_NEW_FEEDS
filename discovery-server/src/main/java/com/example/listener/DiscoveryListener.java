package com.example.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DiscoveryListener {

  private static final Logger log = LoggerFactory.getLogger(DiscoveryListener.class);

  @EventListener
  public void onApplicationEvent(EurekaInstanceRegisteredEvent event) {
    log.info("Instance registered: {}", event.getInstanceInfo().getAppName());
  }

  @EventListener
  public void onInstanceDown(EurekaInstanceCanceledEvent event) {
    log.info("Instance down: {}", event.getAppName());
  }

  @EventListener
  public void onInstanceRenewed(EurekaInstanceRenewedEvent event) {
    log.info("Instance renewed: {}", event.getAppName());
  }

  @EventListener
  public void onInstanceUp(EurekaInstanceRegisteredEvent event) {
    log.info("Instance up: {}", event.getInstanceInfo().getAppName());
  }
}
