package de.gurkenlabs.litiengine.sound;

import java.awt.geom.Point2D;
import java.util.function.Supplier;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import de.gurkenlabs.litiengine.Game;

/**
 * A <code>SoundPlayback</code> implementation for the playback of sound effects.
 */
public class SFXPlayback extends SoundPlayback {
  private Sound sound;
  private FloatControl panControl;
  private Supplier<Point2D> source;
  private VolumeControl distance;
  private boolean loop;

  SFXPlayback(Sound sound, Supplier<Point2D> source, boolean loop) throws LineUnavailableException {
    super(sound.getFormat());
    this.loop = loop;
    this.sound = sound;
    this.panControl = this.line.isControlSupported(FloatControl.Type.PAN) ? (FloatControl) this.line.getControl(FloatControl.Type.PAN) : null;
    this.source = source;
    this.distance = this.createVolumeControl();
  }

  @Override
  public void run() {
    do {
      if (this.play(this.sound)) {
        return;
      }
    } while (this.loop);
    this.finish();
  }

  void updateLocation(Point2D listenerLocation) {
    Point2D location = source.get();
    if (location != null) {
      double dx = location.getX() - listenerLocation.getX();
      double dy = location.getY() - listenerLocation.getY();
      double dist = Math.sqrt(dx * dx + dy * dy);
      if (this.panControl != null) {
        this.panControl.setValue(dist > 0 ? (float) (dx / dist) : 0f);
      }
      this.distance.set(Game.config().sound().getSoundVolume() * (float) Math.max(1.0 - dist / Game.audio().getMaxDistance(), 0.0));
    } else {
      this.distance.set(Game.config().sound().getSoundVolume());
    }
  }

  @Override
  protected void play() {
    this.updateLocation(Game.audio().getListenerLocation());
    super.play();
    Game.audio().addSound(this);
  }
}
