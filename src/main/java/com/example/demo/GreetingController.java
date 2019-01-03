package com.example.demo;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name,
                             HttpServletRequest req) {
        String userIp = req.getRemoteAddr();
        long increment = counter.incrementAndGet();

        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("visit");
        IncompleteKey key = keyFactory.setKind("visit").newKey();

        // Record a visit to the datastore, storing the IP and timestamp.
        FullEntity<IncompleteKey> curVisit =
                FullEntity
                        .newBuilder(key)
                        .set("user_ip", userIp)
                        .set("timestamp", Timestamp.now())
                        .set("increment", increment)
                        .build();
        datastore.add(curVisit);


        return new Greeting(increment, String.format(template, name));
    }
}