package de.gurkenlabs.litiengine;

import java.util.concurrent.locks.Lock;

/**
 * The <code>ILoop</code> interface provide method for game loops that are publicly exposed.
 * 
 * <p>
 * A loop is an implementation that performs actions (e.g. physics, rendering, input processing, ...) and updates other <code>IUpdatable</code>
 * instances while the game is running.
 * </p>
 * 
 * @see IUpdateable
 */
public interface ILoop extends ILaunchable {
  /**
   * Attaches the update method of the specified IUpdatable instance to be called
   * every tick. The tick rate can be configured in the client configuration and
   * is independent from rendering.
   * 
   * @param updatable
   *          The instance that will be registered for the update event.
   */
  public void attach(final IUpdateable updatable);

  /**
   * Detaches the specified instance from the game loop.
   * 
   * @param updatable
   *          The instance that will be unregistered for the update event.
   */
  public void detach(final IUpdateable updatable);

  /**
   * Gets the amount of attached <code>IUpdatable</code> instances of this loop.
   * 
   * @return The amount instances attached to this loop.
   */
  public int getUpdatableCount();

  /**
   * Gets the total amount of ticks performed by this loop since it was started.
   * 
   * @return The total amount of elapsed ticks.
   * 
   * @see #start()
   */
  public long getTicks();

  /**
   * Gets the rate at which this loop performs its updates.
   * 
   * @return The update rate in ticks per second.
   */
  public int getTickRate();

  /**
   * Gets the total time in milliseconds that passed since the last tick.
   * <br>
   * i.e. process time + delay (to enforce the tick rate of this loop)
   *
   * @return The delta time in ms.
   * 
   * @see #getProcessTime()
   */
  public long getDeltaTime();

  /**
   * Gets the actual process time in milliseconds that was required during the last tick.
   * <br>
   * i.e. delta time - delay
   * 
   * @return The actual process time of the last tick in ms.
   * 
   * @see #getDeltaTime()
   */
  public double getProcessTime();

  /**
   * Returns a lock that can be used for actions that must be performed either within or independently of the loop.
   * 
   * @return A {@code Lock} for this loop.
   */
  public Lock getLock();

  /**
   * Sets the tickrate at which the loop performs its updates.
   * 
   * @param tickRate
   *          The tickrate of the loop.
   */
  public void setTickRate(int tickRate);
}
