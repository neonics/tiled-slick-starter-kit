import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Main
{
    protected static void log( String s )
    {
        System.out.println( "[LOG] " + s );
    }

    private static void log( String s, SlickException e )
    {
        System.out.println( "[LOG] " + s );
        e.printStackTrace( System.out );
    }

    public static void main( String[] args )
        throws Exception
    {
        File file = args.length > 0 ? new File( args[0] ) : selectFile();
        if ( file == null || !file.exists() )
            return;

        try
        {
            AppGameContainer appgc;
            appgc = new AppGameContainer( new TestGame( "Tiled Slick Starter Kit", file ) );
            appgc.setDisplayMode( 640, 480, false );
            appgc.setTargetFrameRate( 60 );
            appgc.start();
        }
        catch ( SlickException e )
        {
            log( "Error", e );
        }
    }

    private static File selectFile()
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException
            | UnsupportedLookAndFeelException e )
        {
            e.printStackTrace();
        }

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory( new File( "" ).getAbsoluteFile() );
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fc.setFileFilter( new FileFilter()
        {
            @Override
            public String getDescription()
            {
                return "Tiled JSON files (*.json)";
            }

            @Override
            public boolean accept( File f )
            {
                return f.isDirectory() || f.getName().endsWith( ".json" );
            }
        } );
        switch ( fc.showOpenDialog( null ) )
        {
            case JFileChooser.APPROVE_OPTION:
                return fc.getSelectedFile();

        }
        return null;
    }
}
