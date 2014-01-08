package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;

import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.FingerprintExtractorAndManager;
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
	private FingerprintExtractorAndManager fingerprintExtractorAndManager;

	// private List<PeerInfo> peerRing;
	// {
	// peerRing = new ArrayList<PeerInfo>();
	// }
	private PeerInfo superPeerInfo;

	// info used to bill a client for search/result
	private ClientInfo clientInfo;
	// information regarding the current Peer
	// this is ditributed to the Server and other peers and used in connecting
	// to this Peer
	private PeerInfo peerInfo;

	private UUID peerID;

	public PeerManager() {
		super();
		peerInfo = ConfigurationManagement.loadPeerInfo();
		clientInfo = ConfigurationManagement.loadClientInfo();
		peerID = generatePeerID();
	}

	private UUID generatePeerID() {

		return UUID.nameUUIDFromBytes((peerInfo.getIp() + "_" + peerInfo.getPort()).getBytes());
	}

	public UUID getPeerID() {
		return peerID;
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

	public void setPeerID(UUID peerID) {
		this.peerID = peerID;
	}

	public PeerInfo getsuperPeerInfo() {
		return superPeerInfo;
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

	public FingerprintExtractorAndManager getFingerprintExtractorAndManager() {
		return fingerprintExtractorAndManager;
	}

	public void setFingerprintExtractorAndManager(FingerprintExtractorAndManager fingerprintExtractorAndManager) {
		this.fingerprintExtractorAndManager = fingerprintExtractorAndManager;
	}

	/**
	 * takes the request and forwards it to the MatchingManager. It is used in
	 * case a search is forwarded to this peer from another PeerManager
	 */
	@Override
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

	@Override
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint) {
		// TODO Auto-generated method stub
		if (this.peerID.equals(this.superPeerInfo.getPeerID())) {
			// broadcast to all other peers in ring the search request
		} else {
			// just ignore request
		}
	}

	@Override
	public void distributeFingerprints(Collection<Fingerprint> peerFingerprints) {
		if (serverCommunicationManager != null) {
			superPeerInfo = serverCommunicationManager.registerToServer(peerInfo, peerFingerprints);
		} else {
			Logger.getLogger(this.getClass()).log(Level.ERROR, "Peer Manager not instantiated properly. ServerCommunicationManager is null");
		}
	}

	@Override
	public void updateSuperPeerInfo(PeerInfo superPeerInfo) {
		this.superPeerInfo = superPeerInfo;
		// TODO: need to add here something to mark the super peer id as
		// changed.
		// if not changed in specific interval, means super peer does not send
		// its id anymore, thus its dead and leader election must take place
	}

	@Override
	public void performLeaderElection() {
		// test which Peer is alive. And the Peer with largest ID is compared to
		// this, and the largest ID wins

		// refresh ring
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
				e.printStackTrace();
			}
		}
		
		PeerInfo largestPeer = this.peerInfo;
		//get peer with largest ID from map
		for(PeerInfo info : peerRing.keySet()){
			if(largestPeer.getPeerID().compareTo(info.getPeerID())<0){
				largestPeer = info;
			}
		}
		
		//updating superPeer
		superPeerInfo = largestPeer;
	}

	/**
	 * Used to update the fingerprint information about the other peers in the
	 * ring
	 */
	@Override
	public void updateRingInformation(PeerFingerprintInformation fingerprintInformation) {
		peerRing.put(fingerprintInformation.getPeerInfo(), new ArrayList<Fingerprint>(fingerprintInformation.getFingerprints()));
	}
}
