import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

class Resources
{
    private static class Sheet
    {
        int startId;

        int length;

        SpriteSheet sheet;

        public Sheet( int startId, SpriteSheet sheet )
        {
            this.startId = startId;
            this.sheet = sheet;
            this.length = sheet.getWidth() * sheet.getHeight();
        }

        public boolean contains( int index )
        {
            return index >= startId && index < startId + length;
        }

        public Image getTile( int index )
        {
            index -= startId;
            int y = index / sheet.getHorizontalCount();
            int x = index % sheet.getHorizontalCount();
            return sheet.getSubImage( x, y );
        }
    }

    static Set<Sheet> sheets = new HashSet<>();

    /**
     * @param startId
     * @param o
     * @return the size of the tile set, for updating startId
     */
    static int addTileSet( int startId, JSONObject o )
    {
        try
        {
            Sheet x = new Sheet(
                startId,
                new SpriteSheet(
                    (String) o.get( "image" ),
                    ( (Long) o.get( "tileheight" ) ).intValue(),
                    ( (Long) o.get( "tilewidth" ) ).intValue() ) );

            sheets.add( x );

            return x.length;
        }
        catch ( NumberFormatException | SlickException e )
        {
            throw new RuntimeException( "Error adding TileSet", e );
        }
    }

    private static Sheet getSheetForId( int index )
    {
        for ( Sheet s : sheets )
            if ( s.contains( index ) )
                return s;
        return null;
    }

    static Image getSpriteImage( int index )
    {
        Sheet s = getSheetForId( index );

        if ( s == null )
            return null;

        return s.getTile( index );
    }

}
