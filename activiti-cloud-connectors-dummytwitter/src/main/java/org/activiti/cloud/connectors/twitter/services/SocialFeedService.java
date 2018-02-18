package org.activiti.cloud.connectors.twitter.services;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.activiti.cloud.connectors.twitter.CampaignMessageChannels;
import org.activiti.cloud.connectors.twitter.TweetRepository;
import org.activiti.cloud.connectors.twitter.model.Tweet;
import org.activiti.cloud.connectors.twitter.model.TweetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(CampaignMessageChannels.class)
public class SocialFeedService {

    private Logger logger = LoggerFactory.getLogger(SocialFeedService.class);
    private AtomicBoolean started = new AtomicBoolean(false);
    private final MessageChannel campaignProducer;
    private final TweetRepository tweetRepository;

    public SocialFeedService(MessageChannel campaignProducer,
                             TweetRepository tweetRepository) {
        this.campaignProducer = campaignProducer;
        this.tweetRepository = tweetRepository;
    }

    public boolean isStarted() {
        return started.get();
    }

    public void start() {
        logger.info("Starting Dummy Feed");
        started.set(true);
    }

    public void stop() {
        logger.info("Stopping Dummy Feed");
        started.set(false);
    }

    @Scheduled(fixedRateString = "${tweet.rate}")
    public void startProcessWithTweet() {
        if (started.get()) {
            TweetEntity tweetEntity = getRandomTweet();

            Tweet t = new Tweet(tweetEntity.getText(),
                                tweetEntity.getAuthor(),
                                tweetEntity.getLang());

            tweet(t);
        }
    }

    public void tweet(Tweet t) {
        logger.info(">> Tweeting: " + t);
        campaignProducer.send(MessageBuilder.withPayload(t).setHeader("lang",
                                                                      t.getLang()).build());
    }

    public TweetEntity getRandomTweet() {
        long count = tweetRepository.count();
        int countInt = Integer.MAX_VALUE;
        if (count < Integer.MAX_VALUE) {
            countInt = (int) count;
        }
        Random random = new Random();
        int id = random.nextInt(countInt) + 1;
        return tweetRepository.findById(new Long(id)).get();
    }
}
