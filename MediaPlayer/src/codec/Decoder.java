package codec;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;
import zipManagement.ZipSaveWorker;

/**
 *
 * @author Vicent Roig, Igor Dzinka
 */
public class Decoder {
    public static final int BUFFER_SIZE = 1024;
    private static ArrayList <Byte> builder;
    private static ArrayList<BufferedImage> compressed;
    private static int index;
    
    public Decoder(){ /*...*/ }
    
    
    /**
     * Decoding process. 
     * 
     * @param packed Tar file that contains the compressed video  file
     *               and the  gzip with a info neccessary to decode the video
     * @return Array of decoded video frames
     */
    public static ArrayList <BufferedImage> decode(File packed){   
        System.out.println("@decode receives "+ packed.getName());
        try {
            untar(packed);
        } catch (Exception ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        //printBuilder();
        build();
        return compressed;
    }
    
     /**
     * Method that reconstructs the video frames using the builder skeleton
     * 
     * BUILDER SKELETON STRUCT. (each X represents 1 byte)
     *    X   /   X   /   X   |    X    /    XX    /     XX XX            XX      [...] |    X    (etc) 
     *< brick    gop    rows  |  pframe   matches    pf match (x,y)   iF N-chunk  [...] |  pframe (etc)>
     */
    private static void build(){
        index = 0;
        
        System.out.println("@build -> Reconstructing video...");
        // load builder skeleton header:
        byte brick = builder.get(index++);
        byte gop = builder.get(index++);
        byte rows = builder.get(index++);
/*        
        System.out.println( "--------------- BUILDER HEADER ---------------" +
                "\n[ " + " brick " + brick + " | grop " + gop + " | cols " + cols + " | rows " + rows + " ]\n"
                + "--------------- BUILDER HEADER ---------------" );
*/        
        while(index < builder.size()){
            byte k = builder.get(index++);
            short matches = getNextShort();
            //System.out.println("\tk " + k + " -> matches " + matches);
            //loop over matches
            for(int i=0; i < matches; i++){
                short coord[] = getNextCoord();
                short h = getNextShort();
                //System.out.println("\t\t(x, y) " + coord[0] + ", " + coord[1] + " / h " + h);
                /**                    
                (0,0) (0,1) (0,2) (0,3) (0,4)
                  0     1     2     3     4
                (1,0) (1,1) (1,2) (1,3) (1,4)
                  5     6     7     8     9  */
                BufferedImage chunk = compressed.get((k/gop)*gop).getSubimage((h%rows)*brick, (h/rows)* brick, brick, brick);
                Graphics2D g2 = compressed.get(k).createGraphics(); //pframe drawer
                g2.drawImage(chunk, coord[0], coord[1], null);
            }
        }
        builder.clear();
        System.out.println("@build -> Reconstruction DONE!");
    }
    
    
    
    /**
     * Method that prints the content of builder byte array
     */
    private static void printBuilder(){
        for (byte b : builder) {
            System.out.print(String.format("%02X ", b));
        }
    }
            
    /**
     * 
     * @see /codec/Encoder.serializeCoord()
     * @return 
     */
    private static short[] getNextCoord(){
        short coord[] = new short[2];
        for(int i=0; i<2; i++) coord[i] = getNextShort();
        return coord;
    }
    
     /**
     * Method to get next short value from the builder byte array
     * @return short  extracted from builder
     */
    private static short getNextShort(){
        //(short) ( ((hi & 0xff) << 8) |  (lo & 0xff) )
        return (short) ( (builder.get(index++) & 0xff) |  ((builder.get(index++) & 0xff) << 8 ) );
    }
    
    /**
     * Recovery for builder skeleton & base JPEG sequence of frames
     * 
     * @param encoded   File .tar codified with Encode.java
     * @return          result decompressed frame sequence
     */
    private static File[] untar(File encoded) throws Exception{
        String tarFile = encoded.getName();
        File[] output = new File[2];
        
        // Create a TarInputStream
        TarInputStream tis;
        try {
            tis = new TarInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
            
            TarEntry entry;
            int id = 0;
            while((entry = tis.getNextEntry()) != null) {
    
                byte data[] = new byte[BUFFER_SIZE];           
                FileOutputStream fos;
                fos = new FileOutputStream(entry.getName());
                BufferedOutputStream dest = new BufferedOutputStream(fos);
                
                int count;
                while((count = tis.read(data)) != -1) {
                  dest.write(data, 0, count);
                }
                
                dest.flush();
                dest.close();
                
                //Once extracted read as tmp files. We should delete them after processing.
                System.out.print("\t File " + entry.getName() + " extracted" );
                File tmp = new File(entry.getName());
                if(id==0){
                    System.out.println("\t\t-> @readZip");
                    compressed = ZipSaveWorker.readZip(tmp);
                } else if(id==1 && entry.getName().equals(Encoder.BUILDER_FNAME)){
                    System.out.println("\t-> @decompressGzipFile");
                    builder = decompressGzipFile(entry.getName(), BUFFER_SIZE);
                }
                tmp.delete();
                id++;
            }
            tis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(Decoder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return output;
    }

    
    
    ////////////////////////////////////////////////////    
    //               GZIP skeleton recover            //
    ////////////////////////////////////////////////////

    /**
     * Get byte array from GZip file
     * -------------------------------
     * @param	file .tar codified with Encode.java
     * @param	bufferlength size for buffering bytes. Recommended 1024/2048
     * @return	result decompressed frame sequence
     */
    private static ArrayList <Byte> decompressGzipFile(String filename, int bufferlength) {
        ArrayList <Byte> data = new ArrayList<>();
        byte[] buffer = new byte[bufferlength];
        try {
            FileInputStream fis = new FileInputStream(filename);
            GZIPInputStream gis = new GZIPInputStream(fis);
            
            int bytes_read = -1;
            while ((bytes_read = gis.read(buffer)) != -1){
                for(int i=0; i<bytes_read; i++) data.add(buffer[i]);
            }
            
            //bos.close();
            gis.close();
            fis.close();
            System.out.println("The file was decompressed successfully!");
        } catch (IOException e) {
            return null;
        }
        
        return data;
    }
    

}
