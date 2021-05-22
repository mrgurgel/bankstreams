package dev.legrug.fraudanalysys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.UUID;

import static org.eclipse.microprofile.config.ConfigProvider.getConfig;

public class TopologyProducer {

    private static final Logger LOG = LogManager.getLogger(TopologyProducer.class);
    private static final String ARCHIEVED_TRANSACTION = "achieved-transaction";
    private static final String FRAUD_SUPECT_TOPIC = "fraud-suspect-transaction";
    private static final String FRAUD_SUPECT_PANEL_TOPIC = "fraud-suspect-transaction-panel";
    private static final String SUSPECT_CLIENT = "Al Capone";

    private static final Double MAX_VALUE = getConfig().getValue("fraud-analysis.maxallowedvalue", Double.class);

    @Inject
    @Channel(FRAUD_SUPECT_PANEL_TOPIC)
    Emitter<String> fraudSuspectPanel;

    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<UUID, String> transactionStream =
                builder.stream(ARCHIEVED_TRANSACTION, Consumed.with(Serdes.UUID(), Serdes.String()));

        transactionStream
            .filter((id, jsonContent) -> doesThisTransactionExceedsMaximumValue(jsonContent))
            .to(FRAUD_SUPECT_TOPIC);

        transactionStream
                .filter((id, jsonContent) -> doesThisTransactionExceedsMaximumValue(jsonContent))
                .foreach((id, jsonContent) -> {
                    String jsonContentWithSuspectionLevel = identifyAndAddSuspectionLevel(jsonContent);
                    fraudSuspectPanel.send(jsonContentWithSuspectionLevel);
                });

        return builder.build();
    }

    private String identifyAndAddSuspectionLevel(String jsonContent) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);
            String clientName = jsonNode.get("clientName").asText();
            if(clientName.contains(SUSPECT_CLIENT)) {
                ((ObjectNode)jsonNode).put("level", "high");
            }
            else {
                ((ObjectNode)jsonNode).put("level", "low");
            }
            return jsonNode.toString();
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return jsonContent;
    }

    private boolean doesThisTransactionExceedsMaximumValue(String jsonContent) {
        try {
            Double transactionValue = new ObjectMapper().readTree(jsonContent).get("value").asDouble();
            return transactionValue >= MAX_VALUE;
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return false;
    }


}
