package serializer;




import billing.BillingOuterClass.*;
import billing.BillingOuterClass.PaymentBackend;
import org.apache.*;

import org.apache.kafka.common.serialization.Serializer;

public class MessageSerializer extends Adapter implements Serializer<Kafka> {
    @Override
    public byte[] serialize(final String topic, final Kafka data) {
        return data.toByteArray();
    }
}
