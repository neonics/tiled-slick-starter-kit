import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONLoader
{
    public static void load( File file )
        throws Exception
    {
        Main.log( "Loading " + file );

        JSONParser parser = new JSONParser();
        Object obj = parser.parse( new FileReader( file ) );
        JSONObject jObj = (JSONObject) obj;

        World.tileWidth = ( (Long) jObj.get( "tilewidth" ) ).intValue();
        World.tileHeight = ( (Long) jObj.get( "tileheight" ) ).intValue();

        JSONArray tilesets = (JSONArray) jObj.get( "tilesets" );
        int nextObjectId = (int) (long) jObj.get( "nextobjectid" );
        for ( int i = 0; i < tilesets.size(); i++ )
            nextObjectId += Resources.addTileSet( nextObjectId, (JSONObject) tilesets.get( i ) );

        JSONArray layers = (JSONArray) jObj.get( "layers" );
        for ( int i = 0; i < layers.size(); i++ )
        {
            JSONObject layer = (JSONObject) layers.get( i );
            String name = (String) layer.get( "name" );
            String type = (String) layer.get( "type" );

            Main.log( "Layer " + ( 1 + i ) + ": name '" + name + "' type: '" + type + "'" );

            switch ( type )
            {
                case "tilelayer":
                {
                    int layerWidth = (int) ( (long) layer.get( "width" ) );
                    int layerHeight = (int) ( (long) layer.get( "height" ) );

                    Object o = layer.get( "data" );

                    int[] ids = new int[layerWidth * layerHeight];

                    if ( o instanceof JSONArray )
                        JSONLoader.parseJSONArray( (JSONArray) o, ids );
                    else if ( o instanceof String && "base64".equals( layer.get( "encoding" ) )
                        && "zlib".equals( layer.get( "compression" ) ) )
                        JSONLoader.parseCompressedArray( (String) o, ids );
                    else
                        throw new RuntimeException( "Unimplemented data type: " + o.getClass().getName() );

                    World.newLayer( name, ids, layerWidth, layerHeight );
                    break;
                }
            }
        }
    }

    static void parseJSONArray( JSONArray array, int[] ids )
    {
        for ( int i = 0; i < array.size(); i++ )
            ids[i] = (int) (long) array.get( i );
    }

    static void parseCompressedArray( String o, int[] ids )
        throws DataFormatException
    {
        Main.log( "Decompressing " + o );
        // base64 decode
        Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode( (String) o );

        // zlib decompress
        Inflater decompresser = new Inflater();
        decompresser.setInput( decodedBytes );
        byte[] decompressedBytes = new byte[ids.length * 4];
        int resultLength = decompresser.inflate( decompressedBytes );
        decompresser.end();
        if ( resultLength != decompressedBytes.length )
            Main.log( "Warning: decompression size mismatch" );

        // convert `byte[N*4]` to `int[N]`
        ByteBuffer bb = ByteBuffer.wrap( decompressedBytes );
        bb.order( ByteOrder.LITTLE_ENDIAN );
        bb.asIntBuffer().get( ids );
    }

}
