package filters;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

/**
 * Convolution is the code for applying the convolution operator.
 *
 * @author: Vicent Roig
 */
public class Convolution extends Thread {
    public static enum kernel { SOBEL_X, SOBEL_Y, LAPLACIAN, AVERAGE };
    
    private final Kernel sobel_x = new Kernel(3, 3, new float[] { -1f, 0f, 1f, -2f, 0f, 2f, -1f, 0f, 1f });
    private final Kernel sobel_y = new Kernel(3, 3, new float[] { -1f, -2f, -1f, 0f, 0f, 0f, 1f, 2f, 1f });
    private final Kernel laplacian = new Kernel(3, 3, new float[] { -1f,-1f,-1f, -1f, 8f, -1f, -1f, -1f, -1f});
    private final Kernel average = new Kernel(3, 3, new float[] { 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f});
  
    public Convolution() { /*...*/ }

    public ArrayList<BufferedImage> applyFilterToVideo(kernel k, ArrayList<BufferedImage> fileList){
        ArrayList filtered = new ArrayList<>();

        for (BufferedImage img : fileList) {
            filtered.add(convolve(k,img));
        }
        return filtered;
    }
    
    private BufferedImage convolve(kernel k, BufferedImage image){
        BufferedImageOp op = null;
        switch(k){
            case SOBEL_X:
                op = new ConvolveOp(sobel_x);
                break;
            case SOBEL_Y:
                op = new ConvolveOp(sobel_y);
                break;
            case LAPLACIAN:
                op = new ConvolveOp(laplacian);
                break;
            case AVERAGE:
                op = new ConvolveOp(average);
                break;
            }
        return op.filter(image, null);
    }
}