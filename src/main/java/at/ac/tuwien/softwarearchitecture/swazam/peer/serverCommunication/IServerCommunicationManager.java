package at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication;

import java.util.List;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;

public interface IServerCommunicationManager {
	public void registerToServer(PeerInfo peerInfo, List<Fingerprint> perFingerprints);
}
