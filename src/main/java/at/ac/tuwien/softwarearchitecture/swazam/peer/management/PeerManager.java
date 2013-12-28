package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;

public class PeerManager {
	// holds the peer ring information, i.e. the rest of the peers in the ring.
	private List<PeerInfo> peerRing;

	{
		peerRing = new ArrayList<PeerInfo>();
	}

	private UUID peerID;

	private UUID superPeerID;

	// info used to signal the server when a new SuperPeer is elected
	private ServerInfo serverInfo;

	// info used to bill a client for search/result
	private ClientInfo clientInfo;

	// information regarding the current Peer
	private String ip;

	private int port;

	public PeerManager(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
		generatePeerID();
	}

	public PeerManager(UUID superPeerID, ServerInfo serverInfo, ClientInfo clientInfo, String ip, int port) {
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

	public List<PeerInfo> getPeerRing() {
		return peerRing;
	}

	public void setPeerRing(List<PeerInfo> peerRing) {
		this.peerRing = peerRing;
	}

	public void addPeers(List<PeerInfo> peerRing) {
		this.peerRing.addAll(peerRing);
	}

	public void addPeer(PeerInfo peer) {
		this.peerRing.add(peer);
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
}
