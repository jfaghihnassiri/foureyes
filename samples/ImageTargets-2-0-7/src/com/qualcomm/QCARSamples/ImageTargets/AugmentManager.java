package com.qualcomm.QCARSamples.ImageTargets;

import com.qualcomm.QCAR.QCAR;

public class AugmentManager {
	/** Native function to update the renderer. */
    public native void createAug(int x, int y, int color, int size);
    
    public void createAugmentation(int x, int y, int color, int size)
    {
    	createAug(x,y,color,size);
    }
}
