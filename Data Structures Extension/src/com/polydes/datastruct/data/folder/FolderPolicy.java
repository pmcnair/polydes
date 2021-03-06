package com.polydes.datastruct.data.folder;

import com.polydes.common.nodes.DefaultLeaf;

public class FolderPolicy
{
	public boolean duplicateItemNamesAllowed;
	public boolean itemCreationEnabled;
	public boolean itemRemovalEnabled;
	public boolean itemEditingEnabled;
	public boolean folderCreationEnabled;
	
	public boolean canAcceptItem(Folder folder, DefaultLeaf item)
	{
		return duplicateItemNamesAllowed || folder.getItemByName(item.getName()) == null;
	}
	
	public boolean canCreateItemWithName(Folder folder, String itemName)
	{
		return duplicateItemNamesAllowed || folder.getItemByName(itemName) == null;
	}
}
