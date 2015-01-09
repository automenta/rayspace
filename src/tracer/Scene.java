/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tracer;

import java.util.List;
import tracer.objects.SceneObject;

/**
 *
 * @author me
 */
public abstract class Scene {

    public abstract List<SceneObject> updateScene(double time);
    
}
