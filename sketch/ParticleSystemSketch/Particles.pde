
public class ParticleSystem {
  private ArrayList<Particle> p;
  
  public PVector pos;
  
  public int animationRate;
  public int spawnRate;
  public int spawnPerRate;
  public PVector dispertion;
  
  public PApplet applet;
  
  public PGraphics img;
  private ArrayList<PGraphics>[] graphics = new ArrayList[10];
  
  private boolean spawn;
  private boolean pause;
  
  private int limit;
  
  ParticleSystem(PApplet a, PGraphics image, int rate_animation, int rate_spawn, int spr, PVector d) {
    applet = a;
    
    p = new ArrayList<Particle>();
    
    img = image;
    animationRate = rate_animation;
    spawnRate = rate_spawn;
    spawnPerRate = spr;
    dispertion = d;
    spawn = false;
    
    build(10, 0.2, 0.02, 0.04);
    
    pos = new PVector();
  }
  
  public void show() {
    if(spawnRate <= 0) { spawnRate = 1; }
    if(spawnPerRate < 0) { spawnRate = 0; }
    
    for(int i=0; i<p.size(); i++) {
      if(!pause) {
        p.get(i).update();
      }
      p.get(i).display();
      
      if(p.get(i).dead) {
        p.remove(i);
      }
      else {
        if(limit > 0) {
          if(i > limit) {
            p.remove(i);
          }
        }
      }
    }
    
    if(pause) { return; }
    if(frameCount % spawnRate == 0) {
      if(spawn) {
          for(int i=0; i<spawnPerRate; i++) {
            p.add(  new Particle(
                      applet, graphics[ (int)random(0,8) ], 
                      animationRate, 
                      new PVector(pos.x + random(-10,10), pos.y + random(-10,10)),
                      new PVector(random(-dispertion.x,dispertion.x),random(-dispertion.y,dispertion.y))
                    )
            );
          }
      }
      
      //Collections.shuffle( p );
    }
  }
  
  public void spawn(int num) {
    for(int i=0; i<num; i++) {
      p.add(  
        new Particle(
          applet, graphics[ (int)random(0,8) ], 
          animationRate, 
          new PVector(pos.x + random(-10,10), pos.y + random(-10,10)),
          new PVector(random(-dispertion.x,dispertion.x),random(-dispertion.y,dispertion.y))
        )
      );
    }
  }
  
  public void build(int precision, float startScale, float scaleStepMin, float scaleStepMax) {
    for(int i=0; i<8; i++) {
      graphics[i] = new ArrayList<PGraphics>();
      
      float scale = startScale;
      float angle = radians(11);
      for(int j=0; j<precision; j++) {
        angle += random( radians(11) );
        
        graphics[i].add(
          pg(
            resizeImage(
              rotateImage(img, angle),
              scale + random(scaleStepMin, scaleStepMax), scale + random(scaleStepMin, scaleStepMax)
            ),
          4)
        );
        scale += random(scaleStepMin, scaleStepMax);
      }
    }
  }
  
  public void setLimit(int l) {
    limit = l;
  }
  
  public void noLomit() {
    limit = -1;
  }
  
  public int getLimit() {
    return limit;
  }
  
  public int size() {
    return p.size();
  }
  
  public void clear() {
    p.clear();
  }
  
  public void start() {
    spawn = true;
  }
  
  public void stop() {
    spawn = false;
  }
  
  public void pause() {
    pause = true;
  }
  
  public void unpause() {
    pause = false;
  }
  
  public boolean paused() {
    return pause;
  }
}

public class Particle {
  public PVector pos;
  public PVector velocity;
  
  public ArrayList<PGraphics> graphics;
  
  public int rate;
  
  PApplet applet;
  
  public float angle;
  public int scale;
  
  public boolean dead;
  
  Particle(PApplet a, ArrayList<PGraphics> img, int rate_) {
    graphics = img;
    
    if(rate_ <= 0) { rate_ = 1; }
    
    rate = rate_;
    
    applet = a;
    
    angle = radians( random(0, 360) );
    scale = img.size()-1;
    
    pos = new PVector();
    velocity = new PVector();
  }
  
  Particle(PApplet a, ArrayList<PGraphics> img, int rate_, PVector position) {
    this(a, img, rate_);
    
    pos = position;
  }
  
  Particle(PApplet a, ArrayList<PGraphics> img, int rate_, PVector position, PVector v) {
    this(a, img, rate_, position);
    
    velocity = v;
  }
  
  public void update() {
    
    pos.x += velocity.x;
    pos.y += velocity.y;
    
    if(frameCount % rate == 0) {
      
      scale --;
      angle += radians( random(11) );
      
      if(scale <= 0) {
        dead = true;
      }
    }
  }
  
  public void display() {
    if(dead) { return; }
    
    imageMode(CENTER);
    
    int pixelOffsetX = 0;
    int pixelOffsetY = 0;
    
    if(graphics.get( scale ).height/4 % 2 != 0) {
      pixelOffsetY = 2;
    }
    if(graphics.get( scale ).width/4 % 2 != 0) {
      pixelOffsetX = 2;
    }
    
    applet.image( graphics.get( scale ), round( (pos.x) /4)*4+pixelOffsetX, round( (pos.y) /4)*4+pixelOffsetY);
    
    //println( round( (pos.x) /4)*4+pixelOffsetX, round( (pos.y) /4)*4-pixelOffsetY );
      
      /*
      pg(
        resizeImage(
          rotateImage(img, angle),
          scale, scale
        ),
      4),
      */
  }
}