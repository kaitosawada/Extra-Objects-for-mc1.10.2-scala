package com.mito.exobj.lib.editor;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockObjects extends BlockContainer {

	public BlockObjects() {
		super(Material.CLOTH);/*
		this.setSoundType(Block.soundTypeCloth);
		this.bou(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);*/
		// this.setBlockTextureName("iron_ore");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int a) {
		TileObjects tile = new TileObjects();
		return tile;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile != null && tile instanceof TileObjects) {
			TileObjects t = (TileObjects) tile;
			t.breakBrace();
		}
		worldIn.removeTileEntity(pos);
	}

}
