import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static String LOAD_FILE_NAME = "world.sav";

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private static double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
    }

    // Just for debugging and for P5
    public void mousePressed() {
        Point pressed = mouseToPoint(mouseX, mouseY);
        System.out.println("CLICK! " + pressed.getX() + ", " + pressed.getY());

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent())
        {
            Entity entity = entityOptional.get();
            if(entity instanceof Plant)
            {
                System.out.println(entity.getId() + ": " + entity.getClass() + " : " + ((Plant)entity).getHealth());
            }
            else {
                System.out.println(entity.getId() + ": " + entity.getClass());
            }

        }
        //Background


        List<Point> spawnPoints = new ArrayList<>();
        spawnPoints.add(pressed);
        spawnPoints.add(new Point(pressed.getX() + 1, pressed.getY() + 1));
        spawnPoints.add(new Point(pressed.getX() + 1, pressed.getY() - 1));
        spawnPoints.add(new Point(pressed.getX() - 1, pressed.getY() + 1));
        spawnPoints.add(new Point(pressed.getX() - 1, pressed.getY() - 1));
        spawnPoints.add(new Point(pressed.getX() , pressed.getY() + 1));
        spawnPoints.add(new Point(pressed.getX() , pressed.getY() - 1));
        spawnPoints.add(new Point(pressed.getX() + 1, pressed.getY()));
        spawnPoints.add(new Point(pressed.getX() - 1, pressed.getY()));
        spawnPoints.add(new Point(pressed.getX() + 2, pressed.getY()));


        if(spawnPoints.stream().filter(point -> (!world.isOccupied(point)) && (world.withinBounds(point))).count() == 10) {
            createCobbleBackground(pressed, 1,0);
            createCobbleBackground(pressed, -1,0);
            createCobbleBackground(pressed, 0,1);
            createCobbleBackground(pressed, 0,-1);
            createCobbleBackground(pressed, 0,0);
            createCobbleBackground(pressed, 1,1);
            createCobbleBackground(pressed, -1,-1);
            createCobbleBackground(pressed, -1,1);
            createCobbleBackground(pressed, 1,-1);

            //Castle
            this.world.addEntity(new Castle("castle", pressed,this.imageStore.getImageList("castle")));
            //Fence
            createFence(pressed, -1,1);
            createFence(pressed, 1,1);
            createFence(pressed, -1,-1);
            createFence(pressed, 0,1);
            createFence(pressed, 0,-1);
            createFence(pressed, 1,0);
            createFence(pressed, 1,-1);
            createFence(pressed, -1,1);

            //Knight
            Animator knight = new Knight("knight", new Point(pressed.getX() -1, pressed.getY() ),this.imageStore.getImageList("knight"), 500,51);
            this.world.addEntity(knight);
            knight.scheduleActions(this.scheduler, this.world,this.imageStore);
            //Monster
            Animator monster = new Monster("monster", new Point(pressed.getX()+2, pressed.getY() ),this.imageStore.getImageList("monster"), 500,51);
            this.world.addEntity(monster);
            monster.scheduleActions(this.scheduler, this.world,this.imageStore);
        }
        else {
            System.out.println("Invalid spawn area. Click on an unoccupied 3x3 tile area to spawn the castle and the knight plus an additional space for the monster.");
        }

    }

    private void createCobbleBackground(Point pressed, int x, int y)
    {
        this.world.setBackgroundCell(new Point(pressed.getX()+x,pressed.getY() + y), new Background("cobble", this.imageStore.getImageList("cobble")));
    }

    private void createFence(Point pressed, int x, int y)
    {
        this.world.addEntity(new Fence("fence", new Point(pressed.getX()+x, pressed.getY()+y), this.imageStore.getImageList("fence")));
    }

    private Point mouseToPoint(int x, int y)
    {
        return view.getViewport().viewportToWorld( mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
    }
    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    static void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, imageStore, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            world.load(in, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if(entity instanceof Animator) {
                ((Animator)entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public static void parseCommandLine(String[] args) {
        if (args.length > 1)
        {
            if (args[0].equals("file"))
            {

            }
        }
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
