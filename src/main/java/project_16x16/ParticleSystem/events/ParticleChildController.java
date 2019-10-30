package project_16x16.ParticleSystem.events;

import java.util.ArrayList;

import project_16x16.ParticleSystem.Particle;
import project_16x16.ParticleSystem.ParticleSystem;

/**
 * Particle Child Controller
 * <p>
 * A experimental Controller (could change)
 * Spawns and runs particle system when particle lives for given time or dies.
 *
 * @author petturtle
 */
public class ParticleChildController implements ParticleEventListener {
	
	private boolean hasDelay;
	private boolean spawnOnDeath;
	private int delay;
	
	private ParticleSystem copySystem;
	private ArrayList<ParticleSystem> particleSystems;
	
	/**
	 * @param particleSystem particle system need to have a no loop controller
	 * @param delay how many frames till active
	 */
	public ParticleChildController(ParticleSystem particleSystem, int delay) {
		this.delay = delay;
		hasDelay = true;
		spawnOnDeath = false;
		copySystem = particleSystem;
		particleSystems = new ArrayList<ParticleSystem>();
	}
	
	/**
	 * @param particleSystem particle system need to have a no loop controller
	 * @param delay how many frames till active
	 * @param spawnOnDeath active when particle dies
	 */
	public ParticleChildController(ParticleSystem particleSystem, int delay, boolean spawnOnDeath) {
		this.delay = delay;
		this.spawnOnDeath = spawnOnDeath;
		hasDelay = true;
		copySystem = particleSystem;
		particleSystems = new ArrayList<ParticleSystem>();
	}
	
	/**
	 * @param particleSystem particle system need to have a no loop controller
	 * @param spawnOnDeath active when particle dies
	 */
	public ParticleChildController(ParticleSystem particleSystem, boolean spawnOnDeath) {
		this.spawnOnDeath = spawnOnDeath;
		hasDelay = false;
		delay = 0;
		copySystem = particleSystem;
		particleSystems = new ArrayList<ParticleSystem>();
	}
	
	@Override
	public void onUpdateEvent() {
		ArrayList<ParticleSystem> temp = new ArrayList<ParticleSystem>();
		for(ParticleSystem particleSystem : particleSystems) {
			particleSystem.run();
			if (!particleSystem.spawn && !particleSystem.particles.hasActiveParticles())
				temp.add(particleSystem);
		}
		particleSystems.removeAll(temp);
	}
	
	@Override
	public void onParticleRunEvent(Particle particle) {
		if (hasDelay && particle.frameCount == delay)
			newChild(particle);
	}
	
	@Override
	public void onParticleDeathEvent(Particle particle) {
		if (spawnOnDeath)
			newChild(particle);
	}
	
	@Override
	public ParticleEventListener copy() {
		return new ParticleChildController(copySystem.copy(), delay);
	}
	
	private void newChild(Particle p) {
		ParticleSystem newSystem = copySystem.copy();
		newSystem.emission.setPosition(p.position);
		particleSystems.add(newSystem);
	}
}
