package com.polydes.common.data.types;

import javax.swing.JComponent;

import org.apache.commons.lang3.ArrayUtils;

/**
 * <p>DataEditors often install listeners and hold references,
 * so always call {@code dispose()} when finished with a
 * DataEditor.</p>
 * 
 * To extend DataEditor, generally:													<br />
 * 1) An editor should be created.													<br />
 * 2) When that editor's value changes, call {@code updated()}.						<br />
 * 3) Extends get and set value to work with that editor.							<br />
 * 4) Return a list of the components for your editor in {@code getComponents()}.	<br />
 */
public abstract class DataEditor<T>
{
	private UpdateListener[] listeners;
	private DisposeListener[] disposeListeners;
	private boolean quiet;
	
	public abstract T getValue();
	public final void setValue(T t)
	{
		quiet = true;
		set(t);
		quiet = false;
		updated();
	}
	@SuppressWarnings("unchecked")
	public final void setValueUnchecked(Object o)
	{
		quiet = true;
		set((T) o);
		quiet = false;
		updated();
	}
	protected abstract void set(T t);
	
	public final void updated()
	{
		if(!quiet && listeners != null)
			for(UpdateListener l : listeners)
				l.updated();
	}
	
	public final void addListener(UpdateListener l)
	{
		if(listeners == null)
			listeners = new UpdateListener[] {l};
		else
			listeners = ArrayUtils.add(listeners, l);
	}
	
	public final void removeListener(UpdateListener l)
	{
		if(listeners != null)
			listeners = ArrayUtils.removeElement(listeners, l);
	}
	
	public final void addDisposeListener(DisposeListener l)
	{
		if(disposeListeners == null)
			disposeListeners = new DisposeListener[] {l};
		else
			disposeListeners = ArrayUtils.add(disposeListeners, l);
	}
	
	/**
	 * Subclasses MUST call super.dispose() when extending this method.
	 */
	public void dispose()
	{
		if(disposeListeners != null)
			for(DisposeListener l : disposeListeners)
				l.disposed();
		listeners = null;
		disposeListeners = null;
	}
	
	/**
	 * An array of components is returned so that they can be laid out as desired
	 * in the UI by the caller. An array of the same objects should be returned every
	 * time this method is called.
	 */
	public abstract JComponent[] getComponents();
}