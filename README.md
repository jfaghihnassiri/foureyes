Guide to foureyes git: Vuforia code and Streamer code

Table of Contents -
	Getting Started
	Vuforia Code
	Streamer Code
	Next Steps
	Contact

Getting Started - 
	Prerequisits
		Java
		Eclipse
			Egit (optional)
		Android SDK
		Android NDK
		Vuforia
		Cygwin
		Git
	For more details visit the Vuforia installation guide on their website.

Vuforia Code - 
	The vuforia code is based off of a sample application for Vuforia called "Image Targets." This code was initially made to recognize stock images of stones and chips. I've modfied it in the following ways:
	NOTE: If you make changes to the .cpp code you must use cygwin to navigate to the project folder, and type "ndk-build." This will build libraries out of the .cpp code. Only then should you run the android application.
	1. The local phone target database has been modified so that in addition to the stones and chips targets, the pilot image is also a target.
		// In jni/ImageTargets.cpp
		
		// Load the data sets:
		// JFN changed from "StonesAndChips.xml" to "FourEyes.xml"
		if (!dataSetStonesAndChips->load("FourEyes.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
		{
			LOG("Failed to load data set.");
			return 0;
		}
		
	2. I changed which C++ libraries the code is built off of such that I would be able to use vectors in the code
		// In jni/Application.mk
		
		// Added the line below:
		APP_STL := gnustl_static
		
	3. Instead of simply rendering one teapot in the middle of the screen, the code reads size/color/position information from a vector of structs holding that information
		//In jni/ImageTargets.cpp
		
		// Struct to hold information
		struct teaData {
			long id;
			float x;
			float y;
			float size;
			int color;
		};
		
		// Array of the stucts
		std::vector<teaData> teaAry;
		
		// Code that loops and reads the array to render teapots
		// Lots of code added, search the following, should be around line 440
		// JFN MASTER begin master loop for rendering teapots
		
	4. I changed the "working depth" such that augmentations would render for different depth values (more leniant than the values before)
		// In jni/ImageTargets.cpp
		
		projectionMatrix = QCAR::Tool::getProjectionGL(cameraCalibration, 2.0f, 10000.0f); // JFN working depth, changed 2500.0f to 10000.0f
		
	5. I created and exposed add/delete functions which manipulate the vector to the java level of code
		// In jni/ImageTargets.cpp
		
		JNIEXPORT void JNICALL
		Java_com_qualcomm_QCARSamples_ImageTargets_AugmentManager_createAug(JNIEnv*, jobject, jlong id, jint x, jint y, jint color, jfloat size)
		{
			LOG("Java_com_qualcomm_QCARSamples_ImageTargets_AugmentManager_createAug");

			struct teaData tmpPot;
			tmpPot.id = id;
			tmpPot.x = (float)x;
			tmpPot.y = (float)y;
			tmpPot.color = (int)color;
			tmpPot.size = size;
			teaAry.push_back(tmpPot);
		}
	
		JNIEXPORT void JNICALL
		Java_com_qualcomm_QCARSamples_ImageTargets_AugmentManager_deleteAug(JNIEnv*, jobject, jlong id)
		{
			LOG("Java_com_qualcomm_QCARSamples_ImageTargets_AugmentManager_deleteAug");

			int numPots = teaAry.size();
			int toDel = -1;
			for( int i = 0; i<numPots; i++)
			{
				if( teaAry[i].id == id )
				{
					toDel = i;
				}
			}
			if( toDel == -1 )
			{
				LOG("JFN deleteAug id was not found in the vector");
			}
			else
			{
				teaAry.erase(teaAry.begin()+toDel);
			}
		}
		
		// In src/AugmentManager.java
		package com.qualcomm.QCARSamples.ImageTargets;
		import com.qualcomm.QCAR.QCAR;
		public class AugmentManager {
			/** Native function to add a teapot. */
			public native void createAug(long id, int x, int y, int color, float size);
    
			/** Native function to remove a teapot */
			public native void deleteAug(long id);
		}
		
	6. For testing, I added a button which adds five teapots and deletes them in a different order. 
	
	
Streamer Code - 
	The streamer code is a package for Android which uses live555 to stream video over IP. 
	I have thus far been unable to ndk-build the jni code for the streamer, as I get an error. This is where I left off. 
	
Next Steps -
	1. Debug jni code for Streamer and make sure the standalone app works (streams video over ip)
	2. Find way to access phones camera, even though QCAR from Vuforia is accessing it (need fix from Qualcomm for this, hack workaround has 3fps maximum framerate, not good enough)
	3. Find a way to stream metadata (eventually, this will be camera position information) along with each frame, or in a way which is matched with each frame. 
	4. Combine Streamer app functionality into foureyes Vuforia app such that the enhanced vuforia code function, and the screen is sent to the receiving end WITH metadata.
	
Contact - 
	For questions or comments contact:
	Jhon Faghih-Nassiri
	jfaghihnassiri@gmail.com
	619-549-9991