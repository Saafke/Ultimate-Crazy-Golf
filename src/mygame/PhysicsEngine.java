/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Line;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;

/**
 *
 * @author Husky
 */
public class PhysicsEngine {

    private ArrayList<Ball> balls;
    private ArrayList<Collidables> obstaclesList;
    private TerrainQuad terrain;
    private Line line;
    private boolean done = false;
    private float lastTerrainHeight;
    float yDirection = 0;
    float xDirection = -15;

    public PhysicsEngine(ArrayList<Ball> balls, ArrayList<Collidables> obstaclesList,
            TerrainQuad terrain) {
        this.balls = balls;
        this.obstaclesList = obstaclesList;
        this.terrain = terrain;
    }

    public void moveBall(Ball ball) {
        ball.getBallControl().computeMovement();
        float xtemp = (float) ball.getX() + ball.getBallControl().getxVelocity();
        float ytemp = (float) ball.getY() + ball.getBallControl().getyVelocity();
        float ztemp = (float) ball.getZ() + ball.getBallControl().getzVelocity();
        ball.getSpatial().setLocalTranslation(xtemp, ytemp, ztemp);
        //UPDATE VARIABLES IN BALL CLASS AGAIN!!
        ball.setLocation(ball.getSpatial().getLocalTranslation());
        ball.setXYZLocations();
    }

    public void checkBallCollision() {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                ballCollision(i, j);
            }
        }
    }

    private void ballCollision(int i, int j) {

        Vector3f dist = balls.get(i).getSpatial().getLocalTranslation().subtract(balls.get(j).getSpatial().getLocalTranslation());

        if (dist.length() < balls.get(i).getBallControl().getRadius() + balls.get(j).getBallControl().getRadius()) {

            System.out.println("ball " + i + " collides with ball " + j);

            Vector3f l12 = balls.get(j).getSpatial().getLocalTranslation().subtract(balls.get(i).getSpatial().getLocalTranslation()).normalize();
            Vector3f l21 = balls.get(i).getSpatial().getLocalTranslation().subtract(balls.get(j).getSpatial().getLocalTranslation()).normalize();

            Vector3f v1 = l12.mult(balls.get(i).getBallControl().getVelocity().dot(l12));
            Vector3f v2 = l21.mult(balls.get(j).getBallControl().getVelocity().dot(l21));

            Vector3f v1p = balls.get(i).getBallControl().getVelocity().subtract(v1);
            Vector3f v2p = balls.get(j).getBallControl().getVelocity().subtract(v2);

            float m1 = 1f, m2 = 1f;

            balls.get(i).getBallControl().setVelocity(v1.mult(m1 - m2).add(v2.mult(2 * m2)).mult(1 / (m1 + m2)).add(v1p));
            balls.get(j).getBallControl().setVelocity(v2.mult(m2 - m1).add(v1.mult(2 * m1)).mult(1 / (m1 + m2)).add(v2p));

            Vector3f temp1 = balls.get(i).getSpatial().getLocalTranslation().add(balls.get(i).getBallControl().getVelocity()),
                    temp2 = balls.get(j).getSpatial().getLocalTranslation().add(balls.get(j).getBallControl().getVelocity());

            balls.get(i).setLocation(temp1);
            balls.get(i).setXYZLocations();
            balls.get(j).setLocation(temp2);
            balls.get(j).setXYZLocations();
        }
    }

    public void checkCollisions(Ball ball) {
        for (int i = 0; i < obstaclesList.size(); i++) {

            if (obstaclesList.get(i).getClass() == Wall.class) {
                Wall x = (Wall) obstaclesList.get(i);
                //System.out.println(x.getInfo());
                if (x.getRotation() == false) {
                    if (Math.abs(ball.getSpatial().getLocalTranslation().getX() - x.getX()) < (ball.getZExtent() + x.getZExtent())
                            && Math.abs(ball.getSpatial().getLocalTranslation().getZ() - x.getZ()) < (ball.getXExtent() + x.getXExtent())) {
                        System.out.println("wall z collision with nr" + i);
//                        System.out.println(ball.getSpatial().getLocalTranslation().getX() +" - "+ x.getX() + " < " + (ball.getXExtent() + x.getXExtent()));
//                        System.out.println(ball.getSpatial().getLocalTranslation().getZ() +" - "+ x.getZ() + " < " + (ball.getZExtent() + x.getZExtent()));
                        ball.getBallControl().setzVelocity(ball.getBallControl().getzVelocity() * -1);
                    }
                } else {
                    if (Math.abs(ball.getSpatial().getLocalTranslation().getX() - x.getX()) < (ball.getXExtent() + x.getXExtent())
                            && Math.abs(ball.getSpatial().getLocalTranslation().getZ() - x.getZ()) < (ball.getZExtent() + x.getZExtent())) {
                        System.out.println("wall x collision with nr" + i);
//                        System.out.println(ball.getSpatial().getLocalTranslation().getX() +" - "+ x.getX() + " < " + (ball.getXExtent() + x.getXExtent()));
//                        System.out.println(ball.getSpatial().getLocalTranslation().getZ() +" - "+ x.getZ() + " < " + (ball.getZExtent() + x.getZExtent()));
                        ball.getBallControl().setxVelocity(ball.getBallControl().getxVelocity() * -1);
                    }
                }
            } else if (obstaclesList.get(i).getClass() == Tree.class) {
                Tree x = (Tree) obstaclesList.get(i);
                //System.out.println(x.getInfo());
                if (x.getRotation() == true) {
                    if (Math.abs(ball.getSpatial().getLocalTranslation().getX() - x.getX()) < (ball.getZExtent() + x.getZExtent())
                            && Math.abs(ball.getSpatial().getLocalTranslation().getZ() - x.getZ()) < (ball.getXExtent() + x.getXExtent())) {
                        System.out.println("tree z zcollision with nr" + i);
//                        System.out.println(ball.getSpatial().getLocalTranslation().getX() +" - "+ x.getX() + " < " + (ball.getXExtent() + x.getXExtent()));
//                        System.out.println(ball.getSpatial().getLocalTranslation().getZ() +" - "+ x.getZ() + " < " + (ball.getZExtent() + x.getZExtent()));
                        ball.getBallControl().setzVelocity(ball.getBallControl().getzVelocity() * -1);
                    }
                } else {
                    if (Math.abs(ball.getSpatial().getLocalTranslation().getX() - x.getX()) < (ball.getXExtent() + x.getXExtent())
                            && Math.abs(ball.getSpatial().getLocalTranslation().getZ() - x.getZ()) < (ball.getZExtent() + x.getZExtent())) {
                        System.out.println("tree x collision with nr" + i);
//                        System.out.println(ball.getSpatial().getLocalTranslation().getX() +" - "+ x.getX() + " < " + (ball.getXExtent() + x.getXExtent()));
//                        System.out.println(ball.getSpatial().getLocalTranslation().getZ() +" - "+ x.getZ() + " < " + (ball.getZExtent() + x.getZExtent()));
                        ball.getBallControl().setxVelocity(ball.getBallControl().getxVelocity() * -1);
                    }
                }
            }
        }
    }

    public void checkTerrainCollisions(Ball ball) {
        Vector3f ballLocation = ball.getSpatial().getLocalTranslation();
        //to get the 'slope'
        float x = ball.getSpatial().getLocalTranslation().getX(); // Whatever X you want to check...
        float z = ball.getSpatial().getLocalTranslation().getZ(); // Whatever Z you want to check...
        float ground_slope = 1f - terrain.getNormal(new Vector2f(x, z)).y;
        
        ball.getBallControl().setNormal(terrain.getNormal(new Vector2f(x,z)).normalize());
        
        // setting the 'slope'
        ball.getBallControl().setSlope(ground_slope);

        float terrainHeight = terrain.getHeight(new Vector2f(x, z));

        //System.out.println("slope: " + ground_slope + " height: " + terrainHeight);

        //Going UPHILL
        if (ball.getSpatial().getLocalTranslation().getY() < terrainHeight - 29f && ground_slope != 0
                && isTerrainColliding(ball.getSpatial().getLocalTranslation(), ball)) {
            ball.getBallControl().setUpHill(1);
            //DOWNHILL
        } else if (ball.getSpatial().getLocalTranslation().getY() >= terrainHeight - 29f && ground_slope != 0
                && isTerrainColliding(ball.getSpatial().getLocalTranslation(), ball)) {
            ball.getBallControl().setUpHill(-1);
            //FALLING
        } else if (ball.getSpatial().getLocalTranslation().getY() > terrainHeight - 29f && ball.getBallControl().getyVelocity() <= 0) {
            ball.getBallControl().setUpHill(2);
            //FLYING
        } else if (ball.getSpatial().getLocalTranslation().getY() > terrainHeight - 29f && ball.getBallControl().getyVelocity() > 0) {
            ball.getBallControl().setUpHill(3);
            //RESTING
        } else if (ground_slope == 0) {
            //to make sure the ball doesn't stick into the ground after landing
            ball.getSpatial().getLocalTranslation().setY(-29f);
            ball.setLocation(ball.getSpatial().getLocalTranslation());
            ball.setXYZLocations();

            ball.getBallControl().setyVelocity(0f);
            ball.getBallControl().setUpHill(0);
        }

        //System.out.println(ball.getBallControl().getUpHill());        
        //System.out.println(ball.getSpatial().getLocalTranslation().getY());

        if (isTerrainColliding(ball.getSpatial().getLocalTranslation(), ball)
                && ball.getBallControl().getyVelocity() != 0f) {

            ball.getSpatial().setLocalTranslation(x, terrainHeight - 29f, z);
            ball.setLocation(ball.getSpatial().getLocalTranslation());
            ball.setXYZLocations();

            if (ball.getBallControl().getUpHill() == 2 || ball.getBallControl().getUpHill() == 3) {
                ball.getBallControl().resetTime();
            }

            ball.getBallControl().setyVelocity((ball.getBallControl().getyVelocity() * -0.5f));
            //System.out.println("bouncing on terrain");
        }

        lastTerrainHeight = terrainHeight;
    }

    public boolean isTerrainColliding(Vector3f ballLocation, Ball ball) {
        float terrainHeight = terrain.getHeight(new Vector2f(ballLocation.getX(), ballLocation.getZ()));

        if (ballLocation.getY() + 29f - ball.getZExtent() <= terrainHeight) {
            return true;
        }
        return false;
    }
}
