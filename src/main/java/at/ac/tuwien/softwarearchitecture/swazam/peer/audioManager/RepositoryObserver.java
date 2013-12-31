package at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager;

import java.io.File;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;
import javax.tools.FileObject;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.FingerprintExtractorAndManager;


public class RepositoryObserver{
	private FingerprintExtractorAndManager musicFilesEvaluation;
	private RepositoryAccess repositoryAccess = new RepositoryAccess();
	public RepositoryObserver(FingerprintExtractorAndManager music){
		musicFilesEvaluation=music;
	}
	public void setObservedDirectory(String path){
		 File directory = new File(path);
	      FileAlterationObserver observer = new FileAlterationObserver(directory);
	      observer.addListener(new FileAlterationListener(){

			@Override
			public void onDirectoryChange(File arg0) {
				//TODO nothing for now
			}

			@Override
			public void onDirectoryCreate(File arg0) {
			for (Entry<AudioInputStream,String> entry:repositoryAccess.getAllFilesFromDirectory(arg0.getAbsolutePath()).entrySet())
				musicFilesEvaluation.addFingerprint(entry.getKey(), entry.getValue());
				
			}

			@Override
			public void onDirectoryDelete(File arg0) {
				for (Entry<AudioInputStream,String> entry:repositoryAccess.getAllFilesFromDirectory(arg0.getAbsolutePath()).entrySet())
					musicFilesEvaluation.removeFingerprint(entry.getValue());
				
			}

			@Override
			public void onFileChange(File arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFileCreate(File arg0) {
				AudioInputStream audioInputStream=repositoryAccess.getFileWithPath(arg0.getAbsolutePath());
				musicFilesEvaluation.addFingerprint(audioInputStream,arg0.getAbsolutePath());
				
			}

			@Override
			public void onFileDelete(File arg0) {
				AudioInputStream audioInputStream=repositoryAccess.getFileWithPath(arg0.getAbsolutePath());
				musicFilesEvaluation.removeFingerprint(arg0.getAbsolutePath());
			}

			@Override
			public void onStart(FileAlterationObserver arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStop(FileAlterationObserver arg0) {
				// TODO Auto-generated method stub
			}
	    	  
	      });
	}
}
