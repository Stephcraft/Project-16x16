package ParticleSystem.events;

import ParticleSystem.Particle;
import ParticleSystem.ParticleSystem;

public class ParticleChildController implements ParticleEventListener {
	
	private boolean hasDelay;
	private boolean spawnOnDeath;
	private int delay;
	
	public ParticleChildController(ParticleSystem particleSystem, int delay) {
		this.delay = delay;
		hasDelay = true;
		spawnOnDeath = false;
	}
	
	public ParticleChildController(ParticleSystem particleSystem, int delay, boolean spawnOnDeath) {
		this.delay = delay;
		this.spawnOnDeath = spawnOnDeath;
		hasDelay = true;
	}
	
	public ParticleChildController(ParticleSystem particleSystem, boolean spawnOnDeath) {
		this.spawnOnDeath = spawnOnDeath;
		hasDelay = false;
		delay = 0;
	}
	
	@Override
	public void onUpdateEvent() {
		
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {
		
	}
	
	@Override
	public void onParticleDeathEvent(Particle particle) {
		
	}
	
}
