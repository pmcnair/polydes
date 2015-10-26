package com.polydes.dialog.updates;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.polydes.common.ext.ExtensionInterface;
import com.polydes.common.util.Lang;
import com.polydes.datastruct.DataStructuresExtension;
import com.polydes.datastruct.data.folder.DataItem;
import com.polydes.datastruct.data.folder.Folder;
import com.polydes.datastruct.data.structure.Structure;
import com.polydes.datastruct.data.structure.StructureDefinition;
import com.polydes.datastruct.data.structure.StructureDefinitions;
import com.polydes.datastruct.data.structure.StructureFolder;
import com.polydes.datastruct.data.structure.Structures;
import com.polydes.datastruct.data.structure.elements.StructureField;
import com.polydes.datastruct.updates.TypenameUpdater;

import stencyl.core.lib.Game;

public class V6_ExtensionSubmodules implements Runnable
{
	@Override
	public void run()
	{
		ExtensionInterface.doUponAllLoaded(new String[] {"com.polydes.dialog", "com.polydes.datastruct"}, () ->
		{
			Structure log = make("dialog.ds.ext.Logic", "Dialog/Plugins/Logic");
			Structure ms = make("dialog.ds.ext.MessagingScripts", "Dialog/Plugins/Messaging Scripts");
			Structure ss = make("dialog.ds.ext.SoundScripts", "Dialog/Plugins/Sound Scripts");
			
			StructureDefinition unknownDef = StructureDefinitions.defMap.get("Style");
			for(Structure style : Structures.structures.get(unknownDef))
			{
				String prefix = "Dialog/Plugins/" + style.dref.getName() + " ";
				Structure db = make("dialog.ds.ext.DialogBase", prefix + "Dialog Base");
				Structure ts = make("dialog.ds.ext.TypingScripts", prefix + "Typing Scripts");
				Structure eg = make("dialog.ds.ext.ExtraGlyphs", prefix + "Extra Glyphs");
				Structure cs = make("dialog.ds.ext.CharacterScripts", prefix + "Character Scripts");
				Structure sks = make("dialog.ds.ext.SkipScripts", prefix + "Skip Scripts");
				Structure fs = make("dialog.ds.ext.FlowScripts", prefix + "Flow Scripts");
				Structure te = make("dialog.ds.ext.TextEffects", prefix + "Text Effects");
				Structure dop = make("dialog.ds.ext.DialogOptions", prefix + "Dialog Options");
				
				Map<String,String> map = style.getUnknownData();
				String[] parts;
				String newValue;
				
				for (String prop : new String[] {"msgWindow",
					"fitMsgToWindow", "msgBounds", "msgFont",
					"msgTypeSpeed", "msgStartSound",
					"controlAttribute", "lineSpacing",
					"charSpacing", "clearSound", "closeSound",
					"endSound" })
					set(db, prop, map.remove(prop));
			
				set(ts, "defaultRandomTypeSounds", stringArrayToSoundArray(map.remove("defaultRandomTypeSounds")));
				set(ts, "characterSkipSFX", map.remove("characterSkipSFX"));
				set(ts, "playTypeSoundOnSpaces", map.remove("playTypeSoundOnSpaces"));
				
				set(eg, "glyphPadding", map.remove("glyphPadding"));

				for (String prop : new String[] { "nameboxWindow",
					"nameboxFont", "faceImagePrefix",
					"faceRelation" })
					set(cs, prop, map.remove(prop));
				set(cs, "faceOrigin", convertRatioPoint(map.remove("faceOrigin")));
				set(cs, "facePos", convertRatioPoint(map.remove("facePos")));
				parts = map.remove("faceMsgOffset").replaceAll("\\[|\\]| ", "").split(",");
				newValue = String.format("[%s,-%s,-%s,%s]", parts[1], parts[2], parts[3], parts[0]);
				set(cs, "faceMsgOffset", newValue.replaceAll("--", ""));
				
				for (String prop : new String[] { "fastSpeed",
					"fastButton", "fastSoundInterval", "zoomSpeed",
					"zoomButton", "zoomSoundInterval",
					"instantButton", "instantSound",
					"skippableDefault" })
					set(sks, prop, map.remove(prop));
				set(sks, "fastSound", stringArrayToSoundArray(map.remove("fastSound")));
				set(sks, "zoomSound", stringArrayToSoundArray(map.remove("zoomSound")));
				
				for (String prop : new String[] { "advanceDialogButton",
					"waitingSound", "waitingSoundInterval",
					"inputSound", "noInputSoundWithTags" })
					set(fs, prop, map.remove(prop));
				set(fs, "animForPointer", convertAnimation(map.remove("animForPointer")));
				set(fs, "pointerPos", convertRatioPoint(map.remove("pointerPos")));
				
				for (String prop : new String[] { "v_maxShakeOffsetX",
					"v_maxShakeOffsetY", "v_shakeFrequency",
					"s_magnitude", "s_frequency", "s_pattern",
					"r_diameter", "r_frequency", "r_pattern",
					"g_start", "g_stop", "g_duration" })
					set(te, prop, map.remove(prop));
				
				for(Entry<Object, Object> entry : Lang.hashmap(
					"optWindow", "windowTemplate",
					"optWindowFont", "windowFont",
					"optCursorType", "cursorType",
					"optCursorImage", "cursorImage",
					"optCursorOffset", "cursorOffset",
					"optCursorWindow", "cursorWindow",
					"optChoiceLayout", "choiceLayout",
					"optSelectButton", "selectButton",
					"optScrollWait", "scrollWait",
					"optScrollDuration", "scrollDuration",
					"optAppearSound", "appearSound",
					"optChangeSound", "changeSound",
					"optConfirmSound", "confirmSound",
					"optItemPadding", "itemPadding",
					"optInactiveTime", "inactiveTime").entrySet())
					set(dop, (String) entry.getValue(), map.remove((String) entry.getKey()));
				
				String extensionList =
					StringUtils.join(
						Lang.mapCA
						(
							Lang.arraylist(db,ts,eg,cs,sks,fs,te,dop,log,ms,ss),
							Integer.class,
							(struct) -> ((Structure) struct).getID()
						),
						","
					);
				extensionList = "[" + extensionList + "]:dialog.ds.DialogExtension";
//				map.put("extensions", extensionList);
			}
			
			unknownDef = StructureDefinitions.defMap.get("ScalingImage");
			for(Structure scalingImage : Structures.structures.get(unknownDef))
			{
				Map<String,String> map = scalingImage.getUnknownData();
				map.put("origin", convertRatioPoint(map.get("origin")));
				
				System.out.println(scalingImage.dref.getName());
				System.out.println(map);
			}
			
			unknownDef = StructureDefinitions.defMap.get("Tween");
			for(Structure tween : Structures.structures.get(unknownDef))
			{
				Map<String,String> map = tween.getUnknownData();
				map.put("positionStart", convertRatioPoint(map.get("positionStart")));
				map.put("positionStop", convertRatioPoint(map.get("positionStop")));
				
				System.out.println(tween.dref.getName());
				System.out.println(map);
			}
			
			unknownDef = StructureDefinitions.defMap.get("Window");
			for(Structure window : Structures.structures.get(unknownDef))
			{
				Map<String,String> map = window.getUnknownData();
				map.put("position", convertRatioPoint(map.get("position")));
				map.put("scaleWidthSize", convertRatioInt(map.get("scaleWidthSize")));
				map.put("scaleHeightSize", convertRatioInt(map.get("scaleHeightSize")));
				
				String[] parts = map.remove("insets").replaceAll("\\[|\\]| ", "").split(",");
				map.put("insets", String.format("[%s,%s,%s,%s]", parts[1], parts[2], parts[3], parts[0]));
				
				System.out.println(window.dref.getName());
				System.out.println(map);
			}
			
			TypenameUpdater tu = new TypenameUpdater();
			tu.addTypes(Lang.hashmap(
				"Animation", "dialog.core.Animation",
				"RatioInt", "dialog.geom.RatioInt",
				"RatioPoint", "dialog.geom.RatioPoint",
				"Point", "openfl.geom.Point",
				"Rectangle", "openfl.geom.Rectangle",
				"Window", "dialog.ds.WindowTemplate",
				"Tween", "dialog.ds.TweenTemplate",
				"Style", "dialog.ds.Style",
				"ScalingImage", "dialog.ds.ScalingImageTemplate"
			));
			tu.convert();
			
			DataStructuresExtension.forceUpdateData = true;
			DataStructuresExtension.get().onGameSave(Game.getGame());
		});
	}
	
	private void set(Structure s, String field, String value)
	{
		System.out.println(s.dref.getName() + ":" + field + "=" + value);
		StructureField f = s.getTemplate().getField(field);
		s.setPropertyFromString(f, value);
		s.setPropertyEnabled(f, !f.isOptional() || s.getProperty(f) != null);
	}
	
	private Structure make(String def, String path)
	{
		String[] pathParts = path.split("/");
		String name = pathParts[pathParts.length - 1];
		pathParts = ArrayUtils.remove(pathParts, pathParts.length - 1);
		
		int id = Structures.newID();
		StructureDefinition type = StructureDefinitions.defMap.get(def);
		Structure toReturn = new Structure(id, name, type);
		toReturn.loadDefaults();
		Structures.structures.get(type).add(toReturn);
		Structures.structuresByID.put(id, toReturn);
		
		DataItem item = Structures.root;
		for(String pathPart : pathParts)
		{
			Folder f = (Folder) item;
			if(f.getItemByName(pathPart) == null)
				f.addItem(new StructureFolder(pathPart));
			
			item = (DataItem) f.getItemByName(pathPart);
		}
		((Folder) item).addItem(toReturn.dref);
		
		return toReturn;
	}
	
	private String convertAnimation(String s)
	{
		return s == null ? null : "[" + s.replace("-", ",") + "]";
	}
	
	private String convertRatioInt(String s)
	{
		return s == null ? null : "[" + s + "]";
	}
	
	private String convertRatioPoint(String s)
	{
		return s == null ? null : s.replaceAll("([^,]+)", "\\[$0\\]");
	}
	
	private String simplifyArray(String s)
	{
		return s == null ? null : s.replaceAll(":[^,\\]]+", "").replaceAll("\\[|\\]", "");
	}
	
	private String stringArrayToSoundArray(String s)
	{
		if(s == null)
			return null;
		s = simplifyArray(s);
		String[] ids = s.split(",");
		for(int i = 0; i < ids.length; ++i)
			ids[i] = String.valueOf(Game.getGame().getResources().getResourceWithName(s).getID());
		return "[" + StringUtils.join(ids, ",") + "]:com.stencyl.models.Sound";
	}
}
