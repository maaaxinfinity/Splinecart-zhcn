package io.github.implicitsaber.forkcart;

import io.github.implicitsaber.forkcart.block.ShuttleTiesBlock;
import io.github.implicitsaber.forkcart.block.SwitchTiesBlock;
import io.github.implicitsaber.forkcart.block.TrackTiesBlock;
import io.github.implicitsaber.forkcart.block.TrackTiesBlockEntity;
import io.github.implicitsaber.forkcart.component.OriginComponent;
import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import io.github.implicitsaber.forkcart.item.TrackItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Forkcart implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("forkcart");

	public static final TrackTiesBlock TRACK_TIES = Registry.register(Registries.BLOCK, id("track_ties"),
			new TrackTiesBlock(AbstractBlock.Settings.copy(Blocks.RAIL)));
	public static final SwitchTiesBlock SWITCH_TIES = Registry.register(Registries.BLOCK, id("switch_ties"),
			new SwitchTiesBlock(AbstractBlock.Settings.copy(Blocks.RAIL)));
	public static final TrackTiesBlock INVISIBLE_TIES = Registry.register(Registries.BLOCK, id("invisible_ties"),
			new TrackTiesBlock(AbstractBlock.Settings.copy(Blocks.RAIL)) {
				@Override
				protected BlockRenderType getRenderType(BlockState state) {
					return BlockRenderType.INVISIBLE;
				}
			});
	public static final ShuttleTiesBlock SHUTTLE_TIES = Registry.register(Registries.BLOCK, id("shuttle_ties"),
			new ShuttleTiesBlock(AbstractBlock.Settings.copy(Blocks.RAIL)));
	public static final BlockEntityType<TrackTiesBlockEntity> TRACK_TIES_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("track_ties"),
			BlockEntityType.Builder.create(TrackTiesBlockEntity::new, TRACK_TIES, SWITCH_TIES, INVISIBLE_TIES, SHUTTLE_TIES).build());

	public static final TrackItem TRACK = Registry.register(Registries.ITEM, id("track"),
			new TrackItem(new Item.Settings(), TrackItem.Type.STANDARD));
	public static final TrackItem CHAIN_TRACK = Registry.register(Registries.ITEM, id("chain_track"),
			new TrackItem(new Item.Settings(), TrackItem.Type.CHAIN));
	public static final TrackItem STATION_TRACK = Registry.register(Registries.ITEM, id("station_track"),
			new TrackItem(new Item.Settings(), TrackItem.Type.STATION));
	public static final TrackItem BRAKE_TRACK = Registry.register(Registries.ITEM, id("brake_track"),
			new TrackItem(new Item.Settings(), TrackItem.Type.BRAKE));

	public static final Identifier CHAIN_LIFT_SOUND_ID = id("entity.track_follower.lift");
	public static final SoundEvent CHAIN_LIFT_SOUND = Registry.register(Registries.SOUND_EVENT, CHAIN_LIFT_SOUND_ID,
			SoundEvent.of(CHAIN_LIFT_SOUND_ID));

	public static final ComponentType<OriginComponent> ORIGIN_POS = Registry.register(Registries.DATA_COMPONENT_TYPE, id("origin"),
			ComponentType.<OriginComponent>builder().codec(OriginComponent.CODEC).build());

	public static final EntityType<TrackFollowerEntity> TRACK_FOLLOWER = Registry.register(Registries.ENTITY_TYPE, id("track_follower"),
			EntityType.Builder.<TrackFollowerEntity>create(TrackFollowerEntity::new, SpawnGroup.MISC).trackingTickInterval(2).dimensions(0.25f, 0.25f).build());

	public static final RegistryKey<ItemGroup> MOD_GROUP = Registry.registerReference(Registries.ITEM_GROUP, id("forkcart"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemGroup.forkcart"))
					.icon(() -> new ItemStack(TRACK))
					.build()).getKey().orElseThrow();

	public static final TagKey<EntityType<?>> CARTS = TagKey.of(RegistryKeys.ENTITY_TYPE, id("carts"));
	public static final TagKey<Item> TRACK_TAG = TagKey.of(RegistryKeys.ITEM, id("track"));

	@Override
	public void onInitialize() {
		BlockItem tieItem = Registry.register(Registries.ITEM, id("track_ties"),
				new BlockItem(TRACK_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE, lore(
								Text.translatable("item.forkcart.track_ties.desc").formatted(Formatting.GRAY))
						)));
		BlockItem switchTieItem = Registry.register(Registries.ITEM, id("switch_ties"),
				new BlockItem(SWITCH_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE, lore(
								Text.translatable("item.forkcart.track_ties.desc").formatted(Formatting.GRAY),
								Text.translatable("item.forkcart.switch_ties.desc").formatted(Formatting.GRAY)
						))));
		BlockItem invisibleTieItem = Registry.register(Registries.ITEM, id("invisible_ties"),
				new BlockItem(INVISIBLE_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE, lore(
								Text.translatable("item.forkcart.track_ties.desc").formatted(Formatting.GRAY),
								Text.translatable("item.forkcart.invisible_ties.desc").formatted(Formatting.GRAY)
						))));
		BlockItem shuttleTieItem = Registry.register(Registries.ITEM, id("shuttle_ties"),
				new BlockItem(SHUTTLE_TIES, new Item.Settings()
						.component(DataComponentTypes.LORE, lore(
								Text.translatable("item.forkcart.track_ties.desc").formatted(Formatting.GRAY),
								Text.translatable("item.forkcart.shuttle_ties.desc").formatted(Formatting.GRAY)
						))));

		ItemGroupEvents.modifyEntriesEvent(MOD_GROUP).register(entries -> {
			entries.add(tieItem.getDefaultStack());
			entries.add(switchTieItem.getDefaultStack());
			entries.add(invisibleTieItem.getDefaultStack());
			entries.add(shuttleTieItem.getDefaultStack());
			entries.add(TRACK.getDefaultStack());
			entries.add(CHAIN_TRACK.getDefaultStack());
			entries.add(STATION_TRACK.getDefaultStack());
			entries.add(BRAKE_TRACK.getDefaultStack());
		});
	}

	public static LoreComponent lore(Text lore) {
		return new LoreComponent(List.of(lore));
	}

	public static LoreComponent lore(Text lore, Text lore2) {
		return new LoreComponent(List.of(lore, lore2));
	}

	public static Identifier id(String path) {
		return Identifier.of("forkcart", path);
	}
}