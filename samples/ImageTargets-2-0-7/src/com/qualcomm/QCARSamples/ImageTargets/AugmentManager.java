package com.qualcomm.QCARSamples.ImageTargets;

import com.qualcomm.QCAR.QCAR;

public class AugmentManager {
	/** Native function to add a teapot. */
    public native void createAug(long id, int x, int y, int color, float size);
    
    /** Native function to remove a teapot */
    public native void deleteAug(long id);

}
