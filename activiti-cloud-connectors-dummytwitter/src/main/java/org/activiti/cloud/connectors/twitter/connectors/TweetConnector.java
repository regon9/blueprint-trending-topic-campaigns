package org.activiti.cloud.connectors.twitter.connectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.model.IntegrationRequestEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEvent;
import org.activiti.cloud.connectors.starter.model.IntegrationResultEventBuilder;
import org.activiti.cloud.connectors.twitter.model.RankedAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ObjectMapper mapper;

    private final IntegrationResultSender integrationResultSender;

    public TweetConnector(IntegrationResultSender integrationResultSender) {
        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = TweetConnectorChannels.TWEET_CONSUMER)
    public void processEnglish(IntegrationRequestEvent event) throws InterruptedException, IOException {

        Map<String, String> rewardsText = (Map<String, String>) event.getVariables().get("rewardsText");

        for (String author : rewardsText.keySet()) {
            RankedAuthor rankedAuthor = mapper.convertValue(author,
                                                            RankedAuthor.class);


            logger.info(append("service-name",
                               appName),
                        "Tweeting >>> To: " + rankedAuthor.getUserName() + " related to the campaign: " +
                                " Reward:" + rewardsText.get(author) + " -> nroOfTweets: " + rankedAuthor.getNroOfTweets());
        }

        Map<String, Object> results = new HashMap<>();

        Message<IntegrationResultEvent> message = IntegrationResultEventBuilder.resultFor(event)
                .withVariables(results)
                .buildMessage();
        integrationResultSender.send(message);
    }
}