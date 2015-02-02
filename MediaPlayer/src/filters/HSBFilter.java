/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the GNU GENERAL PUBLIC LICENSE.
 *******************************************************************************/
package filters;

/**
 *
 * @author Vicent Roig
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;

/**
 * HSBFilter.
 *
 */
public class HSBFilter extends RGBImageFilter{
    /**
     * the Hue of the indicated new foreground color.
     */
    float hue = 0;

    /**
     * the Saturation of the indicated new foreground color.
     */
    float saturation = 100;

    /**
     * the Brightness of the indicated new foreground color.
     */
    float brightness = 100;

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public HSBFilter(int hue, int sat, int bright){
        this.hue=hue;
        this.saturation= sat;
        this.brightness = bright;
        canFilterIndexColorModel = true;
    }

    /**
     * Construct a HueFilter object which performs color modifications to warp
     * existing image colors to have a new primary hue.
     * @param fg
     */
    public HSBFilter(Color fg) {
        float hsbvals[] = Color.RGBtoHSB(fg.getRed(), fg.getGreen(), fg.getBlue(), null);
        this.hue = hsbvals[0];
        this.saturation = hsbvals[1];
        this.brightness = hsbvals[2];
        canFilterIndexColorModel = true;
    }

    /**
     * Filter an individual pixel in the image by modifying its hue, saturation,
     * and brightness components to be similar to the indicated new foreground
     * color.
     * @return 
     */
    @Override
    public int filterRGB(int x, int y, int rgb) {
        int alpha = (rgb >> 24) & 0xff;
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = (rgb) & 0xff;
        float [] hsbValue = Color.RGBtoHSB(red, green, blue, null);
        float newHue = hsbValue[0] + (float)(this.hue/360.); //* (float)((360-hue)/360.);
        float newSaturation = hsbValue[1] + (float)(saturation/100.);
        float newBrightness = hsbValue[2] + (float)(brightness/100.);
        if(newHue>1){
                newHue = 1;
        }
        if(newHue<0){
            newHue = 0;
        }
        if(newSaturation>1){
            newSaturation = 1;
        }
        if(newSaturation<0){
            newSaturation = 0;
        }
        if(newBrightness>1){
            newBrightness = 1;
        }
        if(newBrightness<0){
            newBrightness = 0;
        }
        rgb = Color.HSBtoRGB(newHue, newSaturation, newBrightness);
        return (rgb & 0x00ffffff) | (alpha << 24);
    }

    public Color convert(Color color) {
        return new Color(filterRGB(0, 0, color.getRGB()));
    }
    
    /**
    * Function to process an array of images with a HSBFilter
    * @param fileList Array to process
    * @return Processed array
    */
    public ArrayList<BufferedImage> applyHSBFilterToVideo(ArrayList<BufferedImage> fileList){
        ArrayList filtered = new ArrayList<>();
        
        for (BufferedImage img : fileList) {
            filtered.add(applyHSBFilter(img));
        }

        return filtered;
    }
    
     /**
     * Function for applying a HSBFilter on a image
     * @param img The image to process
     * @return  An image with a HSBFilter applied
     */
    public BufferedImage applyHSBFilter(BufferedImage img){
        int imgWidth, imgHeight;
        imgWidth = img.getWidth();
        imgHeight = img.getHeight();
        
        for(int i = 0; i < imgHeight; i++){
            for(int j = 0; j < imgWidth; j++){
                img.setRGB(j, i, filterRGB(j, i, img.getRGB(j, i)) );
            }
       }
       return img;
    }
}
