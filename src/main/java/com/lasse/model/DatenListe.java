package com.lasse.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Liste Daten
 * @param <T>
 */
public class DatenListe<T extends Daten> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5915504726543454318L;
	public final static int MAXNR = 99999999;
	protected HashMap<Integer, Integer> dataN = new HashMap<Integer, Integer>();
	ArrayList<T> daten = new ArrayList<T>();

	// HashMaps neu aufbauen:
	public void init() {
		dataN.clear();
		for (int i = 0; i < size(); i++) {
			T m = get(i);
			dataN.put(m.getNummer(), i);
		}
	}

	/**
	 * Neue Ident-Nummer für Dekoder ermitteln
	 */
	public int getNewDatenNr() {
		boolean vorhanden = false;
		for (int nr = 1; nr < MAXNR; nr++) {
			vorhanden = false;
			for (int i = 0; i < size(); i++) {
				Daten ma = get(i);
				if (ma.getNummer() == nr)
					vorhanden = true;
			}
			if (vorhanden == false)
				return nr;
		}
		if (vorhanden)
			return 0;
		else
			return 1;
	}

	// Dekoder über Ident-Nr holen:
	public T getDekoderByNr(int nr) {
		T d = null;
		Integer index = (Integer) dataN.get(new Integer(nr));
		if (index != null)
			d = get(index.intValue());
		return d;
	}

	// Index über Ident-Nr holen:
	public int getIndexByNr(int nr) {
		Integer index = (Integer) dataN.get(new Integer(nr));
		if (index != null)
			return index.intValue();
		else
			return -1;
	}

	public T get(int i) {
		return daten.get(i);
	}

	public int size() {
		return daten.size();
	}

	public T remove(T d) {
		int i = getIndexByNr(d.getNummer());
		if (i >= 0) {
			return remove(i);
		} else {
			return null;
		}
	}

	public T remove(int i) {
		T d = daten.remove(i);
		init();
		return d;
	}
	
	public void clear() {
		daten.clear();
		init();
	}
	
	public boolean add(T e) {
		boolean erg = daten.add(e);
		init();
		return erg;
	}
	
	public ArrayList<T> getDaten(){
		return daten;
	}
}
