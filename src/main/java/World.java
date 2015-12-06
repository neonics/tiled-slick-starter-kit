import java.util.LinkedHashMap;
import java.util.Map;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

public class World
{

    public static int width;

    public static int height;

    public static int tileWidth;

    public static int tileHeight;

    /**
     * @param c
     * @param xOffs top-left x coordinate of window showing part of map
     * @param yOffs top-left y coordinate of window showing part of map
     */
    public static void render( GameContainer c, float xOffs, float yOffs )
    {
        for ( Layer layer : layers.values() )
            layer.render( c, xOffs, yOffs );
    }

    static class Layer
    {
        Image[][] image;

        int w, h;

        public Layer( int w, int h, int[] ids )
        {
            Main.log( "new Layer: " + w + "x" + h + " ids: " + ids.length );
            this.w = w;
            this.h = h;
            this.image = new Image[w][h];

            for ( int y = 0; y < h; ++y )
                for ( int x = 0; x < w; ++x )
                    image[x][y] = Resources.getSpriteImage( ids[y * w + x] );
        }

        public void render( GameContainer c, float xOffs, float yOffs )
        {
            // convert to tile coordinates
            int xt = (int) ( -xOffs / tileWidth );
            int yt = (int) ( -yOffs / tileHeight );
            float px = -xOffs - xt * tileWidth;
            float py = -yOffs - yt * tileHeight;

            for ( int y = 0; y <= 1 + c.getHeight() / tileHeight; y++ )
                for ( int x = 0; x <= 1 + c.getWidth() / tileWidth; x++ )
                {
                    int a = x - xt, b = y - yt;

                    if ( inBounds( a, b ) && image[a][b] != null )
                        image[a][b].draw( px + x * tileWidth, py + y * tileHeight, tileWidth, tileHeight );
                }
        }

        private boolean inBounds( int x, int y )
        {
            return x >= 0 && x < image.length && y >= 0 && y < image[0].length;
        }
    }

    static Map<String, Layer> layers = new LinkedHashMap<>();

    static void newLayer( String name, int[] array, int w, int h )
    {
        Main.log( "Loading layer " + name + " " + w + "x" + h );
        layers.put( name, new Layer( w, h, array ) );

        if ( width < w )
            width = w;
        if ( height < h )
            height = h;
    }

}
