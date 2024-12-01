package project_16x16.factory;

import project_16x16.Audio;

public class AudioFactory {

	private static Audio audio;
	
    public static Audio getInstance() {
    	if (audio == null) {
    		audio = new Audio();
    	}
    	
        return audio;
    }
    
    public static Audio createInstance() {
    	return new Audio();
    }
	
}
