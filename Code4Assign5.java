/* *********************************************************
 * For use by students to work on assignments and project.
 * Permission required material. Contact: xyuan@uwindsor.ca 
 **********************************************************/
package solar;

 import java.awt.*;
 import java.awt.event.*;
 import java.awt.image.*;
 import java.util.*;
 
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 
 import org.jogamp.java3d.Locale;
 import org.jogamp.java3d.*;
 import org.jogamp.java3d.Raster;
 import org.jogamp.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
 import org.jogamp.java3d.utils.geometry.Cone;
 import org.jogamp.java3d.utils.geometry.Primitive;
 import org.jogamp.java3d.utils.universe.*;
 import org.jogamp.vecmath.*;
 
 public class Code4Assign5 extends JPanel implements ActionListener {
	 private static final long serialVersionUID = 1L;	
	 static final int width = 500;                            // size of each Canvas3D
	 static final int height =500;
 
	 // use hash table to map the name of a Viewer to its KeyNavigatorBehavior
	 Hashtable<String, KeyNavigatorBehavior>	m_KeyHashtable = null;
	 private Canvas3D[] canvas3D;
	 private  Assignment3KS[] code4Assign4 = new Assignment3KS[3];
	 Hashtable<String, MouseListener> m_MouseHashtable = null;
	//  private static 
	 public Code4Assign5( )	{
		 m_KeyHashtable = new Hashtable<String, KeyNavigatorBehavior>( );
		 m_MouseHashtable = new Hashtable<String, MouseListener>( );
		 
		 canvas3D = new Canvas3D[3];
		//  Assignment3KS.initialSound();
		 
		 GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		 for (int i = 0; i < 3; i++) {
			 canvas3D[i] = new Canvas3D( config );
			 canvas3D[i].setSize( width, height );
			 canvas3D[i].setName("" + i);
			 if (i > 0)
				 code4Assign4[i] = new Assignment3KS(canvas3D[i]);
			 add( canvas3D[i] );                            // add 3 Canvas3D to Frame
		 }		
		 ViewingPlatform vp = new ViewingPlatform(2);       // a VP with 2 TG about it		
		 Viewer viewer = new Viewer( canvas3D[0] );         // point 1st Viewer to c3D[0]
		 Transform3D t3d = new Transform3D( );
		//  t3d.rotX( Math.PI / 2.0 );       
		
		Point3d center = new Point3d(0, 0, 0);             // define the point where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);               // define camera's up direction
		Transform3D view_TM = new Transform3D();
		view_TM.lookAt(new Point3d(22.5d, 0.0d, 1.0d), center, up);
		view_TM.invert();                  // rotate and position the 1st ~
		//  t3d.setTranslation( new Vector3d( 6, 1.7, 8 ) );   // viewer looking down from top
		//  t3d.invert( );
		 MultiTransformGroup mtg = vp.getMultiTransformGroup( );
		 mtg.getTransformGroup(0).setTransform(view_TM );
 
		 SimpleUniverse su = new SimpleUniverse(vp, viewer); // a SU with one Vp and 3 Viewers
		 Locale lcl = su.getLocale();                        // point 2nd/3rd Viewer to c3D[1,2]
		 lcl.addBranchGraph( createViewer( canvas3D[1], "Rocket", CommonsXY.Orange,0f, 0f, 4.5f) );
		 lcl.addBranchGraph( createViewer( canvas3D[2] , "free View", CommonsXY.Cyan,4.5d, 0.0d, 1.0d  ) ); // -3.5, 1, 3.5
 
		 BranchGroup scene = Assignment3KS.create_Scene();                  // create a one-cube scene
		 TransformGroup scene_TG = new TransformGroup();
		 scene.addChild(scene_TG);
		 scene.addChild(CommonsXY.add_Lights(CommonsXY.White, 1));
		 
		 scene.compile();
		 su.addBranchGraph( scene );
	 }
	 public static void  getBranch(){
		
	 }
	 ViewingPlatform createViewer(Canvas3D canvas3D, String name, Color3f clr, 
			 double x, double y, double z) {		
		 // a Canvas3D can only be attached to a single Viewer
		 Viewer viewer = new Viewer( canvas3D );	             // attach a Viewer to its canvas
		 ViewingPlatform vp = new ViewingPlatform( 1 );       // 1 VP with 1 TG above
															  // assign PG to the Viewer
		 vp.setPlatformGeometry( labelPlatformGeometry( name ) );
		//  viewer.setAvatar( createViewerAvatar( name, clr ) ); // assign VA to the Viewer
 
		 Point3d center = new Point3d(0, 0, 0);               // define where the eye looks at
		 Vector3d up = new Vector3d(0, 1, 0);                 // define camera's up direction
		 Transform3D viewTM = new Transform3D();
		 Point3d eye = new Point3d(x, y, z);                  // define eye's location
		 viewTM.lookAt(eye, center, up);
		 viewTM.invert();  
		 vp.getViewPlatformTransform().setTransform(viewTM);  // set VP with 'viewTG'
		//  vp.getViewPlatformTransform().addChild();
		 // set TG's capabilities to allow KeyNavigatorBehavior modify the Viewer's position
		 vp.getViewPlatformTransform( ).setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		 vp.getViewPlatformTransform( ).setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		 KeyNavigatorBehavior key = new KeyNavigatorBehavior( vp.getViewPlatformTransform( ) );
		 key.setSchedulingBounds( new BoundingSphere() );          // enable viewer navigation
		 key.setEnable( false );		
		 vp.addChild( key );                                   // add KeyNavigatorBehavior to VP
		 viewer.setViewingPlatform( vp );                      // set VP for the Viewer	
		 m_KeyHashtable.put( name, key );                      // label the Viewer	
		 Button button = new Button( name ); 
		 button.addActionListener( this );                     // button to switch the Viewer ON
		 add( button );
 
		 return vp;
	 }
 
	 /* a function to create and position a simple Cone to represent the Viewer */
	 ViewerAvatar createViewerAvatar( String szText, Color3f objColor ) {
		 ViewerAvatar viewerAvatar = new ViewerAvatar( );
		 // lay down the Cone, pointing sharp-end towards the Viewer's field of view
		 TransformGroup tg = new TransformGroup( );
		 Transform3D t3d = new Transform3D( );
		 t3d.setEuler( new Vector3d( Math.PI / 2.0, Math.PI, 0 ) );
		 tg.setTransform( t3d );
		 
		 Appearance app = CommonsXY.obj_Appearance(objColor);
		 
		//  tg.addChild( new Cone( 0.5f, 1.5f, Primitive.GENERATE_NORMALS, app ) );
		//  viewerAvatar.addChild( tg );                         // add Cone to parent BranchGroup
 
		 return viewerAvatar;
	 }
 
	 PlatformGeometry labelPlatformGeometry( String szText ) {		
		 PlatformGeometry pg = new PlatformGeometry( );
		 pg.addChild( createLabel( szText, 0f, 0.5f, 0f ) );    // label the PlatformGeometry ~
		 return pg;                                           // to help identify the viewer
	 }
 
	 // creates a simple Raster text label (similar to Text2D)
	 private Shape3D createLabel( String szText, float x, float y, float z )	{
		 BufferedImage bufferedImage = new BufferedImage( 25, 14, BufferedImage.TYPE_INT_RGB );
		 Graphics g = bufferedImage.getGraphics( );
		 g.setColor( Color.white );
		 g.drawString( szText, 2, 12 );
 
		 ImageComponent2D img2D = new ImageComponent2D(ImageComponent2D.FORMAT_RGB, bufferedImage);		
		 Raster renderRaster = new Raster(new Point3f( x, y, z ), Raster.RASTER_COLOR,
			 0, 0, bufferedImage.getWidth( ), bufferedImage.getHeight( ), img2D,	null );
		 return new Shape3D( renderRaster );                  // create the Raster for the image
	 }
 
 
	 /* a function to enable the KeyNavigatorBehavior associated with the selected AWT button and 
	  * disables all other KeyNavigatorBehaviors for non-active Viewers. */
	 public void actionPerformed( ActionEvent event ) {
		 KeyNavigatorBehavior key = (KeyNavigatorBehavior)m_KeyHashtable.get(event.getActionCommand());
		 Object[] keysArray = m_KeyHashtable.values( ).toArray( );
		 for( int n = 0; n < keysArray.length; n++ )	{
			 KeyNavigatorBehavior keyAtIndex = (KeyNavigatorBehavior) keysArray[n];
			 keyAtIndex.setEnable( keyAtIndex == key );
			 if( keyAtIndex == key ) {
				 if (n == 1) {
					 canvas3D[1].addMouseListener(code4Assign4[1]);
					 canvas3D[1].addKeyListener(code4Assign4[1]);
					 Assignment3KS.playSound(1);
					 canvas3D[2].removeMouseListener(code4Assign4[2]);
					 canvas3D[2].removeKeyListener(code4Assign4[2]);

				 }
				 else {
					 canvas3D[2].addMouseListener(code4Assign4[2]);
					 canvas3D[2].addKeyListener(code4Assign4[2]);
					 Assignment3KS.playSound(2);
					 canvas3D[1].removeMouseListener(code4Assign4[1]);
					 canvas3D[1].removeKeyListener(code4Assign4[1]);

				 }
			 }
		 }
	 }
 
	 public static void main( String[] args ) { 
		 JFrame frame = new JFrame("Code for Assignment 5");
		 frame.getContentPane().add(new Code4Assign5()); // create an instance of the class
		 frame.setSize(1910, height + 40);                         // set the size of the JFrame
		 frame.setVisible(true);
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 }
	 
 }	
 
