package examples.jms.spring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.listener.SessionAwareMessageListener;

import examples.jms.GameCoverService;

/**
 * We implement springs specific listener interface to have access to the session
 * to create a new message and send it. With the normal MessageListener interface
 * we don't have access to it. <br>
 * Selector is defined in container configuration.
 */
public class GameCoverServiceServerImpl implements GameCoverService, SessionAwareMessageListener {
	/**
	 * I want to focus in JMS use, not the implementation of the service
	 */
	private Map<String, String> gameMap;
	private String path;
	

	public byte[] getGameCover(String sku) {
		
		String fileName = gameMap.get(sku);
		return readFromFile(path, fileName);
	}
	
	private byte[] readFromFile(String path, String fileName){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try{
			byte[] buffer = new byte[512];
			InputStream is = 
				this.getClass().getClassLoader().getResourceAsStream(path + "/" + fileName);
			int count = 0;
			while( (count = is.read(buffer)) != -1){
				os.write(buffer, 0, count);
			}	
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return os.toByteArray();
	}

	public void setGameMap(Map<String, String> gameMap) {
		this.gameMap = gameMap;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * We need acces to the session to reply to other queue
	 */
	public void onMessage(Message message, Session session) throws JMSException {
		TextMessage request = (TextMessage)message;
		String sku = request.getText();
		Queue replyTo = (Queue)message.getJMSReplyTo();
		
		BytesMessage response = session.createBytesMessage();
		response.writeBytes(getGameCover(sku));
		response.setJMSCorrelationID(message.getJMSMessageID());
		
		//we don't care the type, what it's important is the destination object
		MessageProducer producer = session.createProducer(replyTo);
		producer.send(response);
		producer.close();
	}
	
}
