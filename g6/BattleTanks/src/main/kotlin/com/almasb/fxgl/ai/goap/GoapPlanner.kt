/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import java.util.*

/**
 * Plans what actions can be completed in order to fulfill a goal state.
 *
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object GoapPlanner {

    /**
     * Plan what sequence of actions can fulfill the goal.
     * Returns an empty queue if a plan could not be found,
     * or a list of the actions
     * that must be performed, in order, to fulfill the goal.
     */
    fun plan(availableActions: Set<GoapAction>, worldState: WorldState, goal: WorldState): Queue<GoapAction> {

        // reset the actions so we can start fresh with them
        availableActions.forEach { it.cancel() }

        // check what actions can run
        val usableActions = availableActions.filter { it.canRun() }.toSet()

        // we now have all actions that can run, stored in usableActions

        // build up the tree and record the leaf nodes that provide a solution to the goal
        val leaves = ArrayList<Node>()

        // build graph
        val start = Node(null, 0f, worldState, null)
        val success = buildGraph(start, leaves, usableActions, goal)

        if (!success) {
            return ArrayDeque()
        }

        // get the cheapest leaf
        val cheapest = leaves.minBy { it.runningCost }

        // get its node and work back through the parents
        val result = ArrayList<GoapAction>()
        var n: Node? = cheapest
        while (n != null) {
            if (n.action != null) {
                result.add(n.action!!)
            }
            n = n.parent
        }

        // we now have this action list in correct order
        return ArrayDeque(result.reversed())
    }

    /**
     * Returns true if at least one solution was found.
     * The possible paths are stored in the leaves list.
     * Each leaf has a 'runningCost' value where the lowest cost will be the best action
     * sequence.
     */
    private fun buildGraph(parent: Node, leaves: MutableList<Node>, usableActions: Set<GoapAction>, goal: WorldState): Boolean {
        var foundOne = false

        // prefer low cost actions over high cost
        val sortedActions = usableActions.sortedBy { it.cost }

        // go through each action available at this node and see if we can use it here
        for (action in sortedActions) {

            // if the parent state has the conditions for this action's preconditions, we can use it here
            if (action.preconditions.isIn(parent.state)) {

                // apply the action's effects to the parent state
                val currentState = populateState(parent.state, action.effects)

                val node = Node(parent, parent.runningCost + action.cost, currentState, action)

                if (goal.isIn(currentState)) {
                    // we found a solution!
                    leaves.add(node)
                    foundOne = true
                } else {
                    // not at a solution yet, so test all the remaining actions and branch out the tree
                    val subset = usableActions - (action)

                    val found = buildGraph(node, leaves, subset, goal)
                    if (found)
                        foundOne = true
                }
            }
        }

        return foundOne
    }

    /**
     * Apply the stateChange to the currentState.
     */
    private fun populateState(currentState: WorldState, stateChange: WorldState): WorldState {
        // copy
        val newState = WorldState(currentState)
        newState.update(stateChange)

        return newState
    }

    /**
     * Used for building up the graph and holding the running costs of actions.
     */
    private class Node constructor(var parent: Node?, var runningCost: Float, var state: WorldState, var action: GoapAction?)
}

