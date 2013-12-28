package at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import ac.at.tuwien.infosys.swa.audio.FingerprintSystem;
import at.ac.tuwien.softwarearchitecture.swazam.peer.api.impl.SocketAPI;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.RepositoryAccess;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.RepositoryObserver;

public class MusicFilesEvaluation {
	private FingerprintSystem fingerprintSystem = new FingerprintSystem(10);
	private HashMap<Fingerprint, String> knownFingerprints = new HashMap<Fingerprint, String>();

	public MusicFilesEvaluation(String repository) {
		RepositoryObserver repositoryObserver = new RepositoryObserver(this);
		repositoryObserver.setObservedDirectory(repository);
		readCurrentRepository(repository);
	}

	public String printAllFingerprints() {
		String fingerprints = "";
		for (Entry<Fingerprint, String> entry : knownFingerprints.entrySet()) {
			fingerprints += " " + entry.getValue() + " fingerprint= " + entry.getKey() + " \n";
		}
		return fingerprints;
	}

	public void removeFingerprint(Fingerprint fingerprint) {
		knownFingerprints.remove(fingerprint);
	}

	public void removeFingerprint(String fingerprintName) {
		Fingerprint toDelete = null;
		for (Entry<Fingerprint, String> f : knownFingerprints.entrySet()) {
			if (f.getValue().equalsIgnoreCase(fingerprintName)) {
				toDelete = f.getKey();
			}
		}
		if (toDelete != null)
			knownFingerprints.remove(toDelete);
	}

	public void addFingerprint(AudioInputStream audioInputStream, String fileName) {
		Fingerprint fingerprint = null;
		try {
			fingerprint = fingerprintSystem.fingerprint(audioInputStream);
		} catch (IOException e) {

			Logger.getLogger(this.getClass()).log(Level.ERROR, e);
		}
		knownFingerprints.put(fingerprint, fileName);
	}

	public void readCurrentRepository(String repo) {
		RepositoryAccess repositoryAccess = new RepositoryAccess();
		HashMap<AudioInputStream, String> audios = repositoryAccess.getAllFilesFromDirectory(repo);
		for (Entry<AudioInputStream, String> audioInputStream : audios.entrySet()) {
			try {
				//System.out.println("Trying to get fingerprint for " + audioInputStream.getValue());
				knownFingerprints.put(FingerprintSystem.fingerprint(audioInputStream.getKey()), audioInputStream.getValue());
				audioInputStream.getKey().close();
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).log(Level.ERROR, e);
			}
		}
	}
	public String evaluateFingerprint (Fingerprint fingerprint){
		if (knownFingerprints.containsKey(fingerprint))
			return knownFingerprints.get(fingerprint);
		return "";
	}
}
