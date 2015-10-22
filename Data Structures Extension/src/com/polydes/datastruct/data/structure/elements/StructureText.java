package com.polydes.datastruct.data.structure.elements;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitionElement;
import com.polydes.datastruct.data.structure.StructureDefinitionElementType;
import com.polydes.datastruct.io.XML;
import com.polydes.datastruct.res.Resources;
import com.polydes.datastruct.ui.objeditors.StructureTextPanel;
import com.polydes.datastruct.ui.table.GuiObject;
import com.polydes.datastruct.ui.table.PropertiesSheet;
import com.polydes.datastruct.ui.table.PropertiesSheetStyle;
import com.polydes.datastruct.ui.table.Row;
import com.polydes.datastruct.ui.table.RowGroup;

public class StructureText extends StructureDefinitionElement
{
	private String label;
	private String text;
	
	public StructureText(String label, String text)
	{
		this.label = label;
		this.text = text;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getLabel()
	{
		return label;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public String toString()
	{
		return label;
	}
	
	private StructureTextPanel editor;
	
	@Override
	public JPanel getEditor()
	{
		if(editor == null)
			editor = new StructureTextPanel(this, PropertiesSheetStyle.LIGHT);
		
		return editor;
	}
	
	@Override
	public void disposeEditor()
	{
		editor.dispose();
		editor = null;
	}
	
	@Override
	public void revertChanges()
	{
		editor.revert();
	}

	@Override
	public String getDisplayLabel()
	{
		return label;
	}
	
	public static class TextType extends StructureDefinitionElementType<StructureText>
	{
		public TextType()
		{
			sdeClass = StructureText.class;
			tag = "text";
			isBranchNode = false;
			icon = Resources.thumb("text.png", 16);
			childTypes = null;
		}
		
		@Override
		public StructureText read(StructureDefinition model, Element e)
		{
			return new StructureText(XML.read(e, "label"), XML.read(e, "text"));
		}

		@Override
		public Element write(StructureText object, Document doc)
		{
			Element e = doc.createElement("text");
			e.setAttribute("label", StringEscapeUtils.escapeXml10(object.getLabel()));
			e.setAttribute("text", StringEscapeUtils.escapeXml10(object.getText()));
			return e;
		}
		
		@Override
		public StructureText create(StructureDefinition def, String nodeName)
		{
			return new StructureText(nodeName, "");
		}

		@Override
		public void psLoad(PropertiesSheet sheet, RowGroup group, DataItem node, StructureText value)
		{
			group.add(sheet.style.createLabel(value.getLabel()), sheet.style.createDescriptionRow(value.getText()));
			group.add(sheet.style.rowgap);
		}

		@Override
		public void psLightRefresh(PropertiesSheet sheet, GuiObject gui, DataItem node, StructureText value)
		{
			Row r = ((RowGroup) gui).rows[0];
			((JLabel) r.components[0]).setText(value.getLabel());
			((JLabel) r.components[1]).setText(value.getText());
		}
	}
}
