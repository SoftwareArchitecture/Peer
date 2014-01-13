package at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import ac.at.tuwien.infosys.swa.audio.FingerprintSystem;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManager;

public interface IFingerprintExtractorAndManager {
	public IPeerManager getPeerManager() ;

	public void setPeerManager(IPeerManager peerManager);


	public String printAllFingerprints() ;
	public void removeFingerprint(Fingerprint fingerprint) ;

	public void removeFingerprint(String fingerprintName) ;

	public void addFingerprint(AudioInputStream audioInputStream, String fileName) ;

	public void readCurrentRepository(String repo) ;

	public MusicFileInfo evaluateFingerprint(Fingerprint fingerprint) ;
	
	public Collection<Fingerprint> getKnownFingerprints();
}
