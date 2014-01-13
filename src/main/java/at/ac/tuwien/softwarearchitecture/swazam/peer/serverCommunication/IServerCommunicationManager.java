package at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication;

import java.util.Collection;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;


public interface IServerCommunicationManager {
	/**
	 * 
	 * @param peerInfo
	 * @param perFingerprints
	 * @return the superPeerInfo
	 */
	public PeerInfo registerToServer(PeerInfo peerInfo);
	
	//method for broadcasting requests to other peers in sub-peer ring
	public void matchFile(ClientInfo clientInfo, Fingerprint fingerprintToSearch);
    
	public void notifyAboutSearchResult(ClientInfo clientInfo, PeerInfo peerInfo, MusicFileInfo result);
	
}
