package fut;
import robocode.*;
import java.awt.geom.*;
import java.awt.Color;
import robocode.util.Utils;
import java.util.*;
// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html                TO ADD: BETTER TARGETING, WHAT TO DO WHEN DAMAGED

/**
 * Fut - a robot by (your name here)
 */
public class Fut extends AdvancedRobot
{
	private double enemyBearing = 0;
//	private Point2D[] = new Point2D[500]();
	int lastDetectionTicks = 100;
	AdvancedEnemyBot enemy = new AdvancedEnemyBot();
	private byte moveDirection = 1;
//	private Point2D lastLoc;
	private String mode = "Fight";
	private double lastEnergy = 0;
	List<WaveBullet> waves = new ArrayList<WaveBullet>();

	
	int[][] stats = new int[13][31]; // onScannedRobot can scan up to 1200px, so there are only 13.

					  // Note: this must be odd number so we can get
					  // GuessFactor 0 at middle.
	int direction = 1;
	

	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		 setColors(Color.red,Color.red,Color.white); // body,gun,radar

		
		// Robot main loop
		setAdjustRadarForRobotTurn(true);
		int radarDirection = 1;
		
		while(true)
		{
			
		//	System.out.println(getTime());
			doMove();
			//Radar
			if(lastDetectionTicks<15)
			{
				setScanColor(new Color(255,50,50));
				lastDetectionTicks++;
				if (getRadarTurnRemaining() == 0)
				{
					double turn = getHeading() - getRadarHeading() + enemyBearing;
					turn += 20 * radarDirection;
					radarDirection *= -1;
					
					setTurnRadarRight(normalizeBearing(turn));
				}
			}
			else//Robot not detected for x ticks
			{
			setScanColor(new Color(50,150,250));
			System.out.println("Robot not detected for "+lastDetectionTicks+" ticks.");
			setTurnRadarLeft(360);
			}

			
			//Moving
			
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		enemy.update(e);
		enemyBearing = e.getBearing();
		lastDetectionTicks = 0;
		
		if(lastEnergy - enemy.getEnergy() != 0)
		{
			onEnemyFired();
			lastEnergy = enemy.getEnergy();
			doMove();
		}
	/*	if(lastLocation == null)
			lastLocation = new Point2D(enemy.getX(),enemy.getY());
		RecordMovementVector(new Point2D(enemy.getX(),enemy.getY()));
		// calculate firepower based on distance
  */
		double firepower = Math.min(500 / enemy.getDistance(), 3);
		
		// calculate speed of bullet
		//double bulletSpeed = 20 - firepower * 3;
/*
		// distance = rate * time, solved for time
		long time = (long)(enemy.getDistance() / bulletSpeed);
		
		if ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 ||
			e.getName().equals(enemy.getName())) {

		// track him using the NEW update method
		enemy.update(e, this);
		// calculate gun turn to predicted x,y location
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
		// turn the gun to the predicted x,y location
*/
	
		double absBearing = getHeadingRadians() + e.getBearingRadians();
 
		// find our enemy's location:
		double ex = getX() + Math.sin(absBearing) * e.getDistance();
		double ey = getY() + Math.cos(absBearing) * e.getDistance();
 

		// Let's process the waves now:
		for (int i=0; i < waves.size(); i++)
		{
			WaveBullet currentWave = (WaveBullet)waves.get(i);
			if (currentWave.checkHit(ex, ey, getTime()))
			{
				waves.remove(currentWave);
				i--;
			}
		}
 
		double power = Math.min(3, Math.max(.1, firepower));
		// don't try to figure out the direction they're moving 
		// they're not moving, just use the direction we had before
		if (e.getVelocity() != 0)
		{
			if (Math.sin(e.getHeadingRadians()-absBearing)*e.getVelocity() < 0)
				direction = -1;
			else
				direction = 1;
		}
		int[] currentStats = stats[(int)(e.getDistance() / 100)]; // It doesn't look silly now!

					    // show something else later
		WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power, direction, getTime(), currentStats);

		if(mode.equals("Fight"))
		{
			int bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
			for (int i=0; i<31; i++)
				if (currentStats[bestindex] < currentStats[i])
					bestindex = i;
	 
			// this should do the opposite of the math in the WaveBullet:
			double guessfactor = (double)(bestindex - (currentStats.length - 1) / 2)
	                        / ((currentStats.length - 1) / 2);
			double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
	        double gunAdjust = Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + angleOffset);
	        setTurnGunRightRadians(gunAdjust);
			
			      if (getGunHeat() == 0 && gunAdjust < Math.atan2(9, e.getDistance()) && getEnergy() >= firepower+0.1 && setFireBullet(firepower) != null) 
				  {
	
	                        waves.add(newWave);
				   }
		}
		else if(mode.equals("Cornered"))
		{
			setTurnGunRight(normalizeBearing(getHeading() - getGunHeading() + enemy.getBearing()));
		}
		
		//if(getHeading() - getGunHeading() + e.getBearing()<10)
	/*	if(getEnergy()>firePower)
		setFire(firePower);*/
	/*	if(e.getEnergy() == 0)
		{
		setTurnRight(normalizeBearing(getHeading() - getGunHeading() + enemy.getBearing()));
		setFire(0.01);
		}*/
		
	}

	public void onEnemyFired()
	{
		//setTurnGunRight(normalizeBearing(getHeading() - getGunHeading() + enemy.getBearing()));
		//setFire(0.01);
		moveDirection *= -1;
	}
	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		setTurnRight(normalizeBearing(getHeading()+ enemy.getBearing())+35*moveDirection*-1);
		//moveDirection*=-1;
		setAhead(Math.abs(250 *Math.random()*moveDirection+(50*moveDirection))*-1);
		
		moveDirection *= -1;
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {//--------------------------------------------------------HIT WALL--------------------------
		// Replace the next line with any behavior you would like
		if(!mode.equals("Cornered")){
		setTurnRight(normalizeBearing(getHeading() - getGunHeading() + enemy.getBearing()+45*moveDirection));
		setAhead(150);
		mode = "Cornered";
		System.out.println("Cornered");}
	}
	
	public void update()
	{
		
	}
	
	public void doMove() {//--------------------------------------------------------------------------DO MOVE---------------------------
	//	System.out.println(getTime()+"  FUCKING MOVE");
		// always square off against our enemy
		
		if(enemy.getName().equals("")){
			System.out.println("Initial Move");
			setTurnRight(enemy.getBearing() + 90 +moveDirection*-25 );
			moveDirection *= -1;
			setAhead(200 * moveDirection);}
			
		if(mode.equals("Cornered")&&getDistanceRemaining()<10){
		mode = "Fight";}
		if(!mode.equals("Cornered")){
			
		int movein = 10;
		if (enemy.getDistance() < 100)
			movein = -10;
			
		setTurnRight(enemy.getBearing() + 90 + (movein * moveDirection));
	
		// strafe by changing direction every 20 ticks
		if (getTime() % 25 == 0) {
			System.out.println("move 20");
			//moveDirection *= -1;
			setAhead(150 * moveDirection);
		}
		else if(getTime() % (Math.round(15*Math.random())+5) == 0){
			System.out.println("move random");
			setAhead(100*Math.random()*moveDirection);
		}}
	}	
	//----------------------------------------------------------------------------STUFF YOU SHOULDN'T CARE ABOUT------------------------
	public double normalizeBearing(double angle)
	{
		while (angle > 180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	// computes the absolute bearing between two points

	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;
	
		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}
	
		return bearing;
	}
	//-------------------------------------------------------------------------------------------------
}
