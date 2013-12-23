package at.ac.tuwien.softwarearchitecture.swazam.peer.audioManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class RepositoryAccess {
	public AudioInputStream getFileWithPath(String path){
		try {
			return  AudioSystem.getAudioInputStream(new File(path));
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public HashMap<AudioInputStream,String> getAllFilesFromDirectory(String directory){
		  File folder = new File(directory);
		  File[] listOfFiles = folder.listFiles(); 
		  HashMap<AudioInputStream,String> audioStreams = new HashMap<AudioInputStream,String>();
		  
		  for (int i = 0; i < listOfFiles.length; i++) 
		  {
		 
		   if (listOfFiles[i].isFile()) 
		   {
			   System.out.println("Reading input "+listOfFiles[i].getAbsolutePath());
			   try {
				 
				audioStreams.put(AudioSystem.getAudioInputStream(listOfFiles[i]),listOfFiles[i].getName());
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(UnsupportedAudioFileException audioFileException){
				//audioFileException.printStackTrace();
			}
		      }else{
		    	  if (listOfFiles[i].isDirectory()){
		    		  audioStreams.putAll(getAllFilesFromDirectory(listOfFiles[i].getAbsolutePath()));
		    	  }
		      }
		   try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  }
		  return audioStreams;
	}
}
