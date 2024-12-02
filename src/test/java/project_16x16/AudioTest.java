package project_16x16;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;
import project_16x16.Audio.BGM;
import project_16x16.Audio.SFX;

@ExtendWith(MockitoExtension.class)
class AudioTest {

	@InjectMocks
	@Spy
    Audio audio;
	
	@Mock
	Minim minim;
	
	@Mock
	HashMap<SFX, AudioSample> SFX_MAP;
	
	@Mock
	HashMap<BGM, AudioPlayer> BGM_MAP;
	
	@Mock
	AudioPlayer player;
	
	@Mock
	AudioSample sample;
	
	@Mock
	SideScroller sideScroller;
	
	@Test
	void onAssignAppletShouldPopulateSFXMap() {
		doReturn(minim).when(audio).createMinim(sideScroller);
		when(minim.loadSample(any())).thenReturn(sample);
		
        audio.assignApplet(sideScroller);
			
		verify(SFX_MAP, atLeastOnce()).put(any(), any());
	}

	@Test
	void onAssignAppletShouldPopulateBGMMap() {
		doReturn(minim).when(audio).createMinim(sideScroller);
		when(minim.loadFile(any())).thenReturn(player);
		
        audio.assignApplet(sideScroller);
			
		verify(BGM_MAP, atLeastOnce()).put(any(), any());
	}

	@Test
	void onAssignAppletShouldNotPopulateSFXMap() {
		try (MockedConstruction<Minim> minim = mockConstruction(Minim.class)) {
			try (MockedStatic<SFX> sfx = mockStatic(SFX.class)) {
	    		sfx.when(() -> SFX.values()).thenReturn(new SFX[0]);
	    		
	            audio.assignApplet(sideScroller);
	    		
	            verify(SFX_MAP, never()).put(any(), any());
			}
		}
	}
	
	@Test
	void onAssignAppletShouldNotPopulateBGMMap() {
		try (MockedConstruction<Minim> minim = mockConstruction(Minim.class)) {
			try (MockedStatic<BGM> bgm = mockStatic(BGM.class)) {
	    		bgm.when(() -> BGM.values()).thenReturn(new BGM[0]);
	    		
	            audio.assignApplet(sideScroller);
	    		
	            verify(BGM_MAP, never()).put(any(), any());
			}
		}
	}
	
	@Test
	void onPlayKnownSFXShouldPlay() {
		when(SFX_MAP.containsKey(any())).thenReturn(true);
		when(SFX_MAP.get(any())).thenReturn(sample);
		
        audio.play(SFX.ATTACK);
        
        verify(sample).trigger();
	}

	@Test
	void onPlayUnknownSFXShouldNotPlay() {
		when(SFX_MAP.containsKey(any())).thenReturn(false);
		
        audio.play(SFX.ATTACK);
        
        verify(SFX_MAP, never()).get(any());
	}

	@Test
	void onPlayKnownSFXShouldSetGainAndPlay() {
		when(SFX_MAP.containsKey(any())).thenReturn(true);
		when(SFX_MAP.get(any())).thenReturn(sample);
		
        audio.play(SFX.ATTACK, 0f);
        
        verify(sample).setGain(0f);
        verify(sample).trigger();
	}

	@Test
	void onPlayUnknownSFXShouldNotSetGainAndPlay() {
		when(SFX_MAP.containsKey(any())).thenReturn(false);
		
        audio.play(SFX.ATTACK, 0f);
        
        verify(SFX_MAP, never()).get(any());
	}
	
	@Test
	void onPlayNotAlreadyPlayingShouldTryNotPauseAllAndNotPlay() {
		AudioPlayer anotherPlayer = mock(AudioPlayer.class);
		
        when(BGM_MAP.get(any())).thenReturn(player);
        when(BGM_MAP.values()).thenReturn(List.of(anotherPlayer));
		when(BGM_MAP.containsKey(any())).thenReturn(false);

		when(player.isPlaying()).thenReturn(false);
		when(anotherPlayer.isPlaying()).thenReturn(false);
		
        audio.play(BGM.TEST0);
        
        verify(player, never()).setGain(0f);
        verify(player, never()).loop();
	}
	
	@Test
	void onPlayNotAlreadyPlayingShouldNotPauseAllAndNotPlay() {
        when(BGM_MAP.get(any())).thenReturn(player);
        when(BGM_MAP.values()).thenReturn(List.of());
		when(BGM_MAP.containsKey(any())).thenReturn(false);

		when(player.isPlaying()).thenReturn(false);
		
        audio.play(BGM.TEST0);
        
        verify(player, never()).setGain(0f);
        verify(player, never()).loop();
	}

	@Test
	void onPlayNotAlreadyPlayingShouldPauseAllAndPlay() {
		AudioPlayer anotherPlayer = mock(AudioPlayer.class);
		
        when(BGM_MAP.get(any())).thenReturn(player);
        when(BGM_MAP.values()).thenReturn(List.of(anotherPlayer));
		when(BGM_MAP.containsKey(any())).thenReturn(true);

		when(player.isPlaying()).thenReturn(false);
        when(anotherPlayer.isPlaying()).thenReturn(true);
		
        audio.play(BGM.TEST0);
        
        verify(anotherPlayer, atLeastOnce()).pause();
        verify(anotherPlayer, atLeastOnce()).rewind();
        
        verify(player).setGain(0f);
        verify(player).loop();
	}

	@Test
	void onPlayAlreadyPlayingShouldDoNothing() {
		when(BGM_MAP.get(any())).thenReturn(player);
        when(player.isPlaying()).thenReturn(true);
		
        audio.play(BGM.TEST0);
        
        verify(BGM_MAP, never()).values();
	}
	
	@Test
	void onPlayKnownSoundShouldPlay() {
		BGM sound = BGM.TEST0;

		when(BGM_MAP.containsKey(sound)).thenReturn(true);
    	when(BGM_MAP.get(sound)).thenReturn(player);
		
		audio.play(sound, 0f);
		
		verify(player, atLeastOnce()).setGain(0f);
	}
	
	@Test
	void onPlayUnknownSoundShouldNotPlay() {
		BGM sound = BGM.TEST0;
		
        when(BGM_MAP.containsKey(sound)).thenReturn(false);
		
		audio.play(sound, 0f);
		
		verify(BGM_MAP, never()).get(sound);
	}
	
	@Test
	void onSetGainBGMShouldSetGain() {
    	doNothing().when(player).setGain(0f);
    	
    	when(BGM_MAP.values()).thenReturn(List.of(player));
    	
    	audio.setGainBGM(0f);
    	
    	verify(player).setGain(0f);
	}

	@Test
	void onSetGainSFXShouldSetGain() {
    	doNothing().when(sample).setGain(0f);
    	
    	when(SFX_MAP.values()).thenReturn(List.of(sample));
    	
    	audio.setGainSFX(0f);
    	
    	verify(sample).setGain(0f);
	}
	
	@Test
	void onMuteShouldNotPlay() {
    	doNothing().when(sample).mute();
    	doNothing().when(player).mute();
    	
    	when(SFX_MAP.values()).thenReturn(List.of(sample));
    	when(BGM_MAP.values()).thenReturn(List.of(player));
    	
    	audio.mute();
		
		verify(sample).mute();
		verify(player).mute();
	}
	
	@Test
	void onUnMuteShouldPlay() {
    	doNothing().when(sample).unmute();
    	doNothing().when(player).unmute();
    	
    	when(SFX_MAP.values()).thenReturn(List.of(sample));
    	when(BGM_MAP.values()).thenReturn(List.of(player));
    	
    	audio.unMute();
		
		verify(sample).unmute();
		verify(player).unmute();
	}
	
	@Test
	void onExitMinimShouldStop() {
		doNothing().when(minim).stop();
		
		audio.exit();

		verify(minim).stop();
	}

}

