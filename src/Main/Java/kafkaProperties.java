package Main.Java;

public class kafkaProperties {
	public static final String topic = "VISA";
	public static final String kafka_server_url = "127.0.0.1";
	public static final int KAFKA_SERVER_PORT = 9092;
	public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
	public static final int CONNECTION_TIMEOUT = 100000;
	public static final String TOPIC2 = "ProxyVISA";
	
	public static final String CLIENT_ID = "VisaClient";

	public kafkaProperties() {
	}
	
	
}
