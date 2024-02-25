package codyhuh.ambientadditions.client.model;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.common.items.YetiFeedersItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class YetiFeedersModel extends GeoModel<YetiFeedersItem> {

	@Override
	public ResourceLocation getModelResource(YetiFeedersItem object) {
		return new ResourceLocation(AmbientAdditions.MOD_ID, "geo/armor/yeti_feeders.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(YetiFeedersItem object) {
		return new ResourceLocation(AmbientAdditions.MOD_ID, "textures/armor/yeti_feeders_layer_1.png");
	}

	@Override
	public ResourceLocation getAnimationResource(YetiFeedersItem object) {
		return null;
	}
}