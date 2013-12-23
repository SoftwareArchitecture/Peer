package at.ac.tuwien.softwarearchitecture.swazam.peer;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

import ac.at.tuwien.infosys.swa.audio.*;
import at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager.RepositoryAccess;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.MusicFilesEvaluation;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
      FingerprintSystem fingerprintSystem = new FingerprintSystem(2);
      RepositoryAccess access=new RepositoryAccess();
      AudioInputStream file1=access.getFileWithPath("C:\\Users\\Georgiana\\Music\\Anomie Belle - American View [HD].mp3");
      Fingerprint fingerprint1=null;
      try {
    	  fingerprint1=fingerprintSystem.fingerprint(file1);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      AudioInputStream file2=access.getFileWithPath("C:\\Users\\Georgiana\\Music\\Anomie Belle - Down - YouTube [360p].mp3");
      
      Fingerprint fingerprint2=null;
      try {
    	  fingerprint2=fingerprintSystem.fingerprint(file2);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      System.out.println(fingerprint1.match(fingerprint2));
      
      
      MusicFilesEvaluation musicFilesEvaluation = new MusicFilesEvaluation("C:\\Users\\Georgiana\\Music");
      System.out.println("Fingerprints are "+musicFilesEvaluation.printAllFingerprints());
     }
}
