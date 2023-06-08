package solar;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.LOD;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnAWTEvent;
import org.jogamp.vecmath.Vector3f;

public class BehaviorArrowKey extends LOD{

	private TransformGroup chaseTG;
	private WakeupOnAWTEvent wEnter;	
	
	public BehaviorArrowKey(TransformGroup chasedTG) {
		chaseTG = chasedTG;
	}
	
	public void initialize() {
		// TODO Auto-generated method stub
		wEnter = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
		wakeupOn(wEnter);
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> arg0) {
		// TODO Auto-generated method stub
		Transform3D newrot = new Transform3D();
		chaseTG.getTransform(newrot);
		Vector3f vct = new Vector3f();
		newrot.get(vct); vct.y += 0.5d; vct.z -= 0.5d; vct.x +=0.5d;
		
		wakeupOn(wEnter);
	}

}
