package Main.Java;


import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;

import com.google.protobuf.ByteString;

import billing.BillingOuterClass.Kafka;
import billing.BillingOuterClass.PaymentBackend;
import billing.BillingOuterClass.PaymentClient;
import billing.BillingOuterClass.PaymentType;
import kafka.producer.Producer;
import serializer.MessageSerializer;
public class MyProducer {
	private final KafkaProducer<Integer, Kafka> producer;
	private final String topic;
	private final Boolean isAsync;


public MyProducer(String topic, Boolean isAsync) {
	
	Properties props = new Properties();
	
	props.put("bootstrap.servers", kafkaProperties.kafka_server_url + ":" + kafkaProperties.KAFKA_SERVER_PORT);
	props.put("client.id", kafkaProperties.CLIENT_ID);
	props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
	props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	producer = new KafkaProducer<Integer, Kafka>(props, new IntegerSerializer(), new MessageSerializer());
	this.topic = topic;
	this.isAsync = isAsync;
	
}

int messageNo = 1;



//Kafka firstMsg = Kafka.newBuilder().setMessageType("VISA").setSerializedMessage();
String msgType = PaymentBackend.getDescriptor().getClass().getName();

PaymentClient val = PaymentClient.newBuilder().setAmount(137).setPaymentType(PaymentType.VISA).build();
PaymentBackend paymt = PaymentBackend.newBuilder()
		.setClientPaymentInfo(val)
		.setTransactionId(122)
		.setProxyTopic(kafkaProperties.TOPIC2)
		.setPaymentAccepted(false)
		.setErrorMessage(" Oops the  Error  occured  ")
		.build();
		

Kafka firstMsg = Kafka.newBuilder().setMessageType(msgType).setSerializedMessage(paymt.toByteString()).build();


public void run( ){
	
		 
		try{
			producer.send(new ProducerRecord<>(topic,messageNo, firstMsg)).get();
			System.out.println("Hi there sent message--> ( "+firstMsg.toString()+" )");
		}catch(Exception  e){
			System.out.println("Error");
			e.getMessage();
		
		
		
		
		
	}
	
}

}