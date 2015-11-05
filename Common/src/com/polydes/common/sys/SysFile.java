package com.polydes.common.sys;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.polydes.common.nodes.Leaf;
import com.polydes.common.nodes.LeafListener;
import com.polydes.common.ui.object.ViewableObject;

public class SysFile implements Leaf<SysFile,SysFolder>, ViewableObject
{
	protected File file;
	protected String path;
	protected int hash;
	
	protected ArrayList<LeafListener<SysFile,SysFolder>> listeners;
	protected SysFolder parent;
	
	protected String name;
	
	public SysFile(File file)
	{
		this.file = file;
		path = file.getAbsolutePath().intern();
		hash = path.hashCode();
		
		listeners = new ArrayList<LeafListener<SysFile,SysFolder>>();
		parent = null;
	}
	
	@Override
	public void setParent(SysFolder parent, boolean addToParent)
	{
		if(this.parent == parent)
			return;
		
		if(parent == null && !addToParent)
		{
			this.parent = null;
			return;
		}
		
		if(this.parent != null)
			this.parent.removeItem(this);
		
		this.parent = parent;
		if(addToParent)
			parent.addItem(this);
	}
	
	public File getFile()
	{
		return file;
	}

	@Override
	public SysFolder getParent()
	{
		return parent;
	}

	@Override
	public void addListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.add(l);
	}

	@Override
	public void removeListener(LeafListener<SysFile,SysFolder> l)
	{
		listeners.remove(l);
	}

	@Override
	public void setName(String newName)
	{
		if(name != newName)
		{
			String oldName = name;
			
			if(file.renameTo(new File(file.getParentFile(), newName)))
			{
				name = newName;
				for(LeafListener<SysFile,SysFolder> l : listeners) {l.leafNameChanged(this, oldName);}
				
				if(parent != null)
					parent.registerNameChange(oldName, name);
			}
		}
	}
	
	@Override
	public String getName()
	{
		return file.getName();
	}

	@Override
	public boolean canEditName()
	{
		return true;
	}
	
	public void clearIcon()
	{
		cachedIcon = null;
	}

	public void notifyChanged()
	{
		for(LeafListener<SysFile,SysFolder> l : listeners)
			l.leafStateChanged(this);
		
		//TODO reimplement this in ExtrasManager
//		if(FilePreviewer.getPreviewFile() == this)
//			FilePreviewer.preview(this);
//		FileRenderer.clearThumbnail(file);
	}
	
	private ImageIcon cachedIcon = null;
	
	@Override
	public ImageIcon getIcon()
	{
		if(cachedIcon == null)
			cachedIcon = FileRenderer.generateThumb(file);
		
		return cachedIcon;
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof SysFile)
			return path == ((SysFile) obj).path;
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}

	protected JPanel view = null;
	
	@Override
	public JPanel getView()
	{
		if(view == null)
			view = FilePreviewer.getPreview(this);
		
		return view;
	}

	@Override
	public void disposeView()
	{
		view = null;
	}

	@Override
	public boolean fillsViewHorizontally()
	{
		return false;
	}
}