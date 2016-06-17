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
public class SecantBot implements BotStrategy {
    private Ball ball;
    private Vector3f holePosition;
    
    public SecantBot(Ball ball, Vector3f holePosition) {
        this.ball = ball;
        this.holePosition = holePosition;
    }
    
    public Vector3f computeShot() {
        Vector3f ballHoleVector = holePosition.subtract(ball.getSpatial().getLocalTranslation());
        float distanceBallHole = holePosition.distance(ball.getSpatial().getLocalTranslation());
        
        Vector3f direction = ballHoleVector.normalize();
        float answer = secantMethod(0.1f,30f,distanceBallHole,direction) * 1.05f;         // times 1.1 because otherwise it is just too slow
        System.out.println("Secantshit:" + answer);
        return direction.mult(answer);
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
