package dev.miles.sounddetector;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
public final class DetectorScreen extends Screen {
 private final Resonance mod;private int left,top,panelWidth;
 public DetectorScreen(Resonance mod){super(Text.literal("Resonance"));this.mod=mod;}
 private void button(String title,int x,int y,int w,Runnable action){addDrawableChild(ButtonWidget.builder(Text.literal(title),b->{action.run();mod.save();clearAndInit();}).dimensions(x,y,w,22).build());}
 private String state(boolean v){return v?"ON":"OFF";}
 @Override protected void init(){
  panelWidth=Math.min(440,width-24);left=(width-panelWidth)/2;top=Math.max(8,(height-270)/2);int x=left+18,y=top+65,w=(panelWidth-44)/2;
  button("Detector / "+state(mod.enabled),x,y,w,()->{mod.enabled=!mod.enabled;mod.hits.clear();});button("Block ESP / "+state(mod.esp),x+w+8,y,w,()->mod.esp=!mod.esp);
  button("Signal feed / "+state(mod.hud),x,y+28,w,()->mod.hud=!mod.hud);button("Muted sounds / "+state(mod.muted),x+w+8,y+28,w,()->mod.muted=!mod.muted);
  button("Ambient / "+state(mod.ambient),x,y+56,w,()->mod.ambient=!mod.ambient);button("Range / "+mod.range+" blocks",x+w+8,y+56,w,()->mod.range=mod.range>=512?32:mod.range*2);
  button("Lifetime / "+mod.seconds+" seconds",x,y+84,w,()->mod.seconds=mod.seconds>=20?2:mod.seconds+2);button("Palette / "+new String[]{"Champagne","Jade","Iris"}[mod.theme],x+w+8,y+84,w,()->mod.theme=(mod.theme+1)%3);
  button("Ignore own sounds / "+state(mod.ignoreSelf),x,y+112,w,()->mod.ignoreSelf=!mod.ignoreSelf);
  button("Outline opacity / "+Math.round(mod.outlineAlpha*100)+"%",x+w+8,y+112,w,()->mod.outlineAlpha=mod.outlineAlpha>=0.99f?0.1f:Math.min(1,mod.outlineAlpha+0.1f));
  button("Clear signals",x,y+140,w,()->mod.hits.clear());addDrawableChild(ButtonWidget.builder(Text.literal("Done"),b->close()).dimensions(x+w+8,y+140,w,22).build());
 }
 @Override public void render(DrawContext g,int mx,int my,float delta){
  g.fill(0,0,width,height,0xDB080A10);g.fill(left+4,top+5,left+panelWidth+4,top+260,0x70000000);g.fill(left,top,left+panelWidth,top+256,0xFF151A24);g.fill(left,top,left+panelWidth,top+2,mod.accent());
  g.drawText(textRenderer,"R E S O N A N C E",left+18,top+18,mod.accent(),false);g.drawText(textRenderer,"SOUND INTELLIGENCE / 1.0",left+18,top+36,0xFF8793A7,false);g.fill(left+18,top+54,left+panelWidth-18,top+55,0xFF2B3342);
  g.drawText(textRenderer,"Right Shift opens settings. Rebind in Controls.",left+18,top+238,0xFF8793A7,false);super.render(g,mx,my,delta);
 }
 @Override public boolean shouldPause(){return false;}
}
