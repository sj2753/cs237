package Main.Java;



public class simpleDemo {
	
	
	public static void main(String[] args) {
		
		
      /*MyProducer producerThread = new MyProducer(kafkaProperties.topic, false);
       
		producerThread.run();*/

      MyConsumer consumerThread = new MyConsumer(kafkaProperties.topic);
        
        consumerThread.doWork();

}
}
