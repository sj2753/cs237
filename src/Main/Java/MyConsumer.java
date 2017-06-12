package Main.Java;


import org.apache.kafka.clients.consumer.ConsumerConfig;

import kafka.common.TopicAndPartition;
import kafka.consumer.SimpleConsumer;
import kafka.javaapi.*;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;


import kafka.utils.ShutdownableThread;
import serializer.MessageDeserializer;
import serializer.MessageSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.requests.MetadataRequest;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.kafka.common.serialization.Serializer;

import billing.BillingOuterClass.*;


public class MyConsumer {
private  final KafkaConsumer<Integer, Kafka> consumer;
private  final String topic;
private  final KafkaProducer<Integer, Kafka> producer;


public MyConsumer(String topic) {
	
    //super("KafkaConsumerExample", false);
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    //props.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaProperties.CLIENT_ID);
    

    this.consumer = new KafkaConsumer<>(props, new IntegerDeserializer(), new MessageDeserializer());
    this.topic = topic;
    this.producer = new KafkaProducer<Integer, Kafka>(props, new IntegerSerializer(), new MessageSerializer());
   
}


public void doWork() {
	
	
	
	System.out.println("Consumer work begins for the topic "+this.topic);
	
	
	try{
		
	this.consumer.subscribe(Collections.singletonList(this.topic));	
	
	
    ConsumerRecords<Integer, Kafka> records = this.consumer.poll(1000);
    System.out.println("no of messages in consumer =  "+records.count());

    
    for (ConsumerRecord<Integer, Kafka> record : records) {
        //System.out.println("Received message: ("+record.topic()+","+ record.key() + ", " + record.value() + ") at offset " + record.offset());
        
        
        sendResponse(record);
        
    }
	}catch(Exception e){
    	e.getMessage();
    }finally{
    	this.consumer.close();
    }
}
    
    public void sendResponse(ConsumerRecord<Integer,Kafka> record){
    	
    
        
        Kafka kafkaMessage = record.value();
         
        
        PaymentBackend pymtMsg ;
        
        try {
			pymtMsg = PaymentBackend.parser().parseFrom(kafkaMessage.getSerializedMessage());
		
			//This works
			//System.out.println("The proxy topic  is " +pymtMsg.getProxyTopic());
			PaymentBackend paymt = PaymentBackend.newBuilder()
					.setClientPaymentInfo(pymtMsg.getClientPaymentInfo())
					.setTransactionId(pymtMsg.getTransactionId())
					.setProxyTopic(pymtMsg.getProxyTopic())
					.setPaymentAccepted(true)
					.setErrorMessage("No Errors ")
					.build();
			Kafka responseMessage  = Kafka.newBuilder().setMessageType(kafkaMessage.getMessageType()).setSerializedMessage(paymt.toByteString()).build();
			
			//System.out.println("lets see"+ responseMessage.getMessageType());
			
			System.out.println("Amount is "+pymtMsg.getClientPaymentInfo().getAmount());
		
			
			if( pymtMsg.getClientPaymentInfo().getAmount()%2==0){
				
				
				this.producer.send(new ProducerRecord<>(paymt.getProxyTopic(),1, responseMessage)).get();
				//System.out.println("Response message(1) sent  to proxy --> ( "+responseMessage.toString()+" )");
			}else{
				
				PaymentBackend.newBuilder(paymt).setErrorMessage("tanala").build();
				Kafka.newBuilder(responseMessage).setSerializedMessage(paymt.toByteString());
				
				//responseMessage.toBuilder().setSerializedMessage(paymt.toBuilder().setErrorMessage("There is an error in processing").build().toByteString()).build();
				
				/*paymt.toBuilder().setErrorMessage("There is an error in processing");
				responseMessage.toBuilder().setSerializedMessage(paymt.toByteString());*/
				
				this.producer.send(new ProducerRecord<>(paymt.getProxyTopic(),1, responseMessage)).get();
				//System.out.println("Response message(2) sent  to proxy --> ( "+kafkaMessage.toString()+" )");
			}
				
				this.consumer.subscribe(Collections.singletonList(paymt.getProxyTopic()));
				
				ConsumerRecords<Integer,Kafka> recorder = this.consumer.poll(1000);
				for(ConsumerRecord<Integer, Kafka> rec : recorder){
					System.out.println("Received message at Proxy  :  ("+rec.topic()+","+ rec.key() + ", " + rec.value() + ") at offset " + rec.offset());
				
			}
        } catch (InvalidProtocolBufferException | InterruptedException | ExecutionException e) {
        	System.out.println("Exception has occured");
			e.printStackTrace();
		
    }
        
    }
}
			
			
       
