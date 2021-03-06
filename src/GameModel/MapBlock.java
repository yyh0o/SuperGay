package GameModel;



import javafx.scene.canvas.GraphicsContext;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MapBlock {
    private ArrayList<Scenery> scenes; //用于储存当前区块中的景物
    private GroundBlock[][] blocks; //用于储存当前区块中的block
    private boolean isActive; //标记当前区块是否活跃
    private final int Height = 80; //区块高度
    private final int Width = 80; //区块宽度
    private double mHeight = 32 * Height;//区块像素高度
    private double mWidth = 32 * Width;//区块像素宽度
    private int px;//区块在世界的位置(x)
    private int py;//区块在世界的位置(y)
    //这一个要看是否有水块产生，若果没有就先调用再生成，
//    River river=new River(System.nanoTime());


    public ArrayList<Scenery> getScenes() {
        return scenes;
    }

    public double getmHeight() {
        return mHeight;
    }

    public double getmWidth() {
        return mWidth;
    }

    public int getPx() {
        return px;
    }

    public int getPy() {
        return py;
    }

    public void setIsActive(boolean active){
        isActive = active;
    }

    //根据种子随机生成区块
    public MapBlock(long seed,int initx, int inity){
        px = initx;
        py = inity;
        File f = new File("Dat/MapDat/"+initx+","+inity+".map");
        File f2=new File("Dat/MapDat/Water/"+initx+","+inity+".water");
        if (f.exists()){
            blocks = new GroundBlock[Height][Width];
            scenes = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String[] scene = br.readLine().split(" ");
                for (String K : scene){
                    Scenery scenery = null;// = new Scenery();
                    String mark = K.split(":")[0];
                    String[] p = K.split(":")[1].split(",");
                    switch (mark){
                        case "Grass1" :
                            scenery = new Grass1(Double.valueOf(p[0]),Double.valueOf(p[1]));
                            break;
                        case "Grass2" :
                            scenery = new Grass2(Double.valueOf(p[0]),Double.valueOf(p[1]));
                            break;
                        default:
                            break;
                    }
                    scenes.add(scenery);
                }
                if(!f2.exists()) {
                    for (int i = 0; i < Height; i++) {
                        String[] block = br.readLine().split(" ");
                        GroundBlock gb = new GrassBlock(0, 0);
                        for (int j = 0; j < Width; j++) {
                            String[] t = block[j].split(":");
                            switch (t[0]) {
                                case "GrassBlock":
                                    gb = new GrassBlock(Double.valueOf(t[1].split(",")[0]), Double.valueOf(t[1].split(",")[1]));
                                    break;
                                case "WaterBlock":
                                    gb = new WaterBlock(Double.valueOf(t[1].split(",")[0]), Double.valueOf(t[1].split(",")[1]));
                                    break;
                                default:
                                    break;
                            }
                            blocks[i][j] = gb;
                        }
                    }
                }
                if(f2.exists()) {
                    br = new BufferedReader(new FileReader(f2));
                    String[] H = br.readLine().split(":");
                    GroundBlock gb = new GrassBlock(0,0);
                    for(int i=0;i<Height;i++) {
                        String[] W=H[i].split(" ");
                        for(int j=0;j<Width;j++){
                           switch(W[j]){
                               case "1":
                                   gb=new WaterBlock(GrassBlock.Width()*j,GrassBlock.Height()*i);
                                   break;
                               case "0":
                                   gb=new GrassBlock(GrassBlock.Width()*j,GrassBlock.Height()*i);
                                   break;
                               default:
                                       break;
                           }
                            blocks[i][j]=gb;
                        }
                    }
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            double ah = 32;
            double aw = 32;
            for (GroundBlock[] a : blocks){
                for (GroundBlock b : a){
                    if (b instanceof WaterBlock){
                        double ax = b.getPx();
                        double ay = b.getPy();
                        Iterator<Scenery> iterator = scenes.iterator();
                        while (iterator.hasNext()){
                            Scenery k = iterator.next();
                            double bx = k.getPx();
                            double by = k.getPy();
                            double bh = k.getWidth();
                            double bw = k.getWidth();
                            if (ax<bx+bw&& ax+aw>bx&& ay<by+bh&& ah+ay>by){
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }
        else {
            String nSeed = seed + "" + px + "" + py;
            Random r = new Random(Long.valueOf(nSeed.hashCode()));
            blocks = new GroundBlock[Height][Width];
            scenes = new ArrayList<>();
            for (int i = 0; i < Height; i++){
                for (int j = 0; j < Width; j++){
                    blocks[i][j] = new GrassBlock(GrassBlock.Width()*j,GrassBlock.Height()*i);
                }
            }
            int slen = 20 + r.nextInt(400); //随机生成景物数量
            for (int i = 0; i < slen; i++){
                int x = r.nextInt(3);
                switch (x){
                    case 0:
                        break;
                    case 1:
                        scenes.add(new Grass1(r.nextDouble()*mWidth,r.nextDouble()*mHeight));
                        break;
                    case 2:
                        scenes.add(new Grass2(r.nextDouble()*mWidth,r.nextDouble()*mHeight));
                        break;
                    default:
                        break;
                }
            }
            isActive = false;
        }
    }


    //重新toString方便存入文件
    public String toString(){
        String s = "";
//        s += num + "\n";
        for (Scenery k : scenes){
            s += k.toString() + ' ';
        }
        s += '\n';
        for (int i = 0; i < blocks.length; i++){
            for (int j = 0; j < blocks[0].length; j++){
                s += blocks[i][j].toString()+' ';
            }
            s += '\n';
        }
        return s;
    }

    //在画布上绘制出当前区块
    public void draw(GraphicsContext gc, double fx, double fy){
        for (int i = 0; i < Height; i++){
            for (int j = 0; j < Width; j++){
                blocks[i][j].draw(gc,fx,fy);
            }
        }
        for (Scenery k : scenes){
            k.draw(gc,fx,fy);
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public void updateMapBloc(){
        if (isActive){
            for (Item k : scenes){
                k.updateItem();
            }
        }
    }


    //储存当前地图块到文件
    public void save(File f){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(this.toString());
            bw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //储存到已有的文件
    public void save(){
        try {
            File f = new File("Dat/MapDat/"+px+","+py+".map");
            if (!f.exists()){
                f.createNewFile();
            }
            save(f);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
