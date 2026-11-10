package dev.miles.sounddetector;
import java.util.*;
import java.nio.file.*;
import com.google.gson.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
public final class Resonance implements ClientModInitializer {
 public static Resonance INSTANCE;
 public record Hit(String name,BlockPos block,long first,long last){}
 public final LinkedHashMap<BlockPos,Hit> hits=new LinkedHashMap<>();
 public boolean enabled=true,esp=true,hud=true,muted=true,ambient=false;
 public int range=128,seconds=6,theme=0;
 public boolean ignoreSelf=true;
 public float outlineAlpha=0.85f;
 private net.minecraft.client.option.KeyBinding menuKey;
 private Object world; private boolean menu;
 private Path config(){return FabricLoader.getInstance().getConfigDir().resolve("resonance.json");}
 public int accent(){return new int[]{0xFFE1C58B,0xFF86D8CE,0xFFB8A5EE}[Math.floorMod(theme,3)];}
 public void save(){try{JsonObject j=new JsonObject();j.addProperty("enabled",enabled);j.addProperty("esp",esp);j.addProperty("hud",hud);j.addProperty("muted",muted);j.addProperty("ambient",ambient);j.addProperty("range",range);j.addProperty("seconds",seconds);j.addProperty("theme",theme);Files.writeString(config(),new GsonBuilder().setPrettyPrinting().create().toJson(j));}catch(Exception e){org.slf4j.LoggerFactory.getLogger("resonance").warn("Cannot save settings",e);}}
 private void load(){try{if(!Files.exists(config()))return;JsonObject j=JsonParser.parseString(Files.readString(config())).getAsJsonObject();enabled=j.get("enabled").getAsBoolean();esp=j.get("esp").getAsBoolean();hud=j.get("hud").getAsBoolean();muted=j.get("muted").getAsBoolean();ambient=j.get("ambient").getAsBoolean();range=Math.max(16,Math.min(512,j.get("range").getAsInt()));seconds=Math.max(2,Math.min(20,j.get("seconds").getAsInt()));theme=Math.floorMod(j.get("theme").getAsInt(),3);}catch(Exception e){org.slf4j.LoggerFactory.getLogger("resonance").warn("Cannot load settings",e);}}
 public void capture(SoundInstance sound){
  MinecraftClient c=MinecraftClient.getInstance();
  if(!enabled||c.world==null||c.player==null||sound.isRelative())return;
  SoundCategory category=sound.getCategory();
  if(category==SoundCategory.MUSIC||category==SoundCategory.RECORDS)return;
  double x=sound.getX(),y=sound.getY(),z=sound.getZ();String name=sound.getId().toString();Object sourceWorld=c.world;
  if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z))return;
  c.execute(()->{
   if(!enabled||c.world!=sourceWorld||c.player==null)return;
   double dx=x-c.player.getX(),dy=y-c.player.getY(),dz=z-c.player.getZ();
   if(dx*dx+dy*dy+dz*dz>range*range||(!ambient&&category==SoundCategory.AMBIENT))return;
   if(ignoreSelf && category==SoundCategory.PLAYERS && dx*dx+dy*dy+dz*dz<4)return;
   if(!muted&&(c.options.getSoundVolume(SoundCategory.MASTER)<=0||c.options.getSoundVolume(category)<=0))return;
   BlockPos p=BlockPos.ofFloored(x,y,z);long now=System.nanoTime();Hit old=hits.remove(p);
   hits.put(p,new Hit(name,p,old==null?now:old.first,now));
   while(hits.size()>32)hits.remove(hits.keySet().iterator().next());
  });
 }
 public float opacity(Hit h){long now=System.nanoTime();return Math.max(0,Math.min(1,(now-h.first)/180_000_000f)*Math.min(1,(seconds*1_000_000_000L-(now-h.last))/700_000_000f));}
 @Override public void onInitializeClient(){
  INSTANCE=this;load();loadExtras();
  menuKey=net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(new net.minecraft.client.option.KeyBinding("key.sound_detector.menu",org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT,net.minecraft.client.option.KeyBinding.Category.create(net.minecraft.util.Identifier.of("sound_detector","controls"))));
  ClientTickEvents.END_CLIENT_TICK.register(c->{while(menuKey.wasPressed())menu=true;});
  ClientTickEvents.END_CLIENT_TICK.register(c->{if(world!=c.world){world=c.world;hits.clear();Projection.clear();}long now=System.nanoTime();hits.values().removeIf(h->now-h.last>seconds*1_000_000_000L);if(menu){menu=false;c.setScreen(new DetectorScreen(this));}});
  ClientCommandRegistrationCallback.EVENT.register((d,a)->d.register(literal("sounddetector").executes(ctx->{menu=true;return 1;}).then(literal("toggle").executes(ctx->{enabled=!enabled;hits.clear();save();return 1;})).then(literal("clear").executes(ctx->{hits.clear();return 1;}))));
  HudRenderCallback.EVENT.register((g,t)->render(g));
 }
 private void render(DrawContext g){
  saveExtrasIfChanged();
  MinecraftClient c=MinecraftClient.getInstance();if(!enabled||c.world==null||c.player==null||c.options.hudHidden)return;
  if(esp)for(Hit h:hits.values())Projection.box(g,h.block,opacity(h)*outlineAlpha,accent());
  if(!hud||c.currentScreen instanceof DetectorScreen)return;
  int w=Math.min(270,g.getScaledWindowWidth()-24);g.fill(12,12,12+w,40,0xE810131B);g.fill(12,12,14,40,accent());g.drawText(c.textRenderer,"RESONANCE",22,19,accent(),false);g.drawText(c.textRenderer,hits.size()+" signals",12+w-70,19,0xFF9CA6B8,false);
  List<Hit> list=new ArrayList<>(hits.values());Collections.reverse(list);int y=44;
  for(Hit h:list.subList(0,Math.min(5,list.size()))){if(y+30>g.getScaledWindowHeight())break;g.fill(12,y,12+w,y+30,0xC810131B);g.drawText(c.textRenderer,c.textRenderer.trimToWidth(h.name.replace("minecraft:",""),w-20),22,y+4,0xFFECE9E1,false);g.drawText(c.textRenderer,h.block.getX()+" / "+h.block.getY()+" / "+h.block.getZ(),22,y+16,0xFF919DAF,false);y+=32;}
 }
 private String lastExtras="";
 private void loadExtras(){try{Path p=config().resolveSibling("resonance-visual.json");if(Files.exists(p)){JsonObject j=JsonParser.parseString(Files.readString(p)).getAsJsonObject();ignoreSelf=j.get("ignoreSelf").getAsBoolean();outlineAlpha=Math.max(0,Math.min(1,j.get("outlineAlpha").getAsFloat()));}}catch(Exception e){org.slf4j.LoggerFactory.getLogger("resonance").warn("Cannot read visual settings",e);}}
 private void saveExtrasIfChanged(){String value="{\"ignoreSelf\":"+ignoreSelf+",\"outlineAlpha\":"+outlineAlpha+"}";if(value.equals(lastExtras))return;lastExtras=value;try{Files.writeString(config().resolveSibling("resonance-visual.json"),value);}catch(Exception e){org.slf4j.LoggerFactory.getLogger("resonance").warn("Cannot save visual settings",e);}}
}
