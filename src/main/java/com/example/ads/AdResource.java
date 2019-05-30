package com.example.ads;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@Path("/ads")
public class AdResource {

    private final Map<String, List<String>> keywordsByUser =
          new HashMap<>();
    private final Map<String, List<String>> adsByKeyword =
          new HashMap<>();

    @PostConstruct
    public void init() {
        adsByKeyword.put("java", asList("Try Eclipse IDE, the most mature Java IDE of them all", "IntelliJ IDEA, the best Java IDE on the planet"));
        adsByKeyword.put("awesome", asList("Have you heard the newest song by Billie Eilish?", "People are awesome"));
        adsByKeyword.put("pizza", asList("Pizza? Check out kragleplacki.pl"));
    }

    @Incoming("queries")
    public void consumeQuery(String message) {
        int pipeIdx = message.indexOf("|");

        if (pipeIdx < 1 || pipeIdx >= message.length() - 1) {
            return;
        }

        String userId = message.substring(0, pipeIdx);
        String[] keywords = message.substring(pipeIdx + 1)
              .split("[^\\w]+");

        keywordsByUser.computeIfAbsent(userId, u -> new ArrayList<>())
              .addAll(asList(keywords));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAd(@QueryParam("userId") String userId) {

        List<String> ads = keywordsByUser.getOrDefault(userId, emptyList())
              .stream()
              .map(k -> adsByKeyword.getOrDefault(k, emptyList()))
              .flatMap(List::stream)
              .distinct()
              .collect(Collectors.toList());

        if (ads.isEmpty()) {
            return "Toruń JUG rocks, you know " + userId + "?";
        }

        return ads.get(new Random().nextInt(ads.size()));
    }
}