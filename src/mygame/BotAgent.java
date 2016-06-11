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
public class BotAgent implements Agent {
    private Ball ball;
    private Vector3f holePosition;
    private boolean isPlaying;
    
    public BotAgent(Ball ball, Hole hole) {
        this.ball = ball;
        this.holePosition = hole.getLocation();
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    public Vector3f getHolePosition() {
        return holePosition;
    }

    public void setHolePosition(Vector3f holePosition) {
        this.holePosition = holePosition;
    }

    public void performShot(float intensity, float xDir, float yDir) {
        intensity = intensity / 10;
        if (intensity < 0.4f) {
            ball.getBallControl().setxVelocity(xDir * intensity);
            ball.getBallControl().setzVelocity(yDir * intensity);
        } else {
            ball.getBallControl().setxVelocity(xDir * 0.4f);
            ball.getBallControl().setzVelocity(yDir * 0.4f);
        }
    }
    /*public void performShot(float intensity, float xDir, float yDir) {
        intensity = intensity / 10;
        if (intensity < 0.4f) {
            ball.getBallControl().setxVelocity(((xDir) / 10) * intensity);
            ball.getBallControl().setzVelocity(((yDir) / 10) * intensity);
        } else {
            ball.getBallControl().setxVelocity(((xDir) / 10) * 4f);
            ball.getBallControl().setzVelocity(((yDir) / 10) * 4f);
        }
    }*/
    
    public void computeShot() {
        Vector3f ballHoleVector = holePosition.subtract(ball.getSpatial().getLocalTranslation());
        float distanceBallHole = holePosition.distance(ball.getSpatial().getLocalTranslation());
                
        Ball testBall = new Ball("testBall",ball.getSpatial());
        
        Vector3f direction = ballHoleVector.normalize();
        testBall.setLocation(ball.getSpatial().getLocalTranslation());
        
//        System.out.println("ballHoleDistanceVector: " + ballHoleVector);
//        System.out.println("normalized ballHoleDistance (direction): " + direction);
//        System.out.println("ballHoleDistance/normalized ballHoleDistance (direction):" + ballHoleVector.divide(direction));
        
        float point1=0f, point2=4f;
        
//      while (!testBall.getBallControl().isMoving())
//          {
//            testBall.getBallControl().setxVelocity(shot.getX()*point2);
//           testBall.getBallControl().setxVelocity(shot.getY()*point2);
            
//        }
        
//        System.out.println("difference 3 : " + getEquation(distanceBallHole,direction,3f));
//        System.out.println("difference 0.1 : " + getEquation(distanceBallHole,direction,0.1f));
        
        float answer = secantMethod(0.1f,30f,distanceBallHole,direction) * 1.05f;         // times 1.1 because otherwise it is just too slow
        System.out.println("Secantshit:" + answer);
         
        direction = direction.mult(answer);

        ball.getBallControl().setxVelocity(direction.getX());
        ball.getBallControl().setzVelocity(direction.getZ());
    }
    public float getEquation(float distanceBallHole, Vector3f direction, float x){//ballHoleDistance = distance from ball to hole, x approximation
        //the approximated velocity 
        Vector3f fua = direction.mult(x);
        //how much the ball travels with that velocity (in vectors)
        Vector3f distance = ball.getBallControl().getDistance(fua);
        //how much the ball travels with that velocity (in floats)
        float distanceFloat = distance.length();
        //difference between the two
        float difference = distanceBallHole - distanceFloat;
        System.out.println("distance BallHole: " + distanceBallHole + "  -  " +" distanceWithApproxVelocity: " + distanceFloat + " =  " + difference);
        return difference;
    }
    
    public float secantMethod(float approx1, float approx2, float distanceBallHole, Vector3f direction){
        float b = getEquation(distanceBallHole,direction,approx2);
        float a = getEquation(distanceBallHole,direction,approx1);
        System.out.println("b: " + b);
        System.out.println("a: " + a);
        
        if(b == a) throw new IllegalArgumentException();
        
        float epsilon = 0.001f;
        float nextapprox;
        
        nextapprox = approx2 - (  (  (approx2 - approx1)/(b - a)  )  *  b);
       
        System.out.println("next apprpox ;" + nextapprox);
        
        if (Math.abs(nextapprox - approx2) > epsilon) return secantMethod(approx2,nextapprox,distanceBallHole,direction);
        else return nextapprox;
    }
    
}
