package at.ac.tuwien.softwarearchitecture.swazam.peer.peerManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerRingInformation;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.IFingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.ServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.ConfigurationManagement;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.NetworkUtil;

public class PeerManager implements IPeerManager {
    /*
     * holds the peer ring information, i.e. the rest of the peers in the ring.
     * Replaced with List<IDs> for privacy, to avoid a super peer or other peers
     * knowing the content of all other peers
     */

    private Map<PeerInfo, List<Fingerprint>> peerRing;

    {
        peerRing = new HashMap<PeerInfo, List<Fingerprint>>();
    }
    /**
     * In the case a SubPeer receives a request from the SuperPeer, the request
     * is
     */
    private IMatchingManager matchingManager;
    private ServerCommunicationManager serverCommunicationManager;
    private IFingerprintExtractorAndManager fingerprintExtractorAndManager;
    private int MAX_IDLE_PERIOD = 20000;
    private Date latestSuperPeerRefreshMade = new Date();
	// private List<PeerInfo> peerRing;
    // {
    // peerRing = new ArrayList<PeerInfo>();
    // }
    // info used to bill a client for search/result
    private ClientInfo clientInfo;
	// information regarding the current Peer
    // this is ditributed to the Server and other peers and used in connecting
    // to this Peer
    private PeerInfo peerInfo;
    // it can be scheduled at specific intervals
    private TimerTask checkAlivePeriodSuperPeer = new TimerTask() {
        public void run() {
            Date currentDate = new Date();
            if (currentDate.getTime() - latestSuperPeerRefreshMade.getTime() > MAX_IDLE_PERIOD) {
                performLeaderElection();
            }
        }
    };

    /**
     * If the current peer is superPeer, it needs to continuously broadcast to
     * other peers its info
     */
    private TimerTask broadcastSuperPeerInfoHeartbeat() {

        TimerTask task = new TimerTask() {
            public void run() {
                broadcastSuperPeerInfoHeartBeat();
            }
        };
        return task;
    }

    // used to check if super peer sent its info
    Timer checkForSuperPeerRefreshRate;

    public PeerManager(ServerCommunicationManager serverCommunicationManager, IFingerprintExtractorAndManager fingerprintExtractorAndManager) {
        super();
        this.serverCommunicationManager = serverCommunicationManager;
        peerInfo = ConfigurationManagement.loadPeerInfo();
        Logger.getLogger(PeerManager.class).log(Level.WARN, peerInfo);
        clientInfo = ConfigurationManagement.loadClientInfo();
        peerInfo.setPassword(clientInfo.getPassword());
        peerInfo.setUsername(clientInfo.getUsername());
        this.fingerprintExtractorAndManager = fingerprintExtractorAndManager;
        registerToServer();
    }

    private UUID generatePeerID() {
        return UUID.nameUUIDFromBytes((peerInfo.getIp() + "_" + peerInfo.getPort()).getBytes());
    }

  

    public Map<PeerInfo, List<Fingerprint>> getPeerRing() {
        return peerRing;
    }

    public void setPeerRing(Map<PeerInfo, List<Fingerprint>> peerRing) {
        this.peerRing = peerRing;
    }

    public void addPeers(Map<PeerInfo, List<Fingerprint>> peerRing) {
        this.peerRing.putAll(peerRing);
    }

    public void addPeer(PeerInfo peer, List<Fingerprint> fingerprints) {
        this.peerRing.put(peer, fingerprints);
    }

    public IMatchingManager getMatchingManager() {
        return matchingManager;
    }

    public void setMatchingManager(IMatchingManager matchingManager) {
        this.matchingManager = matchingManager;
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    public void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }
 

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public ServerCommunicationManager getServerCommunicationManager() {
        return serverCommunicationManager;
    }

    public void setServerCommunicationManager(ServerCommunicationManager serverCommunicationManager) {
        this.serverCommunicationManager = serverCommunicationManager;
    }

    public IFingerprintExtractorAndManager getFingerprintExtractorAndManager() {
        return fingerprintExtractorAndManager;
    }

    public void setFingerprintExtractorAndManager(IFingerprintExtractorAndManager fingerprintExtractorAndManager) {
        this.fingerprintExtractorAndManager = fingerprintExtractorAndManager;
    }

    /**
     * takes the request and forwards it to the MatchingManager. It is used in
     * case a search is forwarded to this peer from another PeerManager
     */
    public void searchFingerprint(final ClientInfo client, final Fingerprint fingerprint) {
        Thread matchingThread = new Thread() {
            public void run() {
                if (matchingManager != null) {
                    matchingManager.matchFile(client, fingerprint);
                }
            }
        };

        matchingThread.setDaemon(true);
        matchingThread.start();
    }

    public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint) {
        // TODO Auto-generated method stub
        if (this.peerInfo.getPeerID().equals(this.peerInfo.getSuperPeerID())) {
			// broadcast to all other peers in ring the search request.

            for (Entry<PeerInfo, List<Fingerprint>> entry : peerRing.entrySet()) {

                // check if sub-peer contains desired fingerprint
                List<Fingerprint> peerFingerprints = entry.getValue();
                boolean hasFingeprint = false;
                for (Fingerprint f : peerFingerprints) {
                    if (f.match(fingerprint) == 0.0) {
                        hasFingeprint = true;
                        Logger.getLogger(PeerManager.class.getName()).log(Level.INFO, entry.getKey().getPort() + " contains Fingerprint. Forwarding call ");
                        break;
                    }
                }

				// if sub-peer does not contain the desired fingerprint,
                // continue
                if (!hasFingeprint) {
                    continue;
                }

                boolean isAlive = NetworkUtil.checkIfPortOpen(entry.getKey().getIp(), entry.getKey().getPort());
                if (isAlive) {
                    URL url = null;
                    HttpURLConnection connection = null;
                    try {
                        url = new URL("http://" + entry.getKey().getIp() + ":" + entry.getKey().getPort() + "/Peer/REST_API/search");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/xml");
                        connection.setDoOutput(true);
                        OutputStream os = connection.getOutputStream();
                        JAXBContext jaxbContext = JAXBContext.newInstance(FingerprintSearchRequest.class);
                        jaxbContext.createMarshaller().marshal(new FingerprintSearchRequest(clientInfo, fingerprint), os);
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
                                Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, line);
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (Exception e) {
                        Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, e);
                    }
                } else {
                    peerRing.remove(entry.getKey());
                }

            }
        } else {
            // just ignore request
        }
    }

    public void registerToServer() {
        if (serverCommunicationManager != null) {
            peerInfo = serverCommunicationManager.registerToServer(peerInfo);
            Logger.getLogger(PeerManager.class).log(Level.INFO, "Retrieved peerID: " + peerInfo.getPeerID() + " and superPeerID " + peerInfo.getSuperPeerID());
        }
		// broadcast all fingerprints to SuperPeer

		// if I am first in Ring, server will return my info as SuperPeer
        // if I am superPeer, start behavior for sending heartbeat with my Info
        if (peerInfo.getSuperPeerID().equals(peerInfo.getPeerID())) {
            if (checkForSuperPeerRefreshRate != null) {
                checkForSuperPeerRefreshRate.cancel();
            }
            Timer heartbeatTimer = new Timer();
            // schedule at 1.5 seconds
            heartbeatTimer.schedule(broadcastSuperPeerInfoHeartbeat(), 0, 1500);
            Logger.getLogger(PeerManager.class).log(Level.INFO, "I am super peer and starting heartbeat");
        } else {
            // else send its fingerprints to SuperPeer by joining ring
            Logger.getLogger(PeerManager.class).log(Level.INFO, "I am joining peer network ");
            joinPeerNetwork();
            // schedule at 2 seconds interval
            checkForSuperPeerRefreshRate = new Timer();
            // only check If I am super peer if I am NOT superPeer
            checkForSuperPeerRefreshRate.schedule(checkAlivePeriodSuperPeer, 0, 2000);
        }
    }

    /**
     * Sends its information (PeerInfo and Fingerprints) to the SuperPeer, and
     * receives Ring Information
     */
    public void joinPeerNetwork() {

        // check if SuperPeer is alive
        boolean isAlive = NetworkUtil.checkIfPortOpen(peerInfo.getSuperPeerIp(), peerInfo.getSuperPeerPort());
        if (isAlive) {
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://" + peerInfo.getSuperPeerIp() + ":" + peerInfo.getSuperPeerPort() + "/Peer/REST_API/fingerprints");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type", "application/xml");
                connection.setRequestProperty("Accept", "application/xml");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                JAXBContext jaxbContext = JAXBContext.newInstance(PeerFingerprintInformation.class);
                PeerFingerprintInformation fingerprintInformation = new PeerFingerprintInformation();

                fingerprintInformation.setPeerInfo(peerInfo);
                fingerprintInformation.setFingerprints(this.fingerprintExtractorAndManager.getKnownFingerprints());
                jaxbContext.createMarshaller().marshal(fingerprintInformation, os);
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
                        Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, line);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, e);
                e.printStackTrace();
            }
        } else {

            Logger.getLogger(this.getClass()).log(Level.ERROR,
                    "SuperPeer " + peerInfo.getSuperPeerIp() + ":" + peerInfo.getSuperPeerPort() + " is not responding. Unable to register");
        }

    }

    public void performLeaderElection() {
		// test which Peer is alive. And the Peer with largest ID is compared to
        // this, and the largest ID wins

        // refresh ring by checking who is still alive
        List<Thread> ringRefreshThreads = new ArrayList<Thread>();
        for (final PeerInfo info : peerRing.keySet()) {
            Thread thread = new Thread() {
                public void run() {
                    // check if Peer is alive (if port is not closed)
                    boolean isAlive = NetworkUtil.checkIfPortOpen(info.getIp(), info.getPort());
                    if (!isAlive) {
                        peerRing.remove(info);
                    }
                }
            };
            ringRefreshThreads.add(thread);
        }
        // start threads
        for (Thread t : ringRefreshThreads) {
            t.setDaemon(true);
            t.start();
        }
        // wait for threads to finish
        for (Thread t : ringRefreshThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Logger.getLogger(PeerManager.class.getName()).log(Level.ERROR, e);
            }
        }

        PeerInfo largestPeer = this.peerInfo;
        // get peer with largest ID from map
        for (PeerInfo info : peerRing.keySet()) {
            if (largestPeer.getPeerID().compareTo(info.getPeerID()) < 0) {
                largestPeer = info;
            }
        }

        // updating superPeer
        {
            peerInfo.setSuperPeerID(largestPeer.getPeerID());
            peerInfo.setSuperPeerPort(largestPeer.getPort());
            peerInfo.setSuperPeerIp(largestPeer.getIp());
        }

        // if I am superPeer, start behavior for sending heartbeat with my Info
        if (peerInfo.getSuperPeerID().equals(peerInfo.getPeerID())) {
            Timer heartbeatTimer = new Timer();
            if (checkAlivePeriodSuperPeer != null) {
                checkAlivePeriodSuperPeer.cancel();
            }

            Logger.getLogger(PeerManager.class).log(Level.INFO, "I am super peer ");
            serverCommunicationManager.notifyServerIAmSuperPeer(peerInfo);
            // schedule at 1.5 seconds
            heartbeatTimer.schedule(broadcastSuperPeerInfoHeartbeat(), 0, 1500);
        } else {
            // else send its fingerprints to SuperPeer by joining ring
            Logger.getLogger(PeerManager.class).log(Level.INFO, "I am joining peer network ");
            joinPeerNetwork();

        }
    }

    /**
     * Used to update the fingerprint information about the other peers in the
     * ring
     */
    public void updateRingInformation(PeerRingInformation peerRingInformation) {
        PeerInfo superPeerInfo = peerRingInformation.getSuperPeerInfo();
        // updating superPeer
        {
            peerInfo.setSuperPeerID(superPeerInfo.getPeerID());
            peerInfo.setSuperPeerPort(superPeerInfo.getPort());
            peerInfo.setSuperPeerIp(superPeerInfo.getIp());
        }

        latestSuperPeerRefreshMade = new Date();

        // as this is not super peer, it does not store the fingerprints for now
        for (PeerInfo info : peerRingInformation.getPeerRing()) {
            peerRing.put(info, new ArrayList<Fingerprint>());
        }
    }

    public void broadcastSuperPeerInfoHeartBeat() {
        final PeerRingInformation peerRingInformation = new PeerRingInformation();
        peerRingInformation.setSuperPeerInfo(peerInfo);
        peerRingInformation.addPeerRing(peerRing.keySet());

        List<Thread> sendSuperPeerHeartbeatThreads = new ArrayList<Thread>();
        for (final PeerInfo info : peerRing.keySet()) {

            if (info.getPeerID().equals(peerInfo.getPeerID())) {
                continue;
            }

            Thread thread = new Thread() {
                public void run() {

//					Logger.getLogger(PeerManager.class).log(Level.INFO, "Broadcasting Super Peer ID " + peerInfo.getPeerID() + " to " + info.getPeerID());
                    // call Peer RESTful API
                    URL url = null;
                    HttpURLConnection connection = null;
                    try {
//						Logger.getLogger(PeerManager.class).log(Level.INFO, "Sending super-peer heartbeat to " + info.getIp() + ":" + info.getPort());
                        url = new URL("http://" + info.getIp() + ":" + info.getPort() + "/Peer/REST_API/updateRingInformation");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setInstanceFollowRedirects(false);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("PUT");
                        connection.setRequestProperty("Content-Type", "application/xml");

                        // write message body
                        OutputStream os = connection.getOutputStream();
                        JAXBContext jaxbContext = JAXBContext.newInstance(PeerRingInformation.class);
                        jaxbContext.createMarshaller().marshal(peerRingInformation, os);
                        os.flush();
                        os.close();

                        InputStream errorStream = connection.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                Logger.getLogger(PeerManager.class).log(Level.ERROR, line);
                            }
                        }

                        InputStream inputStream = connection.getInputStream();
                        if (inputStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                Logger.getLogger(PeerManager.class).log(Level.ERROR, line);
                            }
                        }
                    } catch (Exception e) {
                        Logger.getLogger(PeerManager.class).log(Level.ERROR, e);
                    }
                }
            };
            sendSuperPeerHeartbeatThreads.add(thread);
        }
        // start threads
        for (Thread t : sendSuperPeerHeartbeatThreads) {
            t.setDaemon(true);
            t.start();
        }

    }

    public void distributeFingerprintsToSuperpeer(PeerFingerprintInformation fingerprintInformation) {
        this.peerRing.put(fingerprintInformation.getPeerInfo(), new ArrayList<Fingerprint>(fingerprintInformation.getFingerprints()));
    }

    public PeerInfo getCurrentPeerInformation() {
        return peerInfo;
    }
}
