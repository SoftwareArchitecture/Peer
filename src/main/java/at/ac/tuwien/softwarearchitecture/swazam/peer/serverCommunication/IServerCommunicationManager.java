package at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication;

import java.util.List;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;

public interface IServerCommunicationManager {
	public void registerToServer(PeerInfo peerInfo, List<Fingerprint> perFingerprints);
	
	//method for broadcasting requests to other peers in sub-peer ring
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprintToSearch);
    
	public void notifyAboutSearchResult(ClientInfo clientInfo, MusicFileInfo result);
	
}
