import java.util.List;

import processing.core.PImage;

/**
 * Represents a background for the 2D world.
 */
public final class Background
{
    private String id;
    private List<PImage> images;
    private int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public List<PImage> getImages()
    {
        return this.images;
    }

    public int getImageIndex()
    {
        return this.imageIndex;
    }


    public  void setBackground(
            WorldModel world, Point pos)
    {
        if (world.withinBounds(pos)) {
            world.setBackgroundCell(pos, this);
        }
    }
    public static PImage getCurrentImage(Background background) {
        return background.getImages().get(
                background.getImageIndex());
    }


}
