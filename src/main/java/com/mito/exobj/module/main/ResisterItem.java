package com.mito.exobj.module.main;

import com.mito.exobj.lib.ExtraObjectRegistry;
import com.mito.exobj.lib.editor.TileObjectsRenderer;
import com.mito.exobj.lib.editor.BlockObjects;
import com.mito.exobj.lib.editor.TileObjects;
import com.mito.exobj.lib.item.ItemBar;
import com.mito.exobj.lib.render.exorender.RenderObject;
import com.mito.exobj.module.exobject.Brace;
import com.mito.exobj.module.exobject.GuideBrace;
import com.mito.exobj.module.exobject.Tofu;
import com.mito.exobj.module.exobject.Wall;
import com.mito.exobj.module.item.*;
import com.mito.exobj.lib.item.ItemSelectTool;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class ResisterItem {

	public static Item ItemBrace;
	public static Item ItemBar;
	public static Item ItemBender;
	public static Item ItemWall;
	public static Item ItemRuler;
	//public static Item ItemLinearMotor;
	public static Item ItemBlockSetter;
	public static Item ItemSelectTool;
	public static Item ItemFakeBlock;
	//public static Item ItemRedCable;
	public static Item ItemTofu;

	public static Block BlockObjects;


	static public void preinit(FMLPreInitializationEvent event) {

		ExtraObjectRegistry.addMapping(Brace.class, "Brace", new RenderObject());
		ExtraObjectRegistry.addMapping(GuideBrace.class, "GuideBrace", new RenderObject());
		ExtraObjectRegistry.addMapping(Tofu.class, "Tofu", new RenderObject());
		ExtraObjectRegistry.addMapping(Wall.class, "Wall", new RenderObject());

		//ItemFakeBlock = new ItemFakeBlock().setUnlocalizedName("ItemFakeBlock");
		ItemSelectTool = new ItemSelectTool().setUnlocalizedName("ItemSelectTool").setCreativeTab(Main.tab);
		ItemBrace = new ItemBrace().setUnlocalizedName("ItemBrace").setCreativeTab(Main.tab);
		ItemBender = new ItemBender().setUnlocalizedName("ItemBender").setCreativeTab(Main.tab);
		ItemBar = new ItemBar().setUnlocalizedName("ItemBar").setCreativeTab(Main.tab);
		//ItemBlockSetter = new ItemBlockSetter().setUnlocalizedName("ItemBlockSetter").setCreativeTab(Main.tab);
		ItemWall = new ItemWall().setUnlocalizedName("ItemWall").setCreativeTab(Main.tab);
		ItemRuler = new ItemRuler().setUnlocalizedName("ItemRuler").setCreativeTab(Main.tab);
		ItemTofu = new ItemTofu().setUnlocalizedName("ItemTofu").setCreativeTab(Main.tab);

		BlockObjects = new BlockObjects().setUnlocalizedName("BlockObjects");
		ItemBlock ItemObjects = new ItemBlock(BlockObjects);

		GameRegistry.register(ItemBar, new ResourceLocation(Main.MODID, "ItemBar"));
		GameRegistry.register(ItemBender, new ResourceLocation(Main.MODID, "ItemBender"));
		GameRegistry.register(ItemRuler, new ResourceLocation(Main.MODID, "ItemRuler"));
		GameRegistry.register(ItemSelectTool, new ResourceLocation(Main.MODID, "ItemSelectTool"));
		GameRegistry.register(ItemBrace, new ResourceLocation(Main.MODID, "ItemBrace"));
		GameRegistry.register(ItemTofu, new ResourceLocation(Main.MODID, "ItemTofu"));
		GameRegistry.register(ItemWall, new ResourceLocation(Main.MODID, "ItemWall"));

		GameRegistry.register(BlockObjects, new ResourceLocation(Main.MODID, "BlockObjects"));
		GameRegistry.register(ItemObjects, new ResourceLocation(Main.MODID, "BlockObjects"));


		if(event.getSide().isClient()){
			ModelLoader.setCustomModelResourceLocation(ItemBar, 0, new ModelResourceLocation(ItemBar.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemBender, 0, new ModelResourceLocation(ItemBender.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemRuler, 0, new ModelResourceLocation(ItemRuler.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemSelectTool, 0, new ModelResourceLocation(ItemSelectTool.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemBrace, 0, new ModelResourceLocation(ItemBrace.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemTofu, 0, new ModelResourceLocation(ItemTofu.getRegistryName(), "inventory"));
			ModelLoader.setCustomModelResourceLocation(ItemWall, 0, new ModelResourceLocation(ItemWall.getRegistryName(), "inventory"));

			ModelLoader.setCustomModelResourceLocation(ItemObjects, 0, new ModelResourceLocation(new ResourceLocation(Main.MODID, "BlockObjects"), "inventory"));

			ClientRegistry.registerTileEntity(TileObjects.class, "TileObjects", new TileObjectsRenderer());
		} else {
			GameRegistry.registerTileEntity(TileObjects.class, "TileObjects");
		}
	}

	static public void RegisterRecipe() {

		RecipeSorter.register(Main.MODID + ";shapeless", MitoShapelessRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

		GameRegistry.addRecipe(new MitoShapedRecipe());
		GameRegistry.addRecipe(new MitoShapelessRecipe());

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBrace, 4, 0),
				"#  ",
				" # ",
				"  #",
				'#', Blocks.IRON_BARS);

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBender),
				" # ",
				" # ",
				"B B",
				'#', Items.IRON_INGOT,
				'B', ResisterItem.ItemBar);

		GameRegistry.addRecipe(new ItemStack(ResisterItem.ItemBar),
				"I  ",
				" I ",
				"  B",
				'B', Blocks.IRON_BARS,
				'I', Items.IRON_INGOT);
	}

}
