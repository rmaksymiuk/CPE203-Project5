import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public int getNumRows()
    {
        return this.numRows;
    }
    public int getNumCols()
    {
        return this.numCols;
    }
    public Set<Entity> getEntities()
    {
        return this.entities;
    }


    public  boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows && pos.getX() >= 0
                && pos.getX() < this.numCols;
    }

    public  Optional<PImage> getBackgroundImage(
             Point pos)
    {
        if (this.withinBounds(pos)) {
            return Optional.of(Background.getCurrentImage(getBackgroundCell(pos)));
        }
        else {
            return Optional.empty();
        }
    }

    public  void setBackgroundCell(Point pos, Background background)
    {
        this.background[pos.getY()][pos.getX()] = background;
    }


    private  Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public  void setOccupancyCell(Point pos, Entity entity)
    {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public  Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        }
        else {
            return Optional.empty();
        }
    }

    public  Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }


    public  void load(
            Scanner in, ImageStore imageStore)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                //not sure why it doesn't change the output if I change from this to world
                if (!this.processLine(in.nextLine(), imageStore)) {
                    System.err.println(String.format("invalid entry on line %d",
                            lineNumber));
                }
            }
            catch (NumberFormatException e) {
                System.err.println(
                        String.format("invalid entry on line %d", lineNumber));
            }
            catch (IllegalArgumentException e) {
                System.err.println(
                        String.format("issue on line %d: %s", lineNumber,
                                e.getMessage()));
            }
            lineNumber++;
        }
    }


    public  boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    //processes each line of the world
    private boolean processLine(
            String line, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[Functions.PROPERTY_KEY]) {
                case Functions.BGND_KEY:
                    return  Functions.parseBackground(properties,this, imageStore);
                case Functions.DUDE_KEY:
                    return Functions.parseDude(properties,this, imageStore);
                case Functions.OBSTACLE_KEY:
                    return Functions.parseObstacle(properties,this, imageStore);
                case Functions.FAIRY_KEY:
                    return Functions.parseFairy(properties,this, imageStore);
                case Functions.HOUSE_KEY:
                    return Functions.parseHouse(properties,this, imageStore);
                case Functions.TREE_KEY:
                    return Functions.parseTree(properties,this, imageStore);
                case Functions.SAPLING_KEY:
                    return Functions.parseSapling(properties,this, imageStore);
            }
        }

        return false;
    }


    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.getEntities().add(entity);
        }
    }


    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }


    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }


    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }


    public  void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition( new Point(-1, -1));
            this.getEntities().remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }



}
