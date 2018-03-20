package org.activiti.cloud.connectors.ranking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankingController {

    private Logger logger = LoggerFactory.getLogger(RankingController.class);

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rank/{topic}")
    public List<RankedAuthor> getRanking(@PathVariable("topic") String topic) {
        logger.info(">>> Getting Ranked Authors for Campaign: " + topic);
        return rankingService.getRanking(topic);
    }
}

