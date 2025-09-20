package codyhuh.ambientadditions.client;

import codyhuh.ambientadditions.AmbientAdditions;
import codyhuh.ambientadditions.client.model.GenericGeoModel;
import codyhuh.ambientadditions.client.model.TextureVariantModel;
import codyhuh.ambientadditions.client.particles.StunParticle;
import codyhuh.ambientadditions.client.particles.ZzzParticle;
import codyhuh.ambientadditions.client.renderer.GenericGeoRenderer;
import codyhuh.ambientadditions.client.renderer.MataMataRenderer;
import codyhuh.ambientadditions.client.renderer.item.DartRenderer;
import codyhuh.ambientadditions.client.renderer.layer.AAGlowingEyesLayer;
import codyhuh.ambientadditions.client.renderer.layer.CardiganCorgiCollarLayer;
import codyhuh.ambientadditions.client.renderer.layer.ChameleonBrightnessLayer;
import codyhuh.ambientadditions.client.renderer.layer.PembrokeCorgiCollarLayer;
import codyhuh.ambientadditions.common.entities.*;
import codyhuh.ambientadditions.common.items.CrateItem;
import codyhuh.ambientadditions.registry.AAEntities;
import codyhuh.ambientadditions.registry.AAItems;
import codyhuh.ambientadditions.registry.AAParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = AmbientAdditions.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static void make(EntityType type, String name){
        EntityRenderers.register(type, (ctx) -> new GenericGeoRenderer<>(ctx, () -> new GenericGeoModel<>(name)));
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        EntityType<?>[] simpleEntities = new EntityType[]{
                AAEntities.GIANT_LAND_SNAIL.get(), AAEntities.LONGHORN_COWFISH.get(), AAEntities.NINE_BANDED_ARMADILLO.get(),
                AAEntities.PINK_FAIRY_ARMADILLO.get(), AAEntities.MOUSTACHED_TAMARIN.get(), AAEntities.IIWI.get(), AAEntities.PINOCCHIO_ANOLE.get(),
                AAEntities.SPIDER_TAILED_ADDER.get(), AAEntities.RABBIT_SNAIL.get(), AAEntities.RING_TAILED_LEMUR.get(),
                AAEntities.RUBBER_DUCKY_ISOPOD.get(), AAEntities.NAKED_MOLE_RAT.get(), AAEntities.STAG_BEETLE.get(), AAEntities.SHAME_FACED_CRAB.get(),
                AAEntities.FLYING_FISH.get(), AAEntities.NAPOLEON_WRASSE.get(), AAEntities.OPAH.get(), AAEntities.RED_RIVER_HOG.get(),
                AAEntities.BLUNTHEAD_TREE_SNAKE.get(), AAEntities.BLUE_SPOTTED_STINGRAY.get(), AAEntities.LEAF_FROG_TADPOLE.get(), AAEntities.LEAF_FROG.get(),

        };
        for (EntityType<?> type : simpleEntities) {
            make(type, type.getDescriptionId().substring("entity.ambientadditions.".length()));
        }

        EntityRenderers.register(AAEntities.PEMBROKE_CORGI.get(), (ctx) -> {
            GenericGeoRenderer<PembrokeCorgi> render = new GenericGeoRenderer<>(ctx, () -> new GenericGeoModel<>("pembroke_corgi"));
            render.addRenderLayer(new PembrokeCorgiCollarLayer(render));
            return render;
        });

        EntityRenderers.register(AAEntities.CARDIGAN_CORGI.get(), (ctx) -> {
            GenericGeoRenderer<CardiganCorgi> render = new GenericGeoRenderer<>(ctx, () -> new GenericGeoModel<>("cardigan_corgi"));
            render.addRenderLayer(new CardiganCorgiCollarLayer(render));
            return render;
        });

        EntityRenderers.register(AAEntities.AYE_AYE.get(), (ctx) -> {
            GenericGeoRenderer<AyeAye> render = new GenericGeoRenderer<>(ctx, () -> new GenericGeoModel<>("aye_aye"));
            render.addRenderLayer(new AAGlowingEyesLayer<>("aye_aye", render));
            return render;
        });

        EntityRenderers.register(AAEntities.VEILED_CHAMELEON.get(), (ctx) -> {
            GenericGeoRenderer<VeiledChameleon> render = new GenericGeoRenderer<>(ctx, () -> {
                TextureVariantModel<VeiledChameleon> model = new TextureVariantModel<>("veiled_chameleon");
                ArrayList<ResourceLocation> textures = new ArrayList<>();
                for (int i=1;i<=7;i++){
                    textures.add(new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/veiled_chameleon/veiled_chameleon_" + i + ".png"));
                }
                model.setTextures(VeiledChameleon::getVariant, textures);
                return model;
            });
            render.addRenderLayer(new ChameleonBrightnessLayer(render));
            return render;
        });

        EntityRenderers.register(AAEntities.CHOCOLATE_CHIP_STARFISH.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<ChocolateChipStarfish> model = new TextureVariantModel<>("chocolate_chip_starfish");
            ArrayList<ResourceLocation> textures = new ArrayList<>();
            for (int i=1;i<=5;i++){
                textures.add(new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/chocolate_chip_starfish/starfish_" + i + ".png"));
            }
            model.setTextures(ChocolateChipStarfish::getVariant, textures);
            return model;
        }));

        EntityRenderers.register(AAEntities.MARTEN.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<Marten> model = new TextureVariantModel<>("marten");
            ArrayList<ResourceLocation> textures = new ArrayList<>();
            for (int i=1;i<=2;i++){
                textures.add(new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/marten/marten_" + i + ".png"));
            }
            model.setTextures(Marten::getVariant, textures);
            return model;
        }));

        EntityRenderers.register(AAEntities.HARLEQUIN_SHRIMP.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<HarlequinShrimp> model = new TextureVariantModel<>("harlequin_shrimp");
            model.setTextures(HarlequinShrimp::getVariant, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/harlequin_shrimp/pink.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/harlequin_shrimp/purple.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/harlequin_shrimp/blue.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.MOLE.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<Mole> model = new TextureVariantModel<>("star_nosed_mole");
            model.setTextures(Mole::getVariant, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/mole/star_nosed_mole.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/mole/eastern_mole.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/mole/hairy_tailed_mole.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.SIAMANG_GIBBON.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<SiamangGibbon> model = new TextureVariantModel<>("siamang_gibbon");
            model.setTextures((e) -> e.isBooming() ? 1 : 0, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/siamang_gibbon/normal.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/siamang_gibbon/booming.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.WHITE_FRUIT_BAT.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<WhiteFruitBat> model = new TextureVariantModel<>("white_fruit_bat");
            model.setTextures((e) -> e.isResting() ? 1 : 0, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/white_fruit_bat/bat.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/white_fruit_bat/resting.png")
            ));
            return model;
        }) {

            @Override
            protected void applyRotations(WhiteFruitBat entitylivingbaseIn, PoseStack matrix, float ageInTicks, float rotationYaw, float partialTicks) {
                super.applyRotations(entitylivingbaseIn, matrix, ageInTicks, rotationYaw, partialTicks);
                matrix.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entitylivingbaseIn.prevTilt, entitylivingbaseIn.tilt)));
            }
        });

        EntityRenderers.register(AAEntities.YETI_CRAB.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<YetiCrab> model = new TextureVariantModel<>("yeti_crab");
            model.setTextures((e) -> e.isSheared() ? 1 : 0, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/yeti_crab/yeti_crab.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/yeti_crab/sheared.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.PANCAKE_SLUG.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<PancakeSlug> model = new TextureVariantModel<>("pancake_slug");
            model.setTextures(PancakeSlug::getVariant, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/pancake_slug/pancake_slug_1.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/pancake_slug/pancake_slug_2.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/pancake_slug/pancake_slug_3.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.SLOTH_BEAR.get(), (ctx) -> new GenericGeoRenderer<>(ctx, () -> {
            TextureVariantModel<SlothBear> model = new TextureVariantModel<>("sloth_bear");
            model.setTextures((e) -> e.isAngry() ? 1 : 0, Arrays.asList(
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/sloth_bear/bear.png"),
                    new ResourceLocation(AmbientAdditions.MOD_ID, "textures/entity/sloth_bear/angry.png")
            ));
            return model;
        }));

        EntityRenderers.register(AAEntities.MATA_MATA.get(), MataMataRenderer::new);

        EntityRenderers.register(AAEntities.DART.get(), DartRenderer::new);

        ItemProperties.register(AAItems.CRATE.get(), new ResourceLocation(AmbientAdditions.MOD_ID, "full"), (stack, world, player, i) -> !CrateItem.containsEntity(stack) ? 0.0F : 1.0F);

        AmbientAdditions.CALLBACKS.forEach(Runnable::run);
        AmbientAdditions.CALLBACKS.clear();

    }

    @SubscribeEvent
    public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AAParticles.ZZZ.get(), ZzzParticle.Provider::new);
        event.registerSpriteSet(AAParticles.STUN.get(), StunParticle.Provider::new);
    }
}