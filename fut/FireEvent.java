package fut;
import robocode.*;
import java.awt.geom.*;

/**
 * MyClass - a class by (your name here)
 */
public class FireEvent
{
	private double ticksfrom;
	private AdvancedEnemyBot enemy;
	private double velocity;
	private double power;
	private double[] location;
	private double[] locationAdvanced;
	private double bearing;
	private double advBearing;
	private double[] meLocation;
	private AdvancedRobot me;
	public FireEvent(AdvancedRobot me, AdvancedEnemyBot enemy, double deltaEnergy)
	{
		this.enemy = enemy;
		this.me = me;
		velocity = 20-deltaEnergy*3;
		power = deltaEnergy;
		location = new double[]{enemy.getX(),enemy.getY()};
		meLocation = new double[]{me.getX(),me.getY()};
		bearing = Math.tan2((location[1]-meLocation[1])/(location[0]-meLocation[0]));
		advBearing = Math.tan2((location[1]-meLocation[1]+me.getVelocity/sin(me.getHeading()))/(location[0]-meLocation[0]+me.getVelocity/cos(me.getHeading())));
	}
	
	public double[] getLocation()
	{
		return location;
	}

	public boolean danger()
	{
		long time = (long)(getDistance() / power);
		Point2D futureTankLocation = new Point2D(time*me.getVelocity()*Math.cos(me.getHeading())+me.getX(), time*me.getVelocity()*Math.sin(me.getHeading())+me.getY())
		if(Point2D(time*velocity*Math.cos(bearing)+location[0], time*velocity*Math.sin(bearing)+location[1]).distance(futureTankLocation)<7.5)
		return true;
		return false;
	}	

	public double[] getAdLocation()
	{
		return locationAdvanced;
	}
	
	public double[] getFutureLocation(double time)
	{
		
	}
	
	public getDistance()	
	{
		Point2D location = new Point2D(me.getX(),me.getY());
		Point2D bLoc 	 = new Point2D(location[0],location[1]);
		return location.distance(bLoc);
	}
	
	public update()
	{
		location[0] += velocity*Math.cos(bearing);
		location[1] += velocity*Math.sin(bearing);
	}
	public void paint()
	{
		g.drawLine(meLocation[1]+me.getVelocity/cos(me.getHeading()), 0, 100, 150); //to, from
	}
}
