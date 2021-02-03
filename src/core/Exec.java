package core;

import java.util.Iterator;

public class Exec {
	public static void main(String[] args) {
		ABR<Integer> a = new ABR<Integer>();
		
		a.add(1);
		a.add(8);
		a.add(7);
		a.add(0);
		a.add(-1);
		a.add(9);
		a.add(-7);
		a.add(12);
		a.add(21);
		a.add(10);
		a.add(25);
		a.add(24);
		a.add(3);
		
		Iterator<Integer> it = a.iterator();
		System.out.println(a);
		while (it.hasNext())
		{
			int cle = it.next();
			if (cle%3 == 0) it.remove();
		}
		System.out.println(a);
	}
}
