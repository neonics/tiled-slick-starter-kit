import java.io.File;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

final class TestGame
    extends BasicGame
{
    private float xOffs = 0, yOffs = 0;

    private float dx = 10, dy = 2;

    private File file;

    TestGame( String title, File file )
    {
        super( title );
        this.file = file;
    }

    @Override
    public void init( GameContainer c )
        throws SlickException
    {
        try
        {
            JSONLoader.load( file );
            Main.log( "World loaded: " + World.width + "x" + World.height );

            xOffs = -c.getWidth() / 2;
            yOffs = -World.tileHeight;
        }
        catch ( Exception e )
        {
            throw new SlickException( "Error loading World", e );
        }
    }

    @Override
    public void render( GameContainer c, Graphics g )
        throws SlickException
    {
        World.render( c, xOffs, yOffs );
    }

    @Override
    public void update( GameContainer c, int arg1 )
        throws SlickException
    {
        xOffs += dx;
        if ( ( dx > 0 && xOffs + c.getWidth() > World.width * World.tileWidth ) || ( dx < 0 && xOffs < 0 ) )
            dx = -dx;

        yOffs += dy;
        if ( ( dy > 0 && yOffs + c.getHeight() > World.width * World.tileHeight ) || ( dy < 0 && yOffs < 0 ) )
            dy = -dy;
    }
}
