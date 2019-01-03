package com.example.demo;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private static final String VISIT = "visit";
    private static final String INCREMENT = "increment";
    private static final String USER_IP = "user_ip";
    private static final String TIMESTAMP = "timestamp";
    private static final String CONTENT = "content";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name,
                             HttpServletRequest req) {
        String userIp = req.getRemoteAddr();
        long increment = counter.incrementAndGet();

        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        KeyFactory keyFactory = datastore.newKeyFactory().setKind(VISIT);
        IncompleteKey key = keyFactory.setKind(VISIT).newKey();

        // Record a visit to the datastore, storing the IP and timestamp.
        FullEntity<IncompleteKey> curVisit =
                FullEntity
                        .newBuilder(key)
                        .set(USER_IP, userIp)
                        .set(TIMESTAMP, Timestamp.now())
                        .set(INCREMENT, increment)
                        .set(CONTENT, name)
                        .build();
        datastore.add(curVisit);


        return new Greeting(increment, String.format(template, name));
    }

    @GetMapping("/greetings")
    public List<GreetingInfo> greetingList(){
        // Retrieve the last 10 visits from the datastore, ordered by timestamp.
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        Query<Entity> query = Query
                .newEntityQueryBuilder()
                .setKind(VISIT)
                .setOrderBy(StructuredQuery.OrderBy.desc(TIMESTAMP))
                .setLimit(10)
                .build();
        QueryResults<Entity> results = datastore.run(query);

        List<GreetingInfo> greetings = new ArrayList<>();
        while (results.hasNext()) {
            Entity entity = results.next();

            greetings.add(GreetingInfo.fromEntity(entity));
        }
        return greetings;
    }
}