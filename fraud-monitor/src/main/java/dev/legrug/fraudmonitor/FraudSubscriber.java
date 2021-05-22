package dev.legrug.fraudmonitor;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FraudSubscriber {


    @Incoming("fraud-suspect-transaction-panel")
    @Outgoing("monitor-stream")
    @Broadcast
    public String recieve(String transactionJson) {
        System.out.println(transactionJson);
        return transactionJson;
    }

}
