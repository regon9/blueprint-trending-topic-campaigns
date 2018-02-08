package org.activiti.cloud.connectors.twitter.connectors;

import java.util.HashMap;
import java.util.Map;

import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.model.IntegrationRequestEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static net.logstash.logback.marker.Markers.append;

@Component
@EnableBinding(TweetConnectorChannels.class)
public class TweetConnector {

    private Logger logger = LoggerFactory.getLogger(TweetConnector.class);
    @Value("${spring.application.name}")
    private String appName;

    private final IntegrationResultSender integrationResultSender;



    public TweetConnector(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = TweetConnectorChannels.TWEET_CONSUMER)
    public void processEnglish(IntegrationRequestEvent event) throws InterruptedException {


        Map<String, String> rewardsText = (Map<String, String>) event.getVariables().get("rewardsText");


        for(String key: rewardsText.keySet()) {
            logger.info(append("service-name",
                               appName),
                        "Tweeting >>> To: " + key + " related to the campaign: " + " Reward:" + rewardsText.get(key));
        }

        Map<String, Object> results = new HashMap<>();

        Message<IntegrationResultEvent> message = IntegrationResultEventBuilder.resultFor(event)
                .withVariables(results)
                .buildMessage();
        integrationResultSender.send(message);
    }


}