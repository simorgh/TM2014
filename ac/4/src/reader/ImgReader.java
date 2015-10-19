import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor
 */
public class ImgReader {
    
    /**
     * Funcion que abre un fichero de imagen  y la convierte en una String de 0's y 1's
     * @param ImageName
     * @return 
     */
    public static String imgToString(String ImageName){
        ArrayList img;
        int mval = (int) Math.pow(2, 8)-1;
        img = new ArrayList();
        try {
            img = extractBytes(ImageName);
        } catch (IOException ex) {
            Logger.getLogger(ImgReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        String output = "";
        for (Object img1 : img) {
            int p = (int) img1;
            output+=int2bin(p,mval);
        }
        return output;
    }
   /**
    * 
    * @param ImageName Ruta de la imagen a tratar
    * @return Una lista de enteros con valores de canal R G B de cada pixel concatenados
    * @throws IOException 
    */
    public static ArrayList extractBytes (String ImageName) throws IOException {
        ArrayList channelR = new ArrayList();
        ArrayList channelG = new ArrayList();
        ArrayList channelB = new ArrayList();
        int imageHeight,imageWidth;
        
        // open image
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        
        imageHeight = bufferedImage.getHeight();
        imageWidth = bufferedImage.getWidth();
        int i = 0;
        for(int y = 0; y < imageHeight;y++){
            for(int x = 0;x < imageWidth; x++){
               Color c = new Color(bufferedImage.getRGB(x,y));
                channelR.add(c.getRed());
                channelG.add(c.getGreen());
                channelB.add(c.getBlue());
                i++;
            }
        }
       channelR.addAll(channelG);
       channelR.addAll(channelB);
       return channelR;
        

    }
	
    /**
     * @param valor numero entero a codificar en binario natural
     * @param maxval valor del màximo entero codificable (determina el número de bits con que se codificara valor)
     * @return output cadena binaria al código binario natural de valor
     */
    public static String int2bin(int valor, int maxval) {
        int numbits = getNumBits(maxval);
        String binstring = Integer.toBinaryString((1 << 31) | (valor));
        return (binstring.substring(binstring.length() - numbits));
    }
	
    /**
     * @param valor número entero
     * @return output número de bits necesarios para codificar entero en binario natural
     */
    public static int getNumBits(int valor) {
        return (Integer.toBinaryString(valor).length());
    }
}
