package serializer;

import billing.BillingOuterClass;
import billing.BillingOuterClass.Kafka;
import billing.BillingOuterClass.PaymentBackend;
import kafka.message.Message;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

public class MessageDeserializer extends Adapter implements Deserializer<Kafka> {
	@Override
    public Kafka deserialize(final String topic, byte[] data) {
        try {
            return Kafka.parseFrom(data);
        } catch (final InvalidProtocolBufferException e) {
            
            throw new RuntimeException("Received unparseable message " + e.getMessage(), e);
        }
    }
}
