package com.deveagles.be15_deveagles_be.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris:http://localhost:9200}")
  private String elasticsearchHost;

  @Override
  public ClientConfiguration clientConfiguration() {
    String hostAndPort = elasticsearchHost.replace("http://", "").replace("https://", "");

    return ClientConfiguration.builder()
        .connectedTo(hostAndPort)
        .withConnectTimeout(60000)
        .withSocketTimeout(60000)
        .build();
  }
}
