package GameModel;

public class Scenery extends Item {
    public String toString(){
        String s = "";
        String c = this.getClass().toString().substring(16,22);
        s += c + ":" + getPx() + "," + getPy();
        return s;
    }
}
