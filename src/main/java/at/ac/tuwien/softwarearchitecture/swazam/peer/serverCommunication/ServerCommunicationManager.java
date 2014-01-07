package at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication;

import java.util.Collection;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;

public class ServerCommunicationManager implements IServerCommunicationManager{

	//to execute file searches asynchronously
	private IMatchingManager matchingManager;
	
	public ServerCommunicationManager() {
		super();
	}

	public IMatchingManager getMatchingManager() {
		return matchingManager;
	}

	public void setMatchingManager(IMatchingManager matchingManager) {
		this.matchingManager = matchingManager;
	}

	@Override
	public PeerInfo registerToServer(PeerInfo peerInfo, Collection<Fingerprint> perFingerprints) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprintToSearch) {
		matchingManager.matchFile(clientInfo, fingerprintToSearch);
	}

	@Override
	public void notifyAboutSearchResult(ClientInfo clientInfo, MusicFileInfo result) {
		// TODO Auto-generated method stub
		
		Thread  thread = new Thread(){
			public void run(){
				
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		
	}

}
