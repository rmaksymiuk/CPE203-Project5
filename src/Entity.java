import java.util.*;
import processing.core.PImage;


public abstract class Entity {

    private String id;
    private Point position;
    private List<PImage> images;
    protected int imageIndex;



    public Entity(String id, Point position, List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
    }
    public String getId(){return this.id;}
    public Point getPosition(){return this.position;}
    public  PImage getCurrentImage() {return this.images.get(this.imageIndex);}
    public void setPosition(Point position){this.position = position;}
    public List<PImage> getImages() {return images;}

}
