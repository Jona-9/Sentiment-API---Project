package com.project.sentimentapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ConectarApi {
    final private EndPointConfg endPointConfg;

    public ConectarApi(EndPointConfg endPointConfg) {
        this.endPointConfg = endPointConfg;
    }

    public WebClient client() {
        String url = endPointConfg.getUrl();
        return WebClient.builder().baseUrl(url).build();
    }
}
