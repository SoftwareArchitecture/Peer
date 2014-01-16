package at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager;

import java.awt.datatransfer.SystemFlavorMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import ac.at.tuwien.infosys.swa.audio.FingerprintSystem;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.IAudioManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.RepositoryAccess;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.RepositoryObserver;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManager;

public class FingerprintExtractorAndManager implements IFingerprintExtractorAndManager {

    private FingerprintSystem fingerprintSystem = new FingerprintSystem(10);
    private HashMap<Fingerprint, String> knownFingerprints = new HashMap<Fingerprint, String>();
    private IPeerManager peerManager;
    private IAudioManager audioManager;

    public FingerprintExtractorAndManager(String repository, IPeerManager peerManager) {
        this.peerManager = peerManager;
        audioManager = new RepositoryAccess(this, repository);

        readCurrentRepository(repository);
        //peerManager.distributeFingerprints(knownFingerprints.keySet());
    }

    public IPeerManager getPeerManager() {
        return peerManager;
    }

    public void setPeerManager(IPeerManager peerManager) {
        this.peerManager = peerManager;
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
        if (toDelete != null) {
            knownFingerprints.remove(toDelete);
        }

        //peerManager.distributeFingerprints(knownFingerprints.keySet());
    }

    public Collection<Fingerprint> getKnownFingerprints() {
        return knownFingerprints.keySet();
    }

    public void addFingerprint(AudioInputStream audioInputStream, String fileName) {
        Fingerprint fingerprint = null;
        try {
            fingerprint = fingerprintSystem.fingerprint(audioInputStream);
        } catch (IOException e) {

            Logger.getLogger(this.getClass()).log(Level.ERROR, e);
        }
        knownFingerprints.put(fingerprint, fileName);
        //peerManager.distributeFingerprints(knownFingerprints.keySet());
    }

    public void readCurrentRepository(String repo) {

        HashMap<AudioInputStream, String> audios = audioManager.getAllFilesFromDirectory(repo);
        for (Entry<AudioInputStream, String> audioInputStream : audios.entrySet()) {
            try {
                // System.out.println("Trying to get fingerprint for " +
                // audioInputStream.getValue());
                knownFingerprints.put(FingerprintSystem.fingerprint(audioInputStream.getKey()), audioInputStream.getValue());
                audioInputStream.getKey().close();
            } catch (IOException e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, e);
            }
        }
    }

    public MusicFileInfo evaluateFingerprint(Fingerprint fingerprint) {
        MusicFileInfo fileInfo = new MusicFileInfo();
        for (Fingerprint f : knownFingerprints.keySet()) {
            if (f.match(fingerprint) == 0.0) {
                fileInfo.setDescription(knownFingerprints.get(f));
            }
        }
        return fileInfo;
    }
}
