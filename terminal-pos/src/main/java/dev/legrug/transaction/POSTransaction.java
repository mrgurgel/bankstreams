package dev.legrug.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import java.util.*;

@RequestScoped
public class POSTransaction implements Serializable {

    private static final Logger LOG = Logger.getLogger(POSTransaction.class);
    private static List<Integer> transactionTypes = Arrays.asList(1,2,3,4,5);

    public UUID id;
    public String value;
    public UUID clientId;
    public String clientName;
    public String terminal = "POS";
    public Integer transactionType;

    public static POSTransaction buildARandomTransacion() {
        POSTransaction posTransaction = new POSTransaction();
        Faker faker = new Faker();
        posTransaction.clientId = UUID.randomUUID();
        posTransaction.value = faker.commerce().price().replaceAll(",", ".");
        posTransaction.id = UUID.randomUUID();
        Collections.sort(transactionTypes);
        posTransaction.transactionType = transactionTypes.get(0);
        return posTransaction;
    }

    String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return this.toString();
    }

    @Override
    public String toString() {
        return "POSTransaction{" +
                "value=" + value +
                ", clientsName='" + clientId + '\'' +
                ", id=" + id +
                '}';
    }
}
