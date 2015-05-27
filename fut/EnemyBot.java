package fut;
import robocode.*;
/**
 * MyClass - a class by (your name here)
 */
public class EnemyBot
{
	private double bearing;
	private double distance;
	private double energy;
	private double heading;
	private double velocity;
	private String name;
	
	public double getBearing()
	{
		return bearing;
	}
	public double getDistance()
	{
		return distance;
	}
	public double getEnergy()
	{
		return energy;
	}
	public double getHeading()
	{
		return heading;
	}
	public double getVelocity()
	{
		return velocity;
	}
	public String getName()
	{
		return name;
	}
		
	public void update(ScannedRobotEvent e)
	{
		bearing = e.getBearing();
		distance = e.getDistance();
		energy = e.getEnergy();
		heading = e.getHeading();
		velocity = e.getVelocity();
		name = e.getName();
	}
	
	public void reset()
	{
		bearing = 0;
		distance = 0;
		energy = 0;
		heading = 0;
		velocity = 0;
		name = "";
	}
	
	public boolean none()
	{
		return name.equals("");
	}
	
	public EnemyBot()
	{
		reset();
	}
}
