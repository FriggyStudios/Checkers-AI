package checkersAIPackage;
import java.util.ArrayList;

public class Board 
{
   ArrayList<Byte> entries;
   
   public Board()
   {
	   entries = new ArrayList<Byte>();	
	   for(int i = 0;i < 32;i++)
	   {
		   entries.add(CheckersData.EMPTY);		   
	   }
   }
   
   public Board(Board data)
   {
	    if (this == data)
	        return;
	   entries = new ArrayList<Byte>();	
	   for (int i = 0; i < data.entries.size();i++)
		   entries.add(i,data.entries.get(i));
   }
   
   public byte Get(int x, int y)
   {
	   if(x%2 != y%2)
		   if(x%2 == 0)
			   return entries.get((x/2)+(y*4));
		   else
			   return entries.get(((x-1)/2)+(y*4));
	   else
		   return CheckersData.EMPTY;
   }
   
   public void Add(byte entry,int x, int y)
   {	   
	   if(x%2 != y%2)
		   if(x%2 == 0)
		   {
			   entries.remove((x/2)+(y*4));
			   entries.add((x/2)+(y*4),entry);
		   }
		   else
		   {
			   entries.remove(((x-1)/2)+(y*4));
			   entries.add(((x-1)/2)+(y*4),entry);	
		   }
	   else
		   System.out.println("Board Add() out of range");
   }
}
