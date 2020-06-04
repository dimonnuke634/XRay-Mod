package com.xray.xray;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.xray.Configuration;
import com.xray.XRay;
import com.xray.utils.RenderBlockProps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Render
{
    public static List<RenderBlockProps> syncRenderList = Collections.synchronizedList( new ArrayList<>() ); // this is accessed by threads

	static void renderBlocks(RenderWorldLastEvent event) {
        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = bufferSource.getBuffer(ModRenderTypes.OVERLAY_LINES);
	    Vec3d view = XRay.mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        MatrixStack stack = event.getMatrixStack();
        stack.push();
        stack.translate(-view.x, -view.y, -view.z); // translate

        syncRenderList.forEach(blockProps -> {
            renderBlockBounding(stack.getLast().getMatrix(), builder, blockProps);
        });

        stack.pop();
        RenderSystem.disableDepthTest();
        bufferSource.finish(ModRenderTypes.OVERLAY_LINES);
    }

    private static void renderBlockBounding(Matrix4f matrix4f, IVertexBuilder builder, RenderBlockProps b) {
        if( b == null )
            return;

        final float size = 1.0f;
        final int x = b.getX(), y = b.getY(), z = b.getZ(), opacity = 1;

        final float red = (b.getColor() >> 16 & 0xff) / 255f;
        final float green = (b.getColor() >> 8 & 0xff) / 255f;
        final float blue = (b.getColor() & 0xff) / 255f;

        builder.pos(matrix4f, x, y + size, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y + size, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y + size, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y + size, z).color(red, green, blue, opacity).endVertex();

        // BOTTOM
        builder.pos(matrix4f, x + size, y, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y, z).color(red, green, blue, opacity).endVertex();

        // Edge 1
        builder.pos(matrix4f, x + size, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z + size).color(red, green, blue, opacity).endVertex();

        // Edge 2
        builder.pos(matrix4f, x + size, y, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x + size, y + size, z).color(red, green, blue, opacity).endVertex();

        // Edge 3
        builder.pos(matrix4f, x, y, z + size).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y + size, z + size).color(red, green, blue, opacity).endVertex();

        // Edge 4
        builder.pos(matrix4f, x, y, z).color(red, green, blue, opacity).endVertex();
        builder.pos(matrix4f, x, y + size, z).color(red, green, blue, opacity).endVertex();
    }
}