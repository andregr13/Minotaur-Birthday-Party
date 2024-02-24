import java.util.*;
import java.io.*;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class vase
{
	private static Queue<Visitor> queue = new LinkedList<>();
	private static Lock lock = new ReentrantLock();
		
	public static void main(String [] args)
	{
		int totalVisitor = 10;
		
		Visitor visitors [] = new Visitor[totalVisitor];
		
		for (int i = 0; i < totalVisitor; i++)
		{
			visitors[i] = new Visitor(i);
			visitors[i].start();
		}
	}
	
	static class Visitor extends Thread 
	{
		public int id;
		Visitor(int id) 
		{
			this.id = id;
		}
		public void run() 
		{
			Random rand = new Random();
			int desiredViewings = rand.nextInt(3)+1; // randomly decide how many times the visitor wants to view vase
			int vaseViewings = 0;
			
			while (vaseViewings < desiredViewings)
			{
				try
				{
					this.sleep(rand.nextInt(100)); // deciding when to join queue again
				}
				catch (InterruptedException e) {}
				synchronized (queue)
				{
					queue.add(this);
					System.out.println("Visitor " + this.id + " joined the queue");
				}
				
				lock.lock();
				
				try
				{
					synchronized (queue)
					{
						System.out.println("Visitor " + this.id + " viewed the vase");
						try
						{
							this.sleep(rand.nextInt(1000)); // viewing time
						}
						catch (InterruptedException e) {}
						vaseViewings++;
					}
				}
				catch (Exception e) {}
				
				synchronized (queue)
				{
					queue.remove();
					if (!queue.isEmpty())
						System.out.println("Visitor " + this.id + " left the vase room and notifies visitor " + queue.peek().id);
					else
						System.out.println("Visitor " + this.id + " left the vase room and the queue is empty");
				}
				lock.unlock();
			}
		}
	}
}
