package at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;

public interface IAudioManager {
	public HashMap<AudioInputStream, String> getAllFilesFromDirectory(String directory);
}
