import java.util.Collections;

ParticleSystem particles;
ParticleSystem explode;

PImage img;
PFont font;

PImage box;

boolean step;

public void setup() {
  size(1000, 900);
  
  noSmooth();
  
  img = loadImage("data/img.png");
  
  box = loadImage("data/box.png");
  //img = rotateImage(img, radians(10)); 
  //img = resizeImage(img, 1, 1);
  
  font = loadFont("data/font-pixel-48.vlw");
  
  textFont( font );
  
  explode = new ParticleSystem(this, pg(img,1), 2, 1, 0, new PVector(2,2));
  //explode.build(30, 0.1, 0.005, 0.02);
  
  particles = new ParticleSystem(this, pg(img,1), 2, 1, 2, new PVector(0.1,0.1));
  //particles.build(30, 0.1, 0.01, 0.02);
  particles.setLimit(40);
}

public void draw() {
  background(29,33,45);
  
  textSize(20);
  text("16x16 Prototype Sketch "+
       "\n\nPARTICLES : "+particles.size()+
       "\n\nAnimation Rate : "+particles.animationRate+
       "\n\nSpawn Rate : "+particles.spawnRate+
       "\n\nSpawn Per Rate : "+particles.spawnPerRate+
       "\n\nDispertion : "+particles.dispertion.x+
       "\n\nFrameRate : "+(int)frameRate+
       "", 20, 20);
  
  //particles.pos = new PVector(mouseX,mouseY);
  if(step) { particles.unpause(); }
  
  particles.show();
  
  if(step) { particles.pause(); step = false; }
  
  explode.pos = new PVector(mouseX,mouseY);
  explode.show();
  
  //testing pixel Perfetct rotation
  /*
  image(
    pg(rotateImage(
         box,
         atan2(mouseY - height/2, mouseX - width/2)
       ),
    4
    ),
    width/2, height/2
  );
  */
  
  if(keyPressed) {
    float speed = 10;
  
    if(keyCode == UP) {
      particles.pos.y -= speed;
    }
    if(keyCode == DOWN) {
      particles.pos.y += speed;
    }
    if(keyCode == LEFT) {
      particles.pos.x -= speed;
    }
    if(keyCode == RIGHT) {
      particles.pos.x += speed;
    }
  }
  
  surface.setTitle("" + (int)frameRate);
}


//Draw Particles on click
public void mousePressed() {
  particles.start();
  explode.spawn(20);
}

public void mouseReleased() {
  //particles.stop();
}


//Cool controls ingame
public void keyPressed() {
  if(key == 'p') {
    if(particles.paused()) {
      particles.unpause();
    }
    else {
      particles.pause();
    }
  }
  
  if(key == '1') {
    particles.animationRate ++;
  }
  if(key == '!') {
    particles.animationRate --;
  }
  
  if(key == '2') {
    particles.spawnRate ++;
  }
  if(key == '@') {
    particles.spawnRate --;
  }
  
  if(key == '3') {
    particles.spawnPerRate ++;
  }
  if(key == '#') {
    particles.spawnPerRate --;
  }
  
  if(key == '4') {
    particles.dispertion.x ++;
    particles.dispertion.y ++;
  }
  if(key == '$') {
    particles.dispertion.x --;
    particles.dispertion.y --;
  }
  
  if(key == 's') {
    step = true;
  }
}