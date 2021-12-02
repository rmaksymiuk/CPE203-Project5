public class ActivityAction extends Action {

    private EntityActivity entity;
    private WorldModel world;
    private ImageStore imageStore;

    //Constructor
    public ActivityAction(
            EntityActivity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler)
    {
        this.entity.executeActorActivity(this.world, this.imageStore, scheduler);
    }



}
