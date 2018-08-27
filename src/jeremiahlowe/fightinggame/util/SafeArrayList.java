package jeremiahlowe.fightinggame.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class SafeArrayList<T> implements Iterable<T> {
	private ArrayList<T> base, toRemove, toAdd;
	private Comparator<T> sorter;

	public SafeArrayList() {
		this(null);
	}
	public SafeArrayList(Comparator<T> sorter) {
		base = new ArrayList<T>();
		toRemove = new ArrayList<T>();
		toAdd = new ArrayList<T>();
		this.sorter = sorter;
	}

	public boolean add(T obj) {
		return toAdd.add(obj);
	}
	public boolean addAll(Collection<? extends T> c) {
		return toAdd.addAll(c);
	}
	public void clear() {
		for (T t : base)
			toRemove.add(t);
	}
	public void clearAddQueue() {
		toAdd.clear();
	}
	public void clearRemoveQueue() {
		toRemove.clear();
	}
	public void clearBase() {
		base.clear();
	}
	public boolean addContains(T obj) {
		return toAdd.contains(obj);
	}
	public boolean removeContains(T obj) {
		return toRemove.contains(obj);
	}
	public boolean baseContains(T obj) {
		return base.contains(obj);
	}
	public boolean contains(T obj) {
		return baseContains(obj) || addContains(obj);
	}
	public boolean remove(T obj) {
		return toRemove.add(obj);
	}
	public boolean removeAll(Collection<? extends T> c) {
		return toRemove.addAll(c);
	}
	public boolean isEmpty() {
		return base.isEmpty();
	}
	public boolean isAddQueueEmpty() {
		return toAdd.isEmpty();
	}
	public boolean isRemoveQueueEmpty() {
		return toRemove.isEmpty();
	}
	public boolean isFullyEmpty() {
		return toAdd.isEmpty() && toRemove.isEmpty() && base.isEmpty();
	}
	public boolean sorted() {
		if (sorter == null)
			return false;
		T l = null;
		for (T t : base) {
			if (t != null && l != null)
				if (sorter.compare(t, l) < 0)
					return false;
			l = t;
		}
		return true;
	}
	public Iterator<T> iterator() {
		return base.iterator();
	}
	public int size() {
		return base.size();
	}
	public int addQueueSize() {
		return toAdd.size();
	}
	public int removeQueueSize() {
		return toRemove.size();
	}
	public void update() {
		for (T t : toAdd) {
			int index = 0;
			if (sorter != null) {
				for (int i = 0; i < base.size(); i++) {
					T t2 = base.get(i);
					if (t != null && t2 != null)
						if (sorter.compare(t, t2) > 0)
							index = i;
				}
			}
			base.add(index, t);
		}
		for (T t : toRemove)
			base.remove(t);
		toAdd.clear();
		toRemove.clear();
	}
	public void setSorting(Comparator<T> sorter) {
		this.sorter = sorter;
	}
	public void sort() {
		for (T t : base)
			toAdd.add(t);
		base.clear();
		update();
	}
	public T get(int i) {
		return base.get(i);
	}
}
