package de.gurkenlabs.litiengine.graphics.emitters.particles;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.gurkenlabs.litiengine.Game;

public abstract class ShapeParticle extends Particle {

  public ShapeParticle(float width, float height) {
    super(width, height);
  }

  protected abstract Shape getShape(final Point2D emitterOrigin);

  @Override
  public Rectangle2D getBoundingBox(Point2D origin) {
    return this.getShape(origin).getBounds2D();
  }

  @Override
  public void render(final Graphics2D g, final Point2D emitterOrigin) {
    g.setColor(this.getColor());
    if (this.isOutlineOnly() || this instanceof LineParticle) {
      Game.graphics().renderOutline(g, this.getShape(emitterOrigin), new BasicStroke(1.0f / Game.graphics().getBaseRenderScale()), this.isAntiAliased());
    } else {
      Game.graphics().renderShape(g, this.getShape(emitterOrigin), this.isAntiAliased());
    }
  }
}
