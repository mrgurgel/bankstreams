package dev.legrug.transaction;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.UUID;

import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

@ApplicationScoped
public class POSTransactionProducer {

    private static final Logger LOG = LogManager.getLogger(POSTransactionProducer.class);
    public static final String INTERVAL_KEY = "transacion-generator.interval.milliseconds";

    @Inject
    ClientNameHandler clientNameHandler;

    @Outgoing("achieved-transaction")
    public Multi<Record<UUID, String>> generate() {
        return Multi.createFrom().ticks()
                .every(Duration.ofMillis(getConfig().getValue(INTERVAL_KEY, Long.class)))
                .onOverflow().drop()
                .map(tick -> {
                    POSTransaction posTransaction = POSTransaction.buildARandomTransacion();
                    String clientName = clientNameHandler.searchClientName(posTransaction.clientId);
                    posTransaction.clientName = clientName;
                    LOG.debug("Generated transacion: {}", posTransaction.toString());
                    return Record.of(posTransaction.id, posTransaction.toJson());
                });
    }

}
