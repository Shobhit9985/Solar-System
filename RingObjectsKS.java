package solar;
import java.awt.Font;
        
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import solar.CollisionDetectShapes;

import java.io.FileNotFoundException;

import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jdesktop.j3d.examples.collision.Box;


public abstract class RingObjectsKS {
	protected Alpha rotationAlpha;                           // NOTE: keep for future use
    protected abstract Node create_Object();	           // use 'Node' for both Group and Shape3D
    public abstract Node position_Object();
    // public Alpha get_Alpha() { return alpha; };    // NOTE: keep for future use 
	// protected static Alpha alpha;
    // public static Transform3D trfm;
    public CollisionDetectShapes cd;
    protected static boolean yy;
    public static TransformGroup newrocket;
}

class StringA2 extends RingObjectsKS {
    private TransformGroup objTG;                              // use 'objTG' to position an object
    private String str;
    private float b;
    private float c;
    Color3f clr;

    public StringA2(String str_ltrs,float bb,float cc,Color3f cl) {
        str = str_ltrs;		
        this.b = bb;
        this.c = cc;
        clr = cl;
        Transform3D scaler = new Transform3D();
        scaler.setScale(0.15);                              // scaling 4x4 matrix 
         Transform3D rot = new Transform3D();
         rot.rotY(Math.PI/2);
        Transform3D tr = new Transform3D();
        tr.mul(scaler);
        tr.mul(rot);
        tr.setTranslation(new Vector3f(0f,(float) b, (float) c));
        objTG = new TransformGroup(tr);
        objTG.addChild(create_Object());		   // apply scaling to change the string's size
    }
    protected Node create_Object() {
        Font my2DFont = new Font("Joker", Font.PLAIN, 1);  // font's name, style, size
        FontExtrusion myExtrude = new FontExtrusion();
        Font3D font3D = new Font3D(my2DFont, myExtrude);		

        Point3f pos = new Point3f(-str.length()/4f, -.3f, 4.9f);// position for the string 
        Text3D text3D = new Text3D(font3D, str, pos);      // create a text3D object
        Appearance app = CommonsKS.obj_Appearance(clr);
        return new Shape3D(text3D,app);
    }
    public Node position_Object() {
        return objTG;
    }
}

class circle extends RingObjectsKS{
	private float r;
    private TransformGroup orbit;
    public circle(float rad){
        r = rad;
        Transform3D rotator = new Transform3D();           // 4x4 matrix for rotation
		rotator.rotZ(Math.PI / 6);         //2.8
		Transform3D trfm = new Transform3D();              // 4x4 matrix for composition

		trfm.mul(rotator);                                 // apply rotation first
		 orbit = new TransformGroup(trfm);
		orbit.addChild(create_Object());
		
    }
	protected Node create_Object() {
		float  z, x;                              // vertices at 0.6 away from origin
		Point3f coor[] = new Point3f[360];                   // declare 15 points for star shape
		LineArray lineArr = new LineArray(720, LineArray.COLOR_3 | LineArray.COORDINATES);
		for (int i = 0; i < 360; i++) {                     // define coordinates for circle shape
			z = (float) Math.cos(Math.PI / 180 * (90 + 1 * i)) * r;
			x = (float) Math.sin(Math.PI / 180 * (90 + 1 * i)) * r;
			coor[i] = new Point3f(x, 0.0f, z);            // use z-value to position star shape
		}
		for (int i = 0; i < 360; i++) {
			lineArr.setCoordinate(i*2 , coor[i]);         // define point pairs for each line
			lineArr.setCoordinate(i*2+1 ,coor[(i+1)%360] );
			lineArr.setColor(i*2 , CommonsKS.White);        // specify color for each pair of points
			lineArr.setColor(i*2+1, CommonsKS.White);
		}
		return new Shape3D(lineArr);                        // create and return a Shape3D
	}
	public Node position_Object() {
		return orbit;
	}
}

class export extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    private String obj_name;
    Color3f clr;
    private float scaling;
    private float aa;
    private float b;
    private float d;

    public export(String obj, Color3f c, float scale, float aa, float b, float d) { 
 
        obj_name = obj;
        clr = c;
        scaling = scale;
        this.aa = aa;
        this.b = b;
        this.d = d;
    }

    protected Node create_Object() {
        ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
        Scene s = null;
        try { // load object's definition file to 's'
            s = f.load("C:\\solarsystem\\soalr\\image\\" + obj_name + ".obj");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ParsingErrorException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IncorrectFormatException e) {
            System.err.println(e);
            System.exit(1);
        }
        objBG = s.getSceneGroup();
        Appearance a = CommonsKS.obj_Appearance(CommonsKS.Orange); // adding the appearance
        a.setTexture(setTexture()); // set the texture to the appearance
        Shape3D sh = (Shape3D) objBG.getChild(0);
        sh.setAppearance(a);
     

        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f((float) aa, (float) b, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);
        // R1.addChild(cd);
        R1.addChild(objBG);
        return R1;
    }

    private Texture setTexture() {
        String filename = "C:\\solarsystem\\soalr\\image\\" + obj_name + ".jpg"; 
        TextureLoader loader = new TextureLoader(filename, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("load failed for texture: " + filename);
        Texture2D texture = new Texture2D(Texture.BASE_LEVEL,
                Texture.RGBA, image.getWidth(), image.getHeight());
        texture.setImage(0, image);
        return texture;
    }

    /* a function to attach the current object to 'objTG' and return 'objTG' */
    public Node position_Object() {
        return create_Object(); // return 'objTG' as object's position
    }
}

class Sun extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Sun(Color3f c, float scale, float d) { // identify object as "Sun.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\sun.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s =  new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        // s.setUserData(1);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Mercury extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Mercury(Color3f c, float scale, float d) { // identify object as "Mercury.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\mercury.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(1);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Venus extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app = new Appearance();

    public Venus(Color3f c, float scale, float d) { // identify object as "Venus.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\venus.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        // app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(2);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Earth extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app = new Appearance();

    public Earth(Color3f c, float scale, float d) { // identify object as "Earth.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\earth.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        // app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency
        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(3);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}
class BoxCol extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    private float b;
    private float c;
    private float x;
    private float y;
    private float z;
    private SoundUtilityJOAL ss;
 
    protected Appearance app = new Appearance();

    public BoxCol(Color3f c, float scale,float b,float cc, float d,float x,float y, float z,SoundUtilityJOAL soundJOAL) { // identify object as "Earth.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        this.b = b;
        this.c = cc;
        this.x  =x;
        this.y  =y;
        this.z =z ;
        ss = soundJOAL;

     

    }

    protected Node create_Object() {
        Transform3D mul= new Transform3D();

        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f((float) b,(float)c,(float) d)); // 0.0f, 0.0f, (float) d 0.5f
        r1.setScale(scaling); // 0.35
        // r1.rotZ(Math.PI/x);
        mul.mul(r1);
        TransformGroup R1 = new TransformGroup(mul);
        
        Shape3D s1 = new Box(y,x, z);   // 0.2f 5.0
        Appearance app1 = s1.getAppearance();
		ColoringAttributes ca = new ColoringAttributes();
        
		ca.setColor(CommonsKS.Red);                     // set column's color and make changeable
		app1.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app1.setColoringAttributes(ca);
      
        R1.addChild(s1);
        cd = new CollisionDetectShapes(s1,ss);
        cd.setSchedulingBounds(CommonsKS.twentyBS);
        R1.addChild(cd);
        if(cd.getCr()){
            yy = true;
        }
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}
// /"C:\\solarsystem\\soalr\\image\\\\mars.jpg"
class Mars extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app = new Appearance();

    public Mars(Color3f c, float scale, float d) { // identify object as "Earth.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\mars.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        // app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency
        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(3);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}
 

class Jupiter extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Jupiter(Color3f c, float scale, float d) { // identify object as "Jupiter.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\jupiter.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(5);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Saturn extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Saturn(Color3f c, float scale, float d) { // identify object as "Saturn.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\saturn.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(6);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Uranus extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Uranus(Color3f c, float scale, float d) { // identify object as "Uranus.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\Uranus.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(7);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Neptune extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Neptune(Color3f c, float scale, float d) { // identify object as "Neptune.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\\\neptune.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(8);
        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Pluto extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    Color3f clr;
    private float scaling;
    private int num;
    private float d;
    Texture t;
    TransparencyAttributes ta;
    protected Appearance app;

    public Pluto(Color3f c, float scale, float d) { // identify object as "Pluto.obj"
        clr = c;
        scaling = scale;
        this.d = d;
        // alpha = new Alpha(-1,5000);

        String file = "C:\\solarsystem\\soalr\\image\\pluto.jpg";
        TextureLoader loader = new TextureLoader(file, null);
        ImageComponent2D image = loader.getImage();
        if (image == null)
            System.out.println("Cannot load file: " + file);
        t = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(),
                image.getHeight());
        t.setImage(0, image);
        ta = new TransparencyAttributes(TransparencyAttributes.FASTEST, 1f);

    }

    protected Node create_Object() {
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f(0.0f, 0.0f, (float) d)); // 0,0.06f,0
        r1.setScale(scaling); // 0.35
        TransformGroup R1 = new TransformGroup(r1);

        app = CommonsKS.obj_Appearance(CommonsKS.White);
        app.setTexture(t); // setting texture
        app.setTransparencyAttributes(ta); // sets transparency

        Sphere s = new Sphere(0.12f, Primitive.GENERATE_TEXTURE_COORDS, 30, app);
        R1.addChild(s);
        s.setUserData(9);

        // R1.addChild(CommonsKS.rotate_Behavior(50, R1,alpha));
        return R1;
    }

    public Node position_Object() {
        return create_Object();
    }

}

class Meteor extends RingObjectsKS {
    protected BranchGroup objBG; // load external object to 'objBG'
    private String obj_name;
    Color3f clr;
    private float aa;
    private float b;
    private float cc;
    private int num;

    public Meteor(String obj, int num,Color3f c,float aa,float bb,float cc) { // identify object as "Ring1.obj"
        obj_name = obj;
        clr = c;
        this.aa = aa;
        b = bb;
        this.cc = cc;
        this.num = num;
    }

    protected Node create_Object() {
        ObjectFile f = new ObjectFile(ObjectFile.RESIZE, (float) (60 * Math.PI / 180.0));
        Scene s = null;
        try { // load object's definition file to 's'
            s = f.load("C:\\solarsystem\\soalr\\image\\"+obj_name + ".obj");
        } catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        } catch (ParsingErrorException e) {
            System.err.println(e);
            System.exit(1);
        } catch (IncorrectFormatException e) {
            System.err.println(e);
            System.exit(1);
        }
        objBG = s.getSceneGroup();
        Appearance a = CommonsKS.obj_Appearance(clr); // adding the appearance
        Shape3D sh = (Shape3D) objBG.getChild(0);
        sh.setAppearance(a);
        sh.setUserData(num);
        // detect column's collision
        Transform3D r1 = new Transform3D();
        r1.setTranslation(new Vector3f((float) aa,(float) b,(float) cc));      //1, 2f, 1
        r1.setScale(0.02);   //0.02
        TransformGroup R1 = new TransformGroup(r1);
        // cd = new CollisionDetectShapes(sh);
        // cd.setSchedulingBounds(CommonsKS.twentyBS); 
        // R1.addChild(cd);
        R1.addChild(objBG);
        return R1;
    }

    /* a function to attach the current object to 'objTG' and return 'objTG' */
    public Node position_Object() {
        return create_Object(); // return 'objTG' as object's position
    }
}

class rocket extends RingObjectsKS	{
	private TransformGroup objSH1 = new TransformGroup();
	private TransformGroup objSH2 = new TransformGroup();
	private TransformGroup objSH3 = new TransformGroup();
	private TransformGroup objSH4 = new TransformGroup();
	private TransformGroup objSH5 = new TransformGroup();
	private TransformGroup objSH6 = new TransformGroup();
	private TransformGroup objTG = new TransformGroup();
	private TransformGroup objTG1 = new TransformGroup();
	private TransformGroup objTG2 = new TransformGroup();
	private BranchGroup objBG = new BranchGroup();
	public static RotationInterpolator rotate_half(int r_num, TransformGroup rotTG,Alpha alpha) {

		rotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis = new Transform3D();
		AxisAngle4f axis = new AxisAngle4f(0.0f,0.0f,1.0f,(float) Math.PI/2);	//rotate around z-axis
		 yAxis.setRotation(axis);
		yAxis.rotZ(Math.PI/6);
		// alpha = new Alpha(-1, r_num);		//speed = 5000 ms
//		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE| Alpha.DECREASING_ENABLE, 0, 0, 5000, 2500, 200, 5000, 2500, 200);
//      RotationInterpolator rot_beh = new RotationInterpolator(rotationAlpha, rotTG, yAxis, -(float) Math.PI/2, (float) Math.PI/2);
		RotationInterpolator rot_beh = new RotationInterpolator(
				alpha, rotTG, yAxis, -(float) Math.PI, (float) Math.PI /5.0f);
		rot_beh.setSchedulingBounds(CommonsKS.hundredBS);
		return rot_beh;
	}
	public BranchGroup rings()	{

		BranchGroup objBG1 = new BranchGroup();
		TransformGroup tran1 = new TransformGroup();
		objBG1.addChild(tran1);
		tran1.addChild(objTG);
		Alpha alpha1 = new Alpha(1, 80000);
		// objBG1.addChild(rotate_half(10000,tran1, alpha1));		//rotating behavior of the 2ndring
		return objBG1;
	}
	public rocket(Alpha alpha)	{
		Transform3D translation = new Transform3D();           
		translation.setTranslation(new Vector3f(0.7f,0f,4.35f  ));		//vector for translation 4.5f 0f, 0f,4.5f   0.7,0,4.2

		Transform3D scaler = new Transform3D();
		scaler.setScale(0.1);			//vector for scaling

        Transform3D trfm = new Transform3D(); 
		trfm.mul(translation); 							// apply translation
		trfm.mul(scaler);                              // apply scaler
		// trfm.rotZ(20);                               
		objTG = new TransformGroup(trfm);			//apply for objTG
		objSH1.addChild(CommonsKS.loadShape("spaceshuttletail", 1,0f,0,3));
		objSH2.addChild(CommonsKS.loadShape("spaceshuttlefront", -1,0,0,3));
		objSH3.addChild(CommonsKS.loadShape("spaceshuttlerear", 0,0,0.2f,3));
		objSH4.addChild(CommonsKS.loadShape("spaceshuttlesection1", -0.95f, 	0.45f,	-0.3f,	3));
		objSH5.addChild(CommonsKS.loadShape("spaceshuttlesection2", -0.95f, 	-0.05f,		-0.45f	,	3));
		objSH6.addChild(CommonsKS.loadShape("spaceshuttlesection3", -0.9f, 	-0.2f,	-0f,	3));
		
		objTG.addChild(objSH1);
		objTG.addChild(objSH2);
		objTG.addChild(objSH3);
		objTG.addChild(objSH4);
		objTG.addChild(objSH5);
		objTG.addChild(objSH6);
		
        // if(yy){
        //     Transform3D tt = new Transform3D();
        //     tt.setTranslation(new Vector3d(10,10,10));
        //     newrocket = new TransformGroup(tt);
        // }
		// //createContent(alpha);
	}
	protected Node create_Object()	{                            
		
		return objBG;                               
	}
	public Node position_Object() {
		return rings();
	}
}
