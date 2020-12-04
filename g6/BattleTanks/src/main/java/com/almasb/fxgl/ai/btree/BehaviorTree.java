/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree;

import com.almasb.fxgl.core.collection.Array;

/**
 * The behavior tree itself.
 *
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * @author implicit-invocation
 * @author davebaol
 */
public class BehaviorTree<E> extends Task<E> {

    private Task<E> rootTask;
    private E entity;
    GuardEvaluator<E> guardEvaluator;

    private final Array<Listener<E>> listeners = new Array<>();

    /**
     * Note: this is needed for no-arg constructions.
     *
     * Creates a {@code BehaviorTree} with no root task and no blackboard object. Both the root task and the blackboard object must
     * be set before running this behavior tree, see {@link #addChild(Task) addChild()} and {@link #setEntity(Object) setObject()}
     * respectively.
     */
    public BehaviorTree() {
        this(null, null);
    }

    /**
     * Creates a behavior tree with a root task and no blackboard object. Both the root task and the blackboard object must be set
     * before running this behavior tree, see {@link #addChild(Task) addChild()} and {@link #setEntity(Object) setObject()}
     * respectively.
     *
     * @param rootTask the root task of this tree. It can be {@code null}.
     */
    public BehaviorTree(Task<E> rootTask) {
        this(rootTask, null);
    }

    /**
     * Creates a behavior tree with a root task and a blackboard object. Both the root task and the blackboard object must be set
     * before running this behavior tree, see {@link #addChild(Task) addChild()} and {@link #setEntity(Object) setObject()}
     * respectively.
     *
     * @param rootTask the root task of this tree. It can be {@code null}.
     * @param entity   the blackboard. It can be {@code null}.
     */
    public BehaviorTree(Task<E> rootTask, E entity) {
        this.rootTask = rootTask;
        this.entity = entity;
        this.tree = this;
        this.guardEvaluator = new GuardEvaluator<E>(this);
    }

    /**
     * Returns the blackboard object of this behavior tree.
     */
    @Override
    public E getEntity() {
        return entity;
    }

    /**
     * Sets the blackboard object of this behavior tree.
     *
     * @param object the new blackboard
     */
    public void setEntity(E object) {
        this.entity = object;
    }

    /**
     * This method will add a child, namely the root, to this behavior tree.
     *
     * @param child the root task to add
     * @return the index where the root task has been added (always 0).
     * @throws IllegalStateException if the root task is already set.
     */
    @Override
    protected int addChildToTask(Task<E> child) {
        if (this.rootTask != null)
            throw new IllegalStateException("A behavior tree cannot have more than one root task");

        this.rootTask = child;
        return 0;
    }

    @Override
    public int getChildCount() {
        return rootTask == null ? 0 : 1;
    }

    @Override
    public Task<E> getChild(int i) {
        if (i == 0 && rootTask != null)
            return rootTask;

        throw new IndexOutOfBoundsException("index can't be >= size: " + i + " >= " + getChildCount());
    }

    @Override
    public void childRunning(Task<E> runningTask, Task<E> reporter, double tpf) {
        running(tpf);
    }

    @Override
    public void childFail(Task<E> runningTask, double tpf) {
        fail(tpf);
    }

    @Override
    public void childSuccess(Task<E> runningTask, double tpf) {
        success(tpf);
    }

    @Override
    public void onUpdate(double tpf) {

    }

    /**
     * This method should be called when game entity needs to make decisions: call this in game loop or after a fixed time slice if
     * the game is real-time, or on entity's turn if the game is turn-based
     */
    public void step(double tpf) {
        if (rootTask.status == Status.RUNNING) {
            rootTask.onUpdate(tpf);
        } else {
            rootTask.setControl(this);
            rootTask.start();
            if (rootTask.checkGuard(this, tpf))
                rootTask.onUpdate(tpf);
            else
                rootTask.fail(tpf);
        }
    }

    @Override
    public void reset() {
        super.reset();
        tree = this;
    }

    @Override
    protected Task<E> copyTo(Task<E> task) {
        BehaviorTree<E> tree = (BehaviorTree<E>) task;
        tree.rootTask = rootTask.cloneTask();

        return task;
    }

    public void addListener(Listener<E> listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener<E> listener) {
        listeners.removeValueByIdentity(listener);
    }

    public void removeListeners() {
        listeners.clear();
    }

    public void notifyStatusUpdated(Task<E> task, Status previousStatus) {
        for (Listener<E> listener : listeners) {
            listener.statusUpdated(task, previousStatus);
        }
    }

    public void notifyChildAdded(Task<E> task, int index) {
        for (Listener<E> listener : listeners) {
            listener.childAdded(task, index);
        }
    }

    private static final class GuardEvaluator<E> extends Task<E> {

        // No argument constructor useful for Kryo serialization
        @SuppressWarnings("unused")
        public GuardEvaluator() {
        }

        public GuardEvaluator(BehaviorTree<E> tree) {
            this.tree = tree;
        }

        @Override
        protected int addChildToTask(Task<E> child) {
            return 0;
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public Task<E> getChild(int i) {
            return null;
        }

        @Override
        public void onUpdate(double tpf) {
        }

        @Override
        public void childSuccess(Task<E> task, double tpf) {
        }

        @Override
        public void childFail(Task<E> task, double tpf) {
        }

        @Override
        public void childRunning(Task<E> runningTask, Task<E> reporter, double tpf) {
        }

        @Override
        protected Task<E> copyTo(Task<E> task) {
            return null;
        }
    }

    /**
     * The listener interface for receiving task events. The class that is interested in processing a task event implements this
     * interface, and the object created with that class is registered with a behavior tree, using the
     * {@link BehaviorTree#addListener(Listener)} method. When a task event occurs, the corresponding method is invoked.
     *
     * @param <E> type of the blackboard object that tasks use to read or modify game state
     * @author davebaol
     */
    public interface Listener<E> {

        /**
         * This method is invoked when the task status is set. This does not necessarily mean that the status has changed.
         *
         * @param task           the task whose status has been set
         * @param previousStatus the task's status before the update
         */
        void statusUpdated(Task<E> task, Status previousStatus);

        /**
         * This method is invoked when a child task is added to the children of a parent task.
         *
         * @param task  the parent task of the newly added child
         * @param index the index where the child has been added
         */
        void childAdded(Task<E> task, int index);
    }
}
