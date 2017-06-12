package Main.Java;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;

import billing.BillingOuterClass.Kafka;
import billing.BillingOuterClass.PaymentBackend;
import kafka.producer.Producer;
import serializer.MessageDeserializer;
import serializer.MessageSerializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
public class VisaProcessor {
	
	
	static KafkaProducer<Integer, Kafka> producer ;
	
	public static void main(String[] args) {
		Map<String, Object> ConsumerConfig = new HashMap<>();
		ConsumerConfig.put("group.id", "VisaProcessors");
		ConsumerConfig.put("bootstrap.servers", "localhost:9092");
		
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", "localhost:9092");
	
	
		 producer = new KafkaProducer<>(props, new IntegerSerializer(), new MessageSerializer());
	
		KafkaConsumer<Integer,Kafka> consumer = new KafkaConsumer<Integer,Kafka>( ConsumerConfig, new IntegerDeserializer(), new MessageDeserializer());
	
		consumer.subscribe(Collections.singletonList(kafkaProperties.topic));	
		ConsumerRecords<Integer, Kafka> records = consumer.poll(1000);	
		
		System.out.println(records.count());
		
		for(ConsumerRecord<Integer, Kafka> rec : records){
			System.out.println("Consumed "+rec.topic()+rec.toString());	
			sendResponse(rec);
						
		}
		 consumer.close();
		 
		
	}
	
	public static void sendResponse(ConsumerRecord<Integer,Kafka>  rec){
		
		 String validMessageType = PaymentBackend.getDescriptor().toString();
		 Kafka kafkaMessage = rec.value();
         PaymentBackend pymtMsg ;
	        
         if(kafkaMessage.getMessageType()!=validMessageType){
        	 
        	 System.out.println("Invalid message type");
        	 return;
         }
         
		try {
			pymtMsg = PaymentBackend.parser().parseFrom(kafkaMessage.getSerializedMessage());
			// pymtMsg =
			// PaymentBackend.parser().parseFrom(kafkaMessage.getSerializedMessage());
		} catch (Exception e) {
			System.out.println("unable to parse" + e.getMessage());
			return;
		}
				
		pymtMsg.toBuilder().setPaymentAccepted(pymtMsg.getClientPaymentInfo().getAmount() % 2 == 0).build();	
		
		 if (!pymtMsg.getPaymentAccepted()){
			 pymtMsg.toBuilder().setErrorMessage("Payment was declined").build();
			 
		 }

		 kafkaMessage.toBuilder().setSerializedMessage(pymtMsg.toByteString());
		MyProducer proc = new MyProducer(pymtMsg.getProxyTopic(),false);
		
		
		proc.run();
				
				
				
	        
	        }
	
	

}
