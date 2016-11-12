package GlooKit.GlooFramework;

/**
 * {@code Rooms} are owned by a {@link GlooApplication GlooApplication} and are the visual state of the application. A
 * {@code GlooApplication} may only have one {@code GlooApplication} at a time, as the previous one will be destroyed
 * when the  new one is added. {@code Room} extends {@link Nook Nook}, which means that {@code Rooms} capture user
 * input. Examples of a {@code Room} include a MainMenu or a Settings screen.
 *
 * @see GlooKit.GlooFramework.Nook
 *
 * @author Duncan Walter
 * @author Eli Jergensen
 * @since 1.0
 * */
public class Room extends Nook {

    /**
     * Constructs a {@code Room} with only the {@code GlooApplication} and a list of {@code KitBit} which are the
     * children of this {@code Room}.
     *
     * @param app The GlooApplication which contains this Room and gives the Room user input
     * @param children possibly many KitBits, which are children of this Room. Can take any number of KitBits (null for
     *                 none)
     * */
    public Room(GlooApplication app, KitBit... children){
        super(CENTER, CENTER, "1/n+", "1/n+", app, children);
        z = -1;
    }

    /**
     * Initiates the process of telling all visible {@code KitBits} by grabbing the spacing and pointSize from the
     * {@code GlooApplication}. This calls the recursive {@link KitBit#configure(float, float) configure(float, float)},
     * thereby ensuring that all of the descendants of this {@code Room} receive the information.
     * */
    public void configure(){
        configure(getApp().getSpacing(), getApp().pointSize);
    }

}
