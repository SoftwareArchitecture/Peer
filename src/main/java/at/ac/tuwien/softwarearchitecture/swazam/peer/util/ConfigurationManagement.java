package at.ac.tuwien.softwarearchitecture.swazam.peer.util;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;


public class ConfigurationManagement {

	private ConfigurationManagement() {
	}

	/**
	 * 
	 * @return information about the Client attached to this Peer. Used in the query billing process for adding/removing coins to Client by the server.
	 * Also used to identify a request/response (as they are asynyhcronous)
	 */
	public static ClientInfo loadClientInfo() {
		ClientInfo clientInfo = null;
		try {
			InputStream inputStream = ClassLoader
					.getSystemResourceAsStream("/config/ClientInfo.xml");

			if (inputStream != null) {
				 JAXBContext context = JAXBContext.newInstance(ClientInfo.class);
				 Unmarshaller unmarshaller = context.createUnmarshaller();
				 clientInfo = (ClientInfo) unmarshaller.unmarshal(inputStream);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			return clientInfo;
		}
	}
	
	/**
	 * 
	 * @return IP and Port of the web services exposed by the Server. Used in registering to the server
	 */
	public static ServerInfo loadServerInfo() {
		ServerInfo serverInfo = null;
		try {
			InputStream inputStream = ClassLoader
					.getSystemResourceAsStream("/config/ServerInfo.xml");

			if (inputStream != null) {
				 JAXBContext context = JAXBContext.newInstance(ServerInfo.class);
				 Unmarshaller unmarshaller = context.createUnmarshaller();
				 serverInfo = (ServerInfo) unmarshaller.unmarshal(inputStream);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			return serverInfo;
		}
	}

	/**
	 * 
	 * @return peer port, IP and ID, which are sent to the Server when registering, and to other Peer entities
	 */
	public static PeerInfo loadPeerInfo() {
		PeerInfo peerInfo = null;
		try {
			InputStream inputStream = ClassLoader
					.getSystemResourceAsStream("/config/PeerInfo.xml");

			if (inputStream != null) {
				 JAXBContext context = JAXBContext.newInstance(PeerInfo.class);
				 Unmarshaller unmarshaller = context.createUnmarshaller();
				 peerInfo = (PeerInfo) unmarshaller.unmarshal(inputStream);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			return peerInfo;
		}
	}
}
