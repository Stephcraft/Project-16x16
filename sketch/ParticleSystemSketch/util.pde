
public PImage resizeImage(PImage img, float sx, float sy) {
  PGraphics pg = createGraphics((int)(img.width*sx), (int)(img.height*sy));
  
  pg.beginDraw();
    pg.clear();
    pg.scale( sx, sy );
    pg.image(img,0,0);
  pg.endDraw();
  
  return pg;
}

public PImage rotateImage(PImage img, float angle) {
  PGraphics pg = createGraphics((int)(img.width*1.5), (int)(img.height*1.5));
  
  pg.beginDraw();
    pg.clear();
    pg.imageMode(CENTER);
    pg.translate(pg.width/2, pg.height/2);
    pg.rotate( angle );
    pg.image(img,0,0);
  pg.endDraw();
  
  return pg;
}

public PGraphics pg(PImage img, float scl) {
    PGraphics pg = createGraphics((int)(img.width*scl), (int)(img.height*scl));
    
    pg.noSmooth();
    pg.beginDraw();
      pg.clear();
      pg.image(img, 0, 0, (int)(img.width*scl), (int)(img.height*scl));
    pg.endDraw();
    
    return pg;
  }