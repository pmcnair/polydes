package com.polydes.common.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.polydes.common.util.Lang;

public class NodeUtils
{
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void recursiveRun(T item, LeafRunnable<T,U> runnable)
	{
		runnable.run(item);
		if(item instanceof Branch)
			for(T curItem : ((U) item).getItems())
				recursiveRun(curItem, runnable);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void recursiveRunPost(T item, LeafRunnable<T,U> runnable)
	{
		if(item instanceof Branch)
			for(T curItem : ((U) item).getItems())
				recursiveRun(curItem, runnable);
		runnable.run(item);
	}
	
	public static interface LeafRunnable<T extends Leaf<T,U>, U extends Branch<T,U>>
	{
		public void run(T item);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> boolean isDescendantOf(T item, U parent)
	{
		while((item = (T) (item.getParent())) != null)
			if(item == parent)
				return true;
		
		return false;
	}
	
	/**
	 * null -> 0<br/>
	 * root -> 1
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> int getDepth(T l)
	{
		int d = 0;
		while(l != null)
		{
			l = (T) l.getParent();
			++d;
		}
		return d;
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void removeNodesWithContainedParents(Collection<T> list)
	{
		HashSet<T> nodeSet = new HashSet<T>(list);
		HashSet<T> toRemove = new HashSet<T>();
		
		nodeIter:
		for(T node : list)
		{
			U parent = node.getParent();
			while(parent != null)
			{
				if(nodeSet.contains(parent))
				{
					toRemove.add(node);
					continue nodeIter;
				}
				parent = parent.getParent();
			}
		}
		
		for(T node : toRemove)
			list.remove(node);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void includeDescendants(Collection<T> list)
	{
		final HashSet<T> set = new HashSet<T>();
		for(T node : list)
			NodeUtils.recursiveRun(node, (T item) -> {
				set.add(item);
			});
		list.clear();
		list.addAll(set);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> void depthSort(List<T> list)
	{
		Collections.sort(list, (a, b) -> Integer.compare(getDepth(b), getDepth(a)));
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> Object[] getPath(U parent, T child)
	{
		ArrayList<T> lookup = new ArrayList<>();
		lookup.add(child);
		
		while(child != parent)
			lookup.add(child = (T) child.getParent());
		Collections.reverse(lookup);
		
		return lookup.toArray();
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> int[] getIndexPath(U parent, T child)
	{
		ArrayList<T> lookup = new ArrayList<>();
		lookup.add(child);
		
		while(child != parent)
			lookup.add(child = (T) child.getParent());
		
		int[] path = new int[lookup.size()];
		path[0] = 0;
		for(int i = 0; i < path.length - 1; ++i)
			path[i + 1] = ((U) lookup.get(path.length - i - 1)).indexOfItem(lookup.get(path.length - i - 2));
		
		return path;
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> int getIndex(T child)
	{
		if(child.getParent() == null)
			return 0;
		return child.getParent().getItems().indexOf(child);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> T getNextSibling(T child)
	{
		U parent = child.getParent();
		int i = parent.getItems().indexOf(child);
		if(i == parent.getItems().size() - 1)
			return null;
		
		return parent.getItemAt(i + 1);
	}
	
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> T getPreviousSibling(T child)
	{
		U parent = child.getParent();
		int i = parent.getItems().indexOf(child);
		if(i == 0)
			return null;
		
		return parent.getItemAt(i - 1);
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends Leaf<T,U>, U extends Branch<T,U>> U getRoot(T child)
	{
		while(child.getParent() != null)
			child = (T) child.getParent();
		return (U) child;
	}
	
	public static final void print(Collection<? extends Leaf<?,?>> c)
	{
		System.out.println(StringUtils.join(Lang.mapCA(c, String.class, i -> i.getName()), ','));
	}
}
