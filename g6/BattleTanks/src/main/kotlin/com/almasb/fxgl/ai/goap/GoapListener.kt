/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.entity.Entity
import java.util.Queue

/**
 * Any agent that wants to use GOAP must implement this interface.
 * It provides information to the GOAP
 * planner so it can plan what actions to use.
 *
 * It also provides an interface for the planner to give
 * feedback to the Agent and report success/failure.
 *
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GoapListener {

//    /**
//     * The starting state of the Agent and the world.
//     * Supply what states are needed for actions to run.
//     */
//    fun obtainWorldState(entity: Entity): WorldState
//
//    /**
//     * Give the planner a new goal so it can figure out
//     * the actions needed to fulfill it.
//     */
//    fun createGoalState(entity: Entity): WorldState

    /**
     * A plan was found for the supplied goal.
     * These are the actions the Agent will perform, in order.
     */
    fun planFound(entity: Entity, goal: WorldState, actions: Queue<GoapAction>)

    /**
     * No sequence of actions could be found for the supplied goal.
     * You will need to try another goal.
     */
    fun planFailed(entity: Entity, failedGoal: WorldState)

    /**
     * All actions are complete and the goal was reached.
     */
    fun actionsFinished(entity: Entity)

    /**
     * One of the actions caused the plan to abort.
     * That action is returned.
     */
    fun planAborted(entity: Entity, aborter: GoapAction)
}