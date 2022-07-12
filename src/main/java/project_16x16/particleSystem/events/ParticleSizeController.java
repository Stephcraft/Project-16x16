package project_16x16.particleSystem.events;

import project_16x16.particleSystem.Particle;

/**
 * Particle Size Controller
 * <p>
 * Change particle size over time.
 *
 * @author petturtle
 */
public class ParticleSizeController implements ParticleEventListener {

	private float startSize;
	private float endSize;

	/**
	 * @param startSize particle start size
	 * @param endSize   particle end size
	 */
	public ParticleSizeController(float startSize, float endSize) {
		this.startSize = startSize;
		this.endSize = endSize;
	}

	@Override
	public void onParticleSpawnEvent(Particle particle) {
		particle.useCustomeSize = true;
	};

	@Override
	public void onParticleRunEvent(Particle particle) {
		float size = particle.lifespan / particle.maxLifespan * (startSize - endSize) + endSize;
		particle.size = size;
	}

	@Override
	public ParticleEventListener copy() {
		return new ParticleSizeController(startSize, endSize);
	}
}
