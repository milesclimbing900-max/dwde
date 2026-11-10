package dev.miles.sounddetector;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.gui.DrawContext;
public final class Projection {
 private static Matrix4f matrix;private static Vec3d camera;
 public static void clear(){matrix=null;}
 public static void capture(Matrix4f view,Matrix4f projection,Vec3d pos){matrix=new Matrix4f(projection).mul(view);camera=pos;}
 public static void box(DrawContext g,BlockPos p,float fade,int rgb){
  if(matrix==null||fade<=0)return;Vector4f[] v=new Vector4f[8];
  for(int i=0;i<8;i++)v[i]=matrix.transform(new Vector4f((float)(p.getX()+(i&1)-camera.x),(float)(p.getY()+((i>>1)&1)-camera.y),(float)(p.getZ()+((i>>2)&1)-camera.z),1));
  for(int i=0;i<8;i++)for(int bit=1;bit<=4;bit*=2)if((i&bit)==0)edge(g,new Vector4f(v[i]),new Vector4f(v[i|bit]),fade,rgb);
 }
 private static void edge(DrawContext g,Vector4f a,Vector4f b,float fade,int rgb){
  for(int p=0;p<6;p++){float da=distance(a,p),db=distance(b,p);if(da<0&&db<0)return;if(da<0)a.lerp(b,da/(da-db));else if(db<0)b.lerp(a,db/(db-da));}
  if(a.w<=0||b.w<=0)return;
  float x1=(a.x/a.w+1)*g.getScaledWindowWidth()/2f,y1=(1-a.y/a.w)*g.getScaledWindowHeight()/2f,x2=(b.x/b.w+1)*g.getScaledWindowWidth()/2f,y2=(1-b.y/b.w)*g.getScaledWindowHeight()/2f;
  float dx=x2-x1,dy=y2-y1;int steps=Math.max(1,(int)Math.ceil(Math.max(Math.abs(dx),Math.abs(dy))));
  for(int i=0;i<=steps;i++){float x=x1+dx*i/steps,y=y1+dy*i/steps;int ix=(int)Math.floor(x),iy=(int)Math.floor(y);float fx=x-ix,fy=y-iy;pixel(g,ix,iy,fade*(1-fx)*(1-fy),rgb);pixel(g,ix+1,iy,fade*fx*(1-fy),rgb);pixel(g,ix,iy+1,fade*(1-fx)*fy,rgb);pixel(g,ix+1,iy+1,fade*fx*fy,rgb);}
 }
 private static float distance(Vector4f v,int p){return switch(p){case 0->v.w+v.x;case 1->v.w-v.x;case 2->v.w+v.y;case 3->v.w-v.y;case 4->v.w+v.z;default->v.w-v.z;};}
 private static void pixel(DrawContext g,int x,int y,float a,int rgb){if(a>.02f)g.fill(x,y,x+1,y+1,((int)(a*255)<<24)|(rgb&0xFFFFFF));}
}
