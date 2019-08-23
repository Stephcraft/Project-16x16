package ParticleSystem.Modifier;

import ParticleSystem.Particle;
import ParticleSystem.ParticleSystem;

public class ParticleNoLoopModifier implements ParticleModifier {

	ParticleSystem particleSystem;
	int spawnAmount;
	int totalSpawned;
	
	public ParticleNoLoopModifier(ParticleSystem particleSystem, int spawnAmount) {
		this.particleSystem = particleSystem;
		this.spawnAmount = spawnAmount;
		totalSpawned = 0;
	}
	
	public void reset() {
		particleSystem.spawn = true;
		totalSpawned = 0;
	}
	
	@Override
	public void update(Particle particle) {
	}
	
	@Override
	public void onSpawn(Particle particle) {
		totalSpawned++;
		if (totalSpawned >= spawnAmount)
			disableSpawn();
	}

	private void disableSpawn() {
		particleSystem.spawn = false;
	}
}
