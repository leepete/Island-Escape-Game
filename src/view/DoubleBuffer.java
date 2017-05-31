package view;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

/**
 * Paints everything to an off-screen image then paint it on-screen in one job
 * @author Peter
 *
 */
public class DoubleBuffer extends Canvas {

	private int bufferWidth;
	private int bufferHeight;
	private Image bufferImage;
	private Graphics bufferGraphics;

	/**
	 * Class constructor
	 */
	public DoubleBuffer(){
		super();
	}

	/**
	 * Retrieves graphics off-screen in order to redraw them on-screen very quickly
	 */
	public void paint(Graphics g){
		if(bufferWidth != getSize().width || bufferHeight != getSize().height || //checks the buffersize with the current panel size
				bufferImage == null || bufferGraphics == null){ //initialises the image with the first paint
			resetBuffer();
		}
		
		if(bufferGraphics!=null){
			//clears the offscreen image but not on onscreen
			bufferGraphics.clearRect(0,0,bufferWidth,bufferHeight);

			//calls paintbuffer with the offscreen graphics
			try {
				paintBuffer(bufferGraphics);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//paint the offscreen image onto the onscreen image
			g.drawImage(bufferImage,0,0,this);
		}

	}
	
	/**
	 * Keeps track of image size then recreate the new image within the panel
	 */
	private void resetBuffer(){
		bufferWidth = getSize().width;
		bufferHeight = getSize().height;

		// clean up the previous image
		if(bufferGraphics != null){
			bufferGraphics.dispose();
			bufferGraphics = null;
		}
		if(bufferImage != null){
			bufferImage.flush();
			bufferImage = null;
		}

		System.gc(); //Garbage collector

		bufferImage = createImage(bufferWidth, bufferHeight);
		bufferGraphics = bufferImage.getGraphics();
	} 


	/**
	 * Methods extended here will be used to paint onto canvas
	 * @param g - Off-screen graphics
	 * @throws IOException
	 */
	public void paintBuffer(Graphics g) throws IOException{ 
	
	}

}
