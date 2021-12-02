import processing.core.PImage;

import java.util.List;

public class Obstacle extends Animator{


    public Obstacle(String id, Point position, List<PImage> images, int animationPeriod) {
        super(id, position, images, animationPeriod);
        super.imageIndex = 0;

    }

}


