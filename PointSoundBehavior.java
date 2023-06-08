package solar;

import java.net.URL;
import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.MediaContainer;
import org.jogamp.java3d.PointSound;
import org.jogamp.java3d.WakeupCondition;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;

public class PointSoundBehavior extends Behavior {
   private WakeupCondition condition = new WakeupOnElapsedFrames(0);

   public PointSoundBehavior(PointSound ps, URL url, Point3f pos) {
      Bounds b = new BoundingSphere(new Point3d(), 10.0D);
      ps.setSoundData(new MediaContainer(url));
      ps.setPosition(pos);
      float distanceAtZero = 30.0F;
      ps.setDistanceGain(new float[]{0.0F, distanceAtZero}, new float[]{1.0F, 0.0F});
      ps.setEnable(true);
      ps.setPause(false);
      ps.setContinuousEnable(true);
      ps.setSchedulingBounds(b);
      ps.setLoop(-1);
   }

   public void initialize() {
      this.wakeupOn(this.condition);
   }

   public void processStimulus(Iterator<WakeupCriterion> criteria) {
      this.wakeupOn(this.condition);
   }
}