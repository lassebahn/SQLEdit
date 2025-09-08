package com.lasse.service;

import java.net.URL;

import javafx.scene.media.AudioClip;

public class Sound {

	
	public void play (){
		URL resource = getClass().getResource("Media/Alarm01.wav");
	    AudioClip clip = new AudioClip(resource.toString());
		clip.play();
	}
	
	public static void main(String args[]){
		Sound s = new Sound();
		s.play();
	}

}
