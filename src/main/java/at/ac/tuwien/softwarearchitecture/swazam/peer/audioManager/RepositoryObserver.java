package at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.sound.sampled.AudioInputStream;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class RepositoryObserver{
	private RepositoryAccess repositoryAccess;
 
	public RepositoryObserver(RepositoryAccess access){
		repositoryAccess=access;
	}
	public void setObservedDirectory(String path){
		 File directory = new File(path);
		  Logger.getLogger(this.getClass()).log(Level.INFO,"Observed directory "+path);
	      FileAlterationObserver observer = new FileAlterationObserver(directory);
	      observer.addListener(new FileAlterationListener(){

			public void onDirectoryChange(File arg0) {
//				System.out.println(arg0.getName()+" was changed ");
				
				//TODO nothing for now
			}

			public void onDirectoryCreate(File arg0) {
				 Logger.getLogger(this.getClass()).log(Level.INFO,arg0.getName()+" was created ");
				
			for (Entry<AudioInputStream,String> entry:repositoryAccess.getAllFilesFromDirectory(arg0.getName()).entrySet()){
				repositoryAccess.addNewFoundFile(entry.getKey(), entry.getValue());
				try {
					entry.getKey().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
			}

			public void onDirectoryDelete(File arg0) {
				 Logger.getLogger(this.getClass()).log(Level.INFO,arg0.getName()+" was deleted ");
				
				for (Entry<AudioInputStream,String> entry:repositoryAccess.getAllFilesFromDirectory(arg0.getName()).entrySet())
					repositoryAccess.removeDeletedFile(entry.getValue());
				
			}

			public void onFileChange(File arg0) {
				 Logger.getLogger(this.getClass()).log(Level.INFO,arg0.getName()+" was changed ");
				
				// TODO Auto-generated method stub
				
			}

			public void onFileCreate(File arg0) {
				 Logger.getLogger(this.getClass()).log(Level.INFO,arg0.getName()+" was created ");
				AudioInputStream audioInputStream=repositoryAccess.getFileWithPath(arg0.getAbsolutePath());
				
				repositoryAccess.addNewFoundFile(audioInputStream,arg0.getName());
				
				
			}

			public void onFileDelete(File arg0) {
				 Logger.getLogger(this.getClass()).log(Level.INFO,arg0.getName()+" was deleted ");
				//AudioInputStream audioInputStream=repositoryAccess.getFileWithPath(arg0.getName());
				repositoryAccess.removeDeletedFile(arg0.getName());
			}

			public void onStart(FileAlterationObserver arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onStop(FileAlterationObserver arg0) {
				// TODO Auto-generated method stub
			}
	    	  
	      });
	      try {
			observer.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      observer.checkAndNotify();
	      FileAlterationMonitor monitor = new FileAlterationMonitor(20);
	      monitor.addObserver(observer);
	      try {
			monitor.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	     // monitor.stop();
	}
}
