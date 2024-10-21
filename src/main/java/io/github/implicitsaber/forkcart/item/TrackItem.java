package io.github.implicitsaber.forkcart.item;

import io.github.implicitsaber.forkcart.Forkcart;
import io.github.implicitsaber.forkcart.block.TrackTiesBlockEntity;
import io.github.implicitsaber.forkcart.component.OriginComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Locale;

public class TrackItem extends Item {

    private final Type type;

    public TrackItem(Settings settings, Type type) {
        super(settings.component(DataComponentTypes.LORE,
                new LoreComponent(List.of(
                        Text.translatable("item.forkcart.track.desc").formatted(Formatting.GRAY),
                        Text.translatable("item.forkcart.track." +type.name().toLowerCase(Locale.ROOT) +".desc")
                                .formatted(Formatting.GRAY)))
        ));
        this.type = type;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null && !context.getPlayer().canModifyBlocks()) {
            return super.useOnBlock(context);
        }

        var world = context.getWorld();
        var pos = context.getBlockPos();
        var stack = context.getStack();

        if (world.getBlockEntity(pos) instanceof TrackTiesBlockEntity) {
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            var origin = stack.get(Forkcart.ORIGIN_POS);
            if (origin != null) {
                var oPos = origin.pos();
                if (!pos.equals(oPos) && world.getBlockEntity(oPos) instanceof TrackTiesBlockEntity oTies) {
                    oTies.setNext(pos, type);

                    world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
                }

                stack.remove(Forkcart.ORIGIN_POS);
            } else {
                stack.set(Forkcart.ORIGIN_POS, new OriginComponent(pos));
            }
        } else {
            var origin = stack.get(Forkcart.ORIGIN_POS);
            if (origin != null) {
                if (world.isClient()) {
                    return ActionResult.CONSUME;
                }

                stack.remove(Forkcart.ORIGIN_POS);
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var origin = stack.get(Forkcart.ORIGIN_POS);
        if (origin != null) {
            origin.appendTooltip(context, tooltip::add, type);
        }
    }

    public Type getType() {
        return type;
    }

    public enum Type {

        STANDARD,
        CHAIN,
        STATION

    }

}
