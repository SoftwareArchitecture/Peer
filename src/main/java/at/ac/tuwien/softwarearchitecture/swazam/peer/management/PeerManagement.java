package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;

public class PeerManagement implements IPeerManagement {
	// holds the peer ring information, i.e. the rest of the peers in the ring.
	// Replaced with List<IDs> for privacy, to avoid
	// a super peer or other peers knowing the content of all other peers
	// private Map<PeerInfo, List<Fingerprint>> peerRing;

	// {
	// peerRing = new HashMap<PeerInfo, List<Fingerprint>>();
	// }

	/**
	 * In the case a SubPeer receives a request from the SuperPeer, the request
	 * is
	 */
	private IMatchingManager matchingManager;

	private List<PeerInfo> peerRing;
	{
		peerRing = new ArrayList<PeerInfo>();
	}

	private UUID superPeerID;

	// info used to signal the server when a new SuperPeer is elected
	private ServerInfo serverInfo;

	// info used to bill a client for search/result
	private ClientInfo clientInfo;

	// information regarding the current Peer
	private String ip;

	private int port;

	private UUID peerID;

	public PeerManagement(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		generatePeerID();
	}

	public PeerManagement(UUID superPeerID, ServerInfo serverInfo, ClientInfo clientInfo, String ip, int port) {
		super();
		this.superPeerID = superPeerID;
		this.serverInfo = serverInfo;
		this.clientInfo = clientInfo;
		this.ip = ip;
		this.port = port;
		generatePeerID();
	}

	private void generatePeerID() {
		peerID = UUID.fromString(ip + "_" + port);
	}

	public UUID getPeerID() {
		return peerID;
	}

	// public Map<PeerInfo, List<Fingerprint>> getPeerRing() {
	// return peerRing;
	// }
	//
	// public void setPeerRing(Map<PeerInfo, List<Fingerprint>> peerRing) {
	// this.peerRing = peerRing;
	// }
	//
	// public void addPeers(Map<PeerInfo, List<Fingerprint>> peerRing) {
	// this.peerRing.putAll(peerRing);
	// }
	//
	// public void addPeer(PeerInfo peer, List<Fingerprint> fingerprints) {
	// this.peerRing.put(peer, fingerprints);
	// }

	public IMatchingManager getMatchingManager() {
		return matchingManager;
	}

	public void setMatchingManager(IMatchingManager matchingManager) {
		this.matchingManager = matchingManager;
	}

	public String getIp() {
		return ip;
	}

	public List<PeerInfo> getPeerRing() {
		return peerRing;
	}

	public void setPeerRing(List<PeerInfo> peerRing) {
		this.peerRing = peerRing;
	}

	public void addPeerRing(List<PeerInfo> peerRing) {
		this.peerRing.addAll(peerRing);
	}

	public void addPeerInfo(PeerInfo peerInfo) {
		this.peerRing.add(peerInfo);
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setPeerID(UUID peerID) {
		this.peerID = peerID;
	}

	public UUID getSuperPeerID() {
		return superPeerID;
	}

	public void setSuperPeerID(UUID superPeerID) {
		this.superPeerID = superPeerID;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	@Override
	public void distributeFingerprintsToServer() {
		// TODO Auto-generated method stub

	}

	/**
	 * takes the request and forwards it to the MatchingManager. It is used in
	 * case a search is forwarded to this peer from another PeerManager
	 */

	@Override
	public void searchFingerprint(ClientInfo client, Fingerprint fingerprint) {
		if (matchingManager != null) {
			matchingManager.matchFile(client, fingerprint);
		}
	}

	@Override
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint) {
		// TODO Auto-generated method stub
		if (this.peerID.equals(this.superPeerID)) {
			// broadcast to all other peers in ring the search request
		} else {
			// just ignore request
		}
	}
}
