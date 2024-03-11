package testmod;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.DifficultyChangeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final Holder<Item> MY_ITEM = ITEMS.register("somemod", () -> new Banan(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(1)
            .saturationMod(2f)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10000, 20), 1f)
            .build())));

    public static final Holder<Block> MY_BLOCK = BLOCKS.register("someblock", () -> new BananBlock(BlockBehaviour.Properties.of().strength(0.1f)));
    public static final Holder<CreativeModeTab> MY_TAB = TABS.register("mytab", () -> CreativeModeTab.builder()
            .title(Component.literal("My tab"))
            .icon(() -> MY_ITEM.value().getDefaultInstance())
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(MY_ITEM.value());
            }).build());

    public ExampleMod(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(this::onBreak);
        NeoForge.EVENT_BUS.addListener(this::onDifficultyChange);

        ITEMS.register(bus);
        TABS.register(bus);
        BLOCKS.register(bus);
    }

    public void onBreak(BlockEvent.BreakEvent event) {
        var state = event.getState();
        Player player = event.getPlayer();
        if (event.getState().getBlock() == Blocks.DIRT) {
            event.getPlayer().getInventory().add(new ItemStack(Items.DIRT, 2));
            player.kill();
        } else if (state.getBlock() == Blocks.SNOW) {
            event.getPlayer().getInventory().add(new ItemStack(Items.DIAMOND, 64));
            player.setPos(player.position().add(0, 10, 0));
        }
    }

    public void onDifficultyChange(DifficultyChangeEvent event) {
        if (event.getDifficulty() == Difficulty.PEACEFUL) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            for (Player player : server.getPlayerList().getPlayers()) {
                player.kill();
            }
        }
    }

}
