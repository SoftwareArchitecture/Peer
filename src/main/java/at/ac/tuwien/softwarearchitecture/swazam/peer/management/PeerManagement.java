package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;

public class PeerManagement implements IPeerManagement{
	// holds the peer ring information, i.e. the rest of the peers in the ring.
	private Map<PeerInfo, List<Fingerprint>> peerRing;

	{
		peerRing = new HashMap<PeerInfo, List<Fingerprint>>();
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

	public String getIp() {
		return ip;
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
	public void initiateFingerprintSearch(IMatchingManager matchingManager, Fingerprint fingerprint) {
		//search fingerprint based on fingerprint table
		
		//search and then notify matchingManager
	}

	@Override
	public void distributeFingerprintsToServer() {
		// TODO Auto-generated method stub
		
	}
}
