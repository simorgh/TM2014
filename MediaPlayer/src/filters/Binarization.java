package filters;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.util.ArrayList;

/**
 *
 * @author Vicent
 */
public class Binarization  {
    public static int THRESHABOVE = 255;
    public static int THRESHBELOW = 0;
    private Raster data;
    private DataBuffer datab;
    private int width, height;
    private int threshold;
    private int datav [];
    private int datanew[];
    
    /**
     * Creates a new instance of Binirization.
     * @param threshold
     */
    public Binarization(int threshold) {
        this.threshold = threshold;
    }
      
    public static int getColor(int b, int g, int r) {
        return (int)Math.pow(2,16)*r + 256*g + b;
    }
    
    public void setThreshold(int threshold){
        this.threshold=threshold;
    }
      
    public int getWidth(){
        return data.getWidth();
    }
      
    public int getHight(){
        return data.getHeight();
    }
    
    
    /**
    * Function to process an array of images with binarization
    * @param fileList Array to process
    * @return Processed array
    */
    public ArrayList<BufferedImage> applyBinarizationToVideo(ArrayList<BufferedImage> fileList){
        ArrayList filtered = new ArrayList<>();
        for (BufferedImage img : fileList) {
            filtered.add(doBinirization(img));
        }
        
        return filtered;
    }
    
    
    public BufferedImage doBinirization(BufferedImage bufi){
        this.width = bufi.getWidth();
        this.height = bufi.getHeight();
        BufferedImage bi = new BufferedImage(this.width, this.height, bufi.getType());
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                int rgb = bufi.getRGB(i,j);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb) & 0xff;

                // Calculate the brightness
                if(r>threshold||g>threshold||b>threshold){
                    r=255;
                    g=255;
                    b=255;
                } else {
                    r=0;
                    g=0;
                    b=0;
                }

                // Return the result
                rgb = (rgb & 0xff000000) | (r << 16) | (g << 8) | (b);
                bi.setRGB(i, j, rgb);
            }
        }
        return bi;
    }
    
}
