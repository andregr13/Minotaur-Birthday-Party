import java.util.*;
import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;

public class LabyrinthGame
{
	static Labyrinth labyrinth = new Labyrinth();
	static Lock lock = new ReentrantLock();
	static Condition labyrinthOpen = lock.newCondition();
	static Condition [] visitorPicked;
	static int totalVisitors;
	
	public static void main(String [] args) throws FileNotFoundException
	{
		totalVisitors = 10;
		long startTime = System.nanoTime();
		
		
		visitorPicked = new Condition[totalVisitors];
		Visitor visitors [] = new Visitor[totalVisitors];
		
		
		
		visitorPicked[0] = lock.newCondition();
		visitors[0] = new Chosen(0, visitorPicked[0], labyrinthOpen, totalVisitors);

		for (int i = 1; i < totalVisitors; i++)
		{
			visitorPicked[i] = lock.newCondition();
			visitors[i] = new Visitor(i, visitorPicked[i], labyrinthOpen);
		}
		
		
		Minotaur minotaur = new Minotaur(visitors);
		minotaur.start();
		
		for (int i = 0; i < totalVisitors; i++)
		{
			visitors[i].start();
		}
		
		try
		{
			minotaur.interrupt();
		}
		catch (Exception e) {}
		
		for (int i = 0; i < totalVisitors; i++)
		{
			try
			{
				visitors[i].interrupt();
			}
			catch (Exception e) {}
		}
		
		boolean valid = true;
		for (Visitor visitor : visitors)
		{
			if (visitor.getVisits() == 0)
				valid = false;
		}
		System.out.println("Verification check of completed game: " + valid);
		long endTime = System.nanoTime();
		System.out.println(startTime-endTime);
	}
	
	public static class Labyrinth
	{
		private boolean cupcake = true;
		private boolean gameOver = false;
		private int currentVisitor;
		private boolean open = true;
	
		public Labyrinth()
		{

		}
		
		public boolean eat()
		{
			boolean retVal = false;
			if (cupcake == true)
			{
				cupcake = false;
				retVal = true;
			}
			return retVal;
		}
		
		public boolean check()
		{
			lock.lock();
			try
			{
				return cupcake;
			} finally
			{
				lock.unlock();
			}
		}
		
		public void replace()
		{
			this.cupcake = true;
		}
		
		public int getVisitor()
		{
			return currentVisitor;
		}
		
		public void setVisitor(int n)
		{
			this.currentVisitor = n;
		}
		
		public boolean isOver()
		{
			return this.gameOver;
		}
		
		public void end()
		{
			this.gameOver = true;
		}
		
		public void open()
		{
			this.open = true;
			this.currentVisitor = -1;
		}
		
		public void close()
		{
			this.open = false;
		}
		
		public boolean isOpen()
		{
			return this.open;
		}
	}
	
	public static class Minotaur extends Thread
	{
		Visitor [] visitors;
	
		public Minotaur(Visitor [] visitors)
		{
			super();
			visitors = visitors;
		}
		
		public void run()
		{
			Random random = new Random();
			int nextVisitor;
			while (!labyrinth.isOver())
			{
				lock.lock();
				try
				{
					while (!labyrinth.isOpen()  && !labyrinth.isOver())
					{
						labyrinthOpen.await();
					}
					if (labyrinth.isOver())
						break;
					nextVisitor = random.nextInt(totalVisitors);
					labyrinth.setVisitor(nextVisitor);
					labyrinth.close();
					visitorPicked[nextVisitor].signalAll();
					System.out.println("Minotaur picks visitor " + nextVisitor + " to enter.");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}
	
	public static class Visitor extends Thread
	{	
		public int visits;
		private boolean eaten;
		public int visitorNum;
		Condition myTurn;
		Condition labyrinthOpen;
		
		Visitor(int n, Condition allowedIn, Condition labyrinthOpen) 
		{
			super();
			this.visits = 0;
			this.eaten = false;
			this.visitorNum = n;
			this.myTurn = allowedIn;
			this.labyrinthOpen = labyrinthOpen;
		}
		
		public void run()
		{
			while (!labyrinth.isOver())
			{
				lock.lock();
				try 
				{
					while (labyrinth.getVisitor() != this.visitorNum && !labyrinth.isOver())
					{
						myTurn.await();
					}
					if (this.eaten == true)
					{
						System.out.println("Visitor " + visitorNum + " leaves without an action.");
						this.visits++;
					}
					else if (labyrinth.check() == true)
					{
						this.eaten = labyrinth.eat();
						System.out.println("Visitor " + visitorNum + " eats the cake.");
						this.visits++;
					}
					else
					{
						System.out.println("Visitor " + visitorNum + " arrives to no cake yet.");
						this.visits++;
					}
					if (labyrinth.isOver())
						break;
					//System.out.println("Visitor " + visitorNum + " leaves their " + visits +"thst visit.");
					labyrinth.open();
					labyrinthOpen.signalAll();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					lock.unlock();
				}
			}
		}
		
		public int getVisits()
		{
			return this.visits;
		}
	}

	public static class Chosen extends Visitor
	{	
		private int count;
		private int visitors;
		
		Chosen(int visitorNum, Condition allowedIn, Condition labyrinthOpen, int total) 
		{
			super(visitorNum, allowedIn, labyrinthOpen);
			this.count = 1;
			this.visitors = total;
		}
		
		public void run()
		{	
			while (!labyrinth.isOver())
			{
				lock.lock();
				try 
				{
					while (labyrinth.getVisitor() != this.visitorNum && !labyrinth.isOver())
					{
						this.myTurn.await();
					}
					if (labyrinth.check() == true)
					{
						System.out.println("The Chosen one leaves the cake.");
						this.visits++;
					}
					else
					{
						labyrinth.replace();
						this.count++;
						this.visits++;
						System.out.println("The Chosen one places a new cake and has confirmed " + count + " visitors.");
					}
					//System.out.println("The Chosen one leaves their " + visits +"thst visit.");
					labyrinth.open();
					labyrinthOpen.signalAll();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (count >= visitors)
					{
						System.out.println("The Chosen one ends the game.");
						labyrinth.end();
					}
					lock.unlock();
				}
				
			}
		}
	}
}
