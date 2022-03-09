package coda.ambientadditions.client.geo.renderer;

import coda.ambientadditions.client.geo.model.ChocolateChipStarfishModel;
import coda.ambientadditions.common.entities.ChocolateChipStarfishEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChocolateChipStarfishRenderer extends GeoEntityRenderer<ChocolateChipStarfishEntity> {

    public ChocolateChipStarfishRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ChocolateChipStarfishModel());
    }
}