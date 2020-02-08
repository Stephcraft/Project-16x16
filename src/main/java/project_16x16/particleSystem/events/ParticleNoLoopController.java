package project_16x16.particleSystem.events;

import project_16x16.particleSystem.Particle;
import project_16x16.particleSystem.ParticleSystem;

/**
 * Particle NoLoop Controller
 * <p>
 * Stops particleSystem after a number of particles spawns.
 *
 * @author petturtle
 */
public class ParticleNoLoopController implements ParticleEventListener {

	ParticleSystem particleSystem;
	int spawnAmount;
	int totalSpawned;
	
	/**
	 * Stops particleSystem after a number of particles spawns
	 *
	 * @param spawnAmount    stop particleSystem after number of spawns
	 */
	public ParticleNoLoopController(int spawnAmount) {
		this.spawnAmount = spawnAmount;
		totalSpawned = 0;
	}
	
	public void reset() {
		particleSystem.spawn = true;
		totalSpawned = 0;
	}
	
	@Override
	public void onCreateEvent(ParticleSystem particleSystem) {
		this.particleSystem = particleSystem;
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
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleNoLoopController(spawnAmount);
	}
}
