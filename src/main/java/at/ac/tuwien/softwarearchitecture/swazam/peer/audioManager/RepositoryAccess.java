package at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.FingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.IFingerprintExtractorAndManager;

public class RepositoryAccess implements IAudioManager {

    private RepositoryObserver repositoryObserver;
    private IFingerprintExtractorAndManager fingerprintExtractorAndManager;

    public RepositoryAccess(IFingerprintExtractorAndManager fingerprintExtractorAndManager, String directoryPath) {
        repositoryObserver = new RepositoryObserver(this);
        this.fingerprintExtractorAndManager = fingerprintExtractorAndManager;
        repositoryObserver.setObservedDirectory(directoryPath);

    }

    public AudioInputStream getFileWithPath(String path) {
        try {
            return AudioSystem.getAudioInputStream(new File(path));
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, e);
            return null;
        }
    }

    public void addNewFoundFile(AudioInputStream audioInputStream, String fileName) {
        fingerprintExtractorAndManager.addFingerprint(audioInputStream, fileName);
    }

    public void removeDeletedFile(String fileName) {
        fingerprintExtractorAndManager.removeFingerprint(fileName);
    }

    public HashMap<AudioInputStream, String> getAllFilesFromDirectory(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        HashMap<AudioInputStream, String> audioStreams = new HashMap<AudioInputStream, String>();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {

                if (listOfFiles[i].isFile()) {
                    //System.out.println("Reading input " + listOfFiles[i].getName());
                    try {
                        audioStreams.put(AudioSystem.getAudioInputStream(listOfFiles[i]), listOfFiles[i].getName());
                    } catch (IOException e) {

                        Logger.getLogger(this.getClass()).log(Level.ERROR, e);
                    } catch (UnsupportedAudioFileException e) {
                        Logger.getLogger(this.getClass()).log(Level.ERROR, e);
                    }
                } else {
                    if (listOfFiles[i].isDirectory()) {
                        //System.out.println("REading from directory"+listOfFiles[i].getName());
                        audioStreams.putAll(getAllFilesFromDirectory(listOfFiles[i].getAbsolutePath()));
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Logger.getLogger(this.getClass()).log(Level.ERROR, e);
                }
            }
        }
        return audioStreams;
    }
}
