package filters;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
/**
 *
 * @author Igor Dzinka
 */
public class NegativeFilter {
   /**
    * Constructor
    */
    public NegativeFilter(){}
   
    /**
     * Function for applying a Negative Filter on a image
     * @param img The image to process
     * @return  An image with a Negative Filter applied
     */
   public BufferedImage applyNegativeFilter(BufferedImage img){
       int imgWidth, imgHeight;
       imgWidth = img.getWidth();
       imgHeight = img.getHeight();
       //convert to negative
       for(int i = 0; i < imgHeight; i++){
           for(int j = 0; j < imgWidth; j++){
               
               int p = img.getRGB(j, i);//take pixel RGB value
               
               int a =(p>>24)&0xff;     //image color information
               
               int r = (p>>16)&0xff;    //red channel value
               int g = (p>>8)&0xff;     //green channel value
               int b = p&0xff;          //blue channel value
               
               //transforming image into a negative
               r = 255 - r;
               g = 255 - g;
               b = 255 - b;
               
               p = (a << 24)|(r<<16)|(g<<8)|b;
               
               img.setRGB(j, i, p);
           }
       }
       return img;
   }
   
   /**
    * Function to process an array of images with a Negative Filter
    * @param fileList Array to process
    * @return Processed array
    */
    public ArrayList applyNegativeFilterToVideo(ArrayList<BufferedImage> fileList){
        ArrayList filtered = new ArrayList<>();
        
        for (int i = 0; i < fileList.size(); i++){
            filtered.add(applyNegativeFilter(fileList.get(i)));
        }

        return filtered;
     }
}
