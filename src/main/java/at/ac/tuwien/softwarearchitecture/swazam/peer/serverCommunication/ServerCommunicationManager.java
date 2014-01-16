package at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchResponse;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.PeerManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.ConfigurationManagement;

public class ServerCommunicationManager implements IServerCommunicationManager {

    //to execute file searches asynchronously
    private IMatchingManager matchingManager;
    private ServerInfo serverInfo;

    public ServerCommunicationManager() {
        super();
        this.serverInfo = ConfigurationManagement.loadServerInfo();
    }

    public IMatchingManager getMatchingManager() {
        return matchingManager;
    }

    public void setMatchingManager(IMatchingManager matchingManager) {
        this.matchingManager = matchingManager;
    }

    @Override
    public PeerInfo registerToServer(PeerInfo peerInfo) {
//		PeerInfo p =  new PeerInfo();
//		p.setIp(peerInfo.getIp());
//		p.setUsername(peerInfo.getUsername());
//		p.setPassword(peerInfo.getPassword());
//		p.setPeerID(peerInfo.getPeerID());
//		p.setPort(peerInfo.getPort());
//		p.setSuperPeerID(peerInfo.getPeerID());
//		p.setSuperPeerIp(peerInfo.getIp());
//		p.setSuperPeerPort(peerInfo.getPort());
//		
//		return p;
        URL url = null;
        HttpURLConnection connection = null;
        PeerInfo superPeerInfo = null;
        try {
            url = new URL("http://" + serverInfo.getIp() + ":" + serverInfo.getPort() + "/SWazam/webapi/peermanagement/registerpeer");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(PeerInfo.class);
            jaxbContext.createMarshaller().marshal(peerInfo, os);
            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                superPeerInfo = (PeerInfo) jaxbContext.createUnmarshaller().unmarshal(inputStream);
            }
            if (connection != null) {
                connection.disconnect();
            }

        } catch (Exception e) {
            Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, e);
        } finally {
            return superPeerInfo;
        }
    }

    @Override
    public void matchFile(ClientInfo clientInfo, Fingerprint fingerprintToSearch) {
        matchingManager.matchFile(clientInfo, fingerprintToSearch);
    }

    @Override
    public void notifyAboutSearchResult(ClientInfo clientInfo, PeerInfo peerInfo, MusicFileInfo musicFileInfo) {
        Logger.getLogger(ServerCommunicationManager.class).log(Level.INFO, "Found result " + musicFileInfo.getDescription() + " for client " + clientInfo.getClientID());
        final FingerprintSearchResponse response = new FingerprintSearchResponse(clientInfo, peerInfo, musicFileInfo);
        Logger.getLogger(ServerCommunicationManager.class).log(Level.INFO, "Responding to server that we found result for peer with ID " + peerInfo.getPeerID() );
        Thread thread = new Thread() {
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL("http://" + serverInfo.getIp() + ":" + serverInfo.getPort() + "/SWazam/webapi/peermanagement/searchresult");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/xml");
                    connection.setRequestProperty("Accept", "application/xml");
                    connection.setDoOutput(true);

                    OutputStream os = connection.getOutputStream();
                    JAXBContext jaxbContext = JAXBContext.newInstance(FingerprintSearchResponse.class);
                    jaxbContext.createMarshaller().marshal(response, os);
                    os.flush();
                    os.close();
                    
                    InputStream errorStream = connection.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, line);
                        }
                    }

                    InputStream inputStream = connection.getInputStream();
                    if (inputStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, line);
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Exception e) {
                    Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, e);
                }
            }
        };

        thread.setDaemon(true);
        thread.start();

    }

	@Override
	public void notifyServerIAmSuperPeer(PeerInfo peerInfo) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://" + serverInfo.getIp() + ":" + serverInfo.getPort() + "/SWazam/webapi/peermanagement/updatepeer");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
//            connection.setRequestProperty("Accept", "application/xml");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(PeerInfo.class);
            jaxbContext.createMarshaller().marshal(peerInfo, os);
            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, line);
                }
            }
 

        } catch (Exception e) {
            Logger.getLogger(ServerCommunicationManager.class.getName()).log(Level.ERROR, e);
        } 

	}
}
