/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.BehaviorTreeInstantAction;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PatrolTask extends BehaviorTreeInstantAction {

    private static final Point2D[] POINTS = new Point2D[] {
            new Point2D(300, 300),
            new Point2D(500, 500),
            new Point2D(450, 250)
    };

    private Point2D selectedPoint = POINTS[0];

    @Override
    public void performOnce(double tpf) {
        getEntity().translateTowards(selectedPoint, 60 * tpf);

        if (getEntity().getPosition().distance(selectedPoint) < 5) {
            selectedPoint = FXGLMath.random(POINTS).get();
        }
    }
}
