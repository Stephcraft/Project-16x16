package ParticleSystem.events;

import ParticleSystem.Particle;
import ParticleSystem.ParticleSystem;

public class ParticleNoLoopController implements ParticleEventListener {

	ParticleSystem particleSystem;
	int spawnAmount;
	int totalSpawned;
	
	public ParticleNoLoopController(ParticleSystem particleSystem, int spawnAmount) {
		this.particleSystem = particleSystem;
		this.spawnAmount = spawnAmount;
		totalSpawned = 0;
	}
	
	public void reset() {
		particleSystem.spawn = true;
		totalSpawned = 0;
	}
	
	@Override
	public void onParticleSpawnEvent(Particle particle) {
		totalSpawned++;
		if (totalSpawned >= spawnAmount)
			disableSpawn();
	}

	private void disableSpawn() {
		particleSystem.spawn = false;
	}
}
