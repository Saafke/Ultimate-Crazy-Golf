/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author nicolagheza
 */
public class BinarySearchBot implements BotStrategy {

    private PhysicsEngine physics;
    private Ball ball;
    private Vector3f ballPosition, holePosition;
    private final boolean DEBUG = true;

    public BinarySearchBot(PhysicsEngine physics, Ball ball, Vector3f holePosition) {
        this.physics = physics;
        this.ball = ball;
        this.holePosition = holePosition;
    }

    public Vector3f computeShot() {
        ballPosition = ball.getLocation();
        Vector3f shotVector = holePosition.subtract(ball.getLocation());
        float distance = shotVector.length();
        if (DEBUG) {
            System.out.println("Hole location: " + holePosition);
            System.out.println("Ball position: " + ballPosition);
            System.out.println("Starting distance: " + distance);
        }
        shotVector = shotVector.normalize();
        float a = 0.1f;
        float b = 10.0f;
        float c = bisection_method(a, b, distance, shotVector,0.001f);
        return shotVector.mult(c);
    }

    private float bisection_method(float a, float b, float distance, Vector3f shotVector, float e) {
        float c = 0f;
        float newDistance =0f; // distancance between original ball and new ball 
        while (Math.abs(distance-newDistance)>1f) {
            c = (a + b) / 2;
            newDistance = simulateShot(shotVector.mult(c));
            if (DEBUG) {
                System.out.println("New distance: " + newDistance + " with c: " + c);
            }
            if (newDistance < distance) {
                a = c;
            } else if (newDistance > distance) {
                b = c;
            }
            if (DEBUG) {
                System.out.println("New c: " + c);
            }
        }
        return c;
    }

    private float simulateShot(Vector3f shotVector) {
        Ball testBall = new Ball("testBall", ball.getBallControl().getSpatial().clone());
        testBall.getSpatial().setLocalTranslation(ballPosition);
        testBall.setLocation(ballPosition);
        if (DEBUG)
            System.out.println("Creating TestBall at position: " + testBall.getLocation());
        testBall.getBallControl().setVelocity(shotVector);
        boolean firstIteration = true;
        while (testBall.getBallControl().isMoving() || firstIteration) {
            firstIteration = false;
            physics.moveBall(testBall);
        }
        if (DEBUG) {
            System.out.println("TestBall moved to position: " + testBall.getLocation());
        }
        float newDistance = testBall.getLocation().subtract(ballPosition).length();
        return newDistance;
    }
}
