package core;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * <p>
 * Implantation de l'interface Collection bas�e sur les arbres binaires de
 * recherche. Les �l�ments sont ordonn�s soit en utilisant l'ordre naturel (cf
 * Comparable) soit avec un Comparator fourni � la cr�ation.
 * </p>
 * 
 * <p>
 * Certaines m�thodes de AbstractCollection doivent �tre surcharg�es pour plus
 * d'efficacit�.
 * </p>
 * 
 * @param <E>
 *            le type des cl�s stock�es dans l'arbre
 */
public class ABR<E> extends AbstractCollection<E>
{
	private Noeud racine;
	private int taille;
	private Comparator<? super E> cmp;

	private class Noeud
	{
		E cle;
		Noeud gauche;
		Noeud droit;
		Noeud pere;

		Noeud(E cle) {
			this.cle = cle;
			gauche = droit = pere = null;
		}

		/**
		 * Renvoie le noeud contenant la cl� minimale du sous-arbre enracin�
		 * dans ce noeud
		 * 
		 * @return le noeud contenant la cl� minimale du sous-arbre enracin�
		 *         dans ce noeud
		 */
		Noeud minimum()
		{
			Noeud x = this;
			while (x.gauche != null) x = x.gauche;
			return x;
		}

		/**
		 * Renvoie le successeur de ce noeud
		 * 
		 * @return le noeud contenant la cl� qui suit la cl� de ce noeud dans
		 *         l'ordre des cl�s, null si c'es le noeud contenant la plus
		 *         grande cl�
		 */
		Noeud suivant()
		{
			if(this == null) return null;
			Noeud x = this;
			if (x.droit != null) return x.droit.minimum();
			Noeud y = x.pere;
			while (y != null && x == y.droit)
			{
				x = y;
				y = y.pere;
			}
			return y;
		}
	}

	// Consructeurs

	/**
	 * Cr�e un arbre vide. Les �l�ments sont ordonn�s selon l'ordre naturel
	 */
	public ABR()
	{
		racine = null;
		taille = 0;
		cmp    = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);
	}

	/**
	 * Cr�e un arbre vide. Les �l�ments sont compar�s selon l'ordre impos� par
	 * le comparateur
	 * 
	 * @param cmp
	 *            le comparateur utilis� pour d�finir l'ordre des �l�ments
	 */
	public ABR(Comparator<? super E> cmp)
	{
		racine   = null;
		taille   = 0;
		this.cmp = cmp;
	}

	/**
	 * Constructeur par recopie. Cr�e un arbre qui contient les m�mes �l�ments
	 * que c. L'ordre des �l�ments est l'ordre naturel.
	 * 
	 * @param c
	 *            la collection � copier
	 */
	public ABR(Collection<? extends E> c)
	{
		for(int i=0; i<c.size(); i++)
		{
			Noeud z = new Noeud((E)(c.toArray()[i]));
			Noeud y = null;
			Noeud x = racine;
			cmp = (e1, e2) -> ((Comparable<E>)e1).compareTo(e2);
			c.stream().sorted(cmp);
			y = null;
			x = racine;
			while (x != null) {
				y = x;
				x = cmp.compare(z.cle, y.cle) ==-1 ? x.gauche : x.droit;
			}
			z.pere = y;
			if (y == null) { // arbre vide
				racine = z;
			} else {
				if (cmp.compare(z.cle, y.cle) ==-1)
					y.gauche = z;
				else
					y.droit = z;
			  }
			  z.gauche = z.droit = null;
		}
	}

	@Override
	public Iterator<E> iterator() {return new ABRIterator();} //Un sans ABRIterator?

	@Override
	public int size() {return taille;}

	// Quelques m�thodes utiles

	/**
	 * Recherche une cl�. Cette m�thode peut �tre utilis�e par
	 * {@link #contains(Object)} et {@link #remove(Object)}
	 * 
	 * @param o
	 *            la cl� � chercher
	 * @return le noeud qui contient la cl� ou null si la cl� n'est pas trouv�e.
	 */
	private Noeud rechercher(Object o)
	{
		Noeud courant = racine;
		while(courant != null)
		{
			if(cmp.compare(courant.cle, (E)o) < 0)
			{
				courant=courant.gauche;
			}
			else if(cmp.compare(courant.cle, (E)o) > 0)
			{
				courant = courant.droit;
			}
			else
			{
				return courant;
			}
		}
		return null;
	}

	/**
	 * Supprime le noeud z. Cette m�thode peut �tre utilis�e dans
	 * {@link #remove(Object)} et {@link Iterator#remove()}
	 * 
	 * @param z
	 *            le noeud � supprimer
	 * @return le noeud contenant la cl� qui suit celle de z dans l'ordre des
	 *         cl�s. Cette valeur de retour peut �tre utile dans
	 *         {@link Iterator#remove()}
	 */
	private Noeud supprimer(Noeud z)
	{
		if( z == null ) return null;
		
		Noeud y;
		//Noeud next = z.suivant();

		if( z.gauche == null || z.droit == null ) y = z;
		else                                      y = z.suivant();

		Noeud x;
		
		if( y == null ) return null;
		
		if( y.gauche != null ) x = y.gauche;
		else                   x = y.droit;
		// x est le fils unique de y ou null si il n'y as pas de fils
		
		if( x != null ) x.pere = y.pere;
		
		// suppression de la racine
		if( y.pere == null ) {this.racine = x;}
		else
			if( y.equals(y.pere.gauche) ) y.pere.gauche = x;
			else                          y.pere.droit  = x;
		
		if( !y.equals(z) )  z.cle = y.cle;
		this.taille--;
		
		return y;//next;
    }
	
	public E supprimer( E e )
    {
        if( e == null ) return null;

        Noeud n = this.rechercher(e);

        if( n == null ) return null;

        n = this.supprimer(n);

        if( n == null ) return null;

        return n.cle;
    }


	/**
	 * Les it�rateurs doivent parcourir les �l�ments dans l'ordre ! Ceci peut se
	 * faire facilement en utilisant {@link Noeud#minimum()} et
	 * {@link Noeud#suivant()}
	 */
	private class ABRIterator implements Iterator<E>
	{
		private Noeud courant;
		private Noeud suivant;

		public ABRIterator()
		{
			super();
			this.courant = null;
			this.suivant = ABR.this.racine.minimum();
		}

        public boolean hasNext(){return this.suivant != null;}

		public E next()
		{
			this.courant = this.suivant;
			this.suivant = this.suivant.suivant();
			return this.courant.cle;
		}

        public void remove(){ABR.this.supprimer(this.courant);}
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		toString(racine, buf, "", maxStrLen(racine));
		return buf.toString();
	}

	private void toString(Noeud x, StringBuffer buf, String path, int len)
	{
		if (x == null)
			return;
		toString(x.droit, buf, path + "D", len);
		for (int i = 0; i < path.length(); i++)
		{
			for (int j = 0; j < len + 6; j++)
				buf.append(' ');
			char c = ' ';
			if (i == path.length() - 1)
				c = '+';
			else if (path.charAt(i) != path.charAt(i + 1))
				c = '|';
			buf.append(c);
		}
		buf.append("-- " + x.cle.toString());
		if (x.gauche != null || x.droit != null)
		{
			buf.append(" --");
			for (int j = x.cle.toString().length(); j < len; j++)
				buf.append('-');
			buf.append('|');
		}
		buf.append("\n");
		toString(x.gauche, buf, path + "G", len);
	}

	private int maxStrLen(Noeud x) {
		return x == null ? 0 : Math.max(x.cle.toString().length(),
				Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
	}

	@Override
	public boolean add(E e)
	{
		if( e == null ) return false;

		Noeud z = new Noeud(e);
		Noeud y = null;
		Noeud x = racine;

		while (x != null)
		{
			y = x;
			
			if (cmp.compare(z.cle, x.cle) < 0) x = x.gauche;
			else                               x = x.droit;
		}

		z.pere = y;
		
		if( y == null )
			racine = z;
		else
			if( this.cmp.compare(z.cle, y.cle) < 0) y.gauche = z;
			else                                    y.droit  = z;
		
		z.gauche = z.droit = null;

		return true;
    
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		for( E e : c )
			if( !this.add(e) ) return false;
		
		return true;
	}
	
	@Override
    public boolean remove(Object o)
    {
        if( o instanceof Collection ) return this.removeAll((Collection<?>) o);

        if( o.getClass().equals(Noeud.class) ) return this.supprimer((Noeud) o) != null;
        if( o.getClass().equals(this.racine.cle.getClass())) return this.supprimer((E)o) != null;

        return false;
    }
}