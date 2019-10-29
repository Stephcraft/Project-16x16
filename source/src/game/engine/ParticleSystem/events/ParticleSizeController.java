package game.engine.ParticleSystem.events;

import game.engine.ParticleSystem.Particle;

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
	public void onParticleSpawnEvent(Particle p) {
		p.useCustomeSize = true;
	};
	
	@Override
	public void onParticleRunEvent(Particle p) {	
		float size = p.lifespan/p.maxLifespan*(startSize-endSize)+endSize;
		p.size = size;
	}
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleSizeController(startSize, endSize);
	}
}
