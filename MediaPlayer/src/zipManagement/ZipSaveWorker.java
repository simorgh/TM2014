/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zipManagement;

import gui.MediaPlayer;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Vicent Roig
 */
public class ZipSaveWorker implements Runnable{
    public static ZipOutputStream out=null;
    private ArrayList<BufferedImage> images;
    private static int counter=0;


    @Override
    public void run() {
        for (BufferedImage image : images) {
            ZipEntry entry = new ZipEntry("video"+counter+".jpg");
            
            try {
                out.putNextEntry(entry);
                ImageIO.write(image, "jpg", out);
                System.out.println("\tAdded image " + image.hashCode() + " to ZipFile");
            } catch (IOException ex) {
                Logger.getLogger(ZipSaveWorker.class.getName()).log(Level.SEVERE, null, ex);
                this.closeStream();
                return;
            }
            counter++;
        }
        this.closeStream();
    }

    public ZipSaveWorker(ArrayList<BufferedImage> images,  File file){
        if (out==null){
            String file_name = file.getName();
            if(!file_name.endsWith(".zip")){
                file_name = file.getName() + ".zip";
            }
                
            try {
                out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file_name, true)));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ZipSaveWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            counter=0;
        }
        
        this.images = images;
    }

    
    private void closeStream(){
        try {
            out.flush();
            out.close();
            out = null;
            System.out.println("Zip Stream closed");
        } catch (IOException ex) {
            Logger.getLogger(ZipSaveWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public static ArrayList<BufferedImage> readZip(File file) throws Exception{
        ZipFile zFl = null;
        ArrayList <BufferedImage> images  = new ArrayList<>();
        try {
            zFl = new ZipFile(file);
        } catch (ZipException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        Enumeration<? extends ZipEntry> entries = zFl.entries();
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream is = null;
            try {
                is = zFl.getInputStream(entry);
            } catch (IOException ex) {
                Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ImageInputStream iis = null;
            try {
                iis = ImageIO.createImageInputStream(is);
            } catch (IOException ex) {
                Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                BufferedImage bufImg = ImageIO.read(iis);
                images.add(bufImg);
            } catch (IOException ex) {
                Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        zFl.close();
        return images;
    }
    
}