package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import at.ac.tuwien.softwarearchitecture.swazam.peer.management.PeerManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.MatchingManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.ServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.NetworkUtil;
import java.io.InputStream;
import java.util.Date;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * Class used to instantiate Peer components, and acts as a backbone for the PeerAPI
 *
 */
public class PeerControl {
    
        
    
	private int defaultPort = 1234;
	private String ip = "localhost";
	
	private ServerCommunicationManager communicationManager;
	private MatchingManager matchingManager;
	private PeerManager peerManager;
	
	private static PeerControl instance;
	
	static{
		instance = new PeerControl();
                //initiate Log4J
                 // initiate logger
        {
            String date = new Date().toString();
            date = date.replace(" ", "_");
            date = date.replace(":", "_");
            System.getProperties().put("recording_date", date);

            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                // ClassLoader classLoader =
                // Configuration.class.getClassLoader();

                InputStream log4jStream = ClassLoader.getSystemResourceAsStream("/config/Log4j.properties");

                if (log4jStream != null) {
                    PropertyConfigurator.configure(log4jStream);
                    log4jStream.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
                
                
	}
	
	private PeerControl(){
		//search for open port
		
		while(NetworkUtil.checkIfPortOpen(ip, defaultPort)){
			defaultPort++;
		}
		
		//instantiate all objects and their dependencies
		peerManager = new PeerManager(ip, defaultPort);
		matchingManager = new MatchingManager(peerManager);
		communicationManager = new ServerCommunicationManager();
		communicationManager.setMatchingManager(matchingManager);
		matchingManager.setCommunicationManager(communicationManager);
		peerManager.setMatchingManager(matchingManager);
		
	}
	
	public static PeerControl getInstance(){
		return instance;
	}
	
}
