package gui;

import codec.Decoder;
import codec.Encoder;
import filters.Binarization;
import filters.Convolution;
import filters.HSBFilter;
import filters.NegativeFilter;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import zipManagement.ZipSaveWorker;
/**
 *
 * @author Igor Dzinka, Vicent Roig
 */
public class MediaPlayer extends javax.swing.JFrame {
    // Specify the look and feel to use by defining the LOOKANDFEEL constant
    // Valid values are: null (use the default), "Metal", "System", "Motif",
    // and "GTK"
    final static String LOOKANDFEEL = "Motif";
    final static int DEFAULT_SPEED = 24;
    final static int MAX_FPS = 100;
    /** 
     * If you choose the Metal L&F, you can also choose a theme.
     * Specify the theme to use by defining the THEME constant
     * Valid values are: "DefaultMetal", "Ocean",  and "Test"
     * */
    final static String THEME = "Ocean";
    private ProgressDialog progressDialog;
    private static enum PlayMode { BACKWARD, FORWARD }
    private int reproduction_mode;
    private boolean playing = true;
    private boolean  isFiltered = false;
    private File imgFile;
    private File zipFile;
    private ArrayList<BufferedImage> filesFromZip;
    private ArrayList<BufferedImage> filtered;
    private int indexImg;
    private Timer th;
    private int fps = DEFAULT_SPEED;
    private DialogAbout aboutUs;
    private DialogHSB hsbHandling;
    private DialogEncode encodeHandling;
    
    /**
     * Creates new form MediaPlayer.
     */
    public MediaPlayer() {
        this.setTitle(MediaPlayer.class.getSimpleName());
        this.setIconImage(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/Application16.gif")).getImage());
        this.pack();
        
        initLookAndFeel();
        initComponents();
    }
    
    /**
     * Look And Feel initialization
     */
    private void initLookAndFeel() {
        String lookAndFeel = null;
       
        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
                //  an alternative way to set the Metal L&F is to replace the 
                // previous line with:
                // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
            }  else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            }  else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK")) { 
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "+ LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);
                
                // If L&F = "Metal", set the theme
                if (LOOKANDFEEL.equals("Metal")) {
                  if (THEME.equals("DefaultMetal"))
                     MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                  else if (THEME.equals("Ocean"))
                     MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                  UIManager.setLookAndFeel(new MetalLookAndFeel()); 
                }	  
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (InstantiationException | IllegalAccessException e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
            }
        }
    }


    
    
    /**
     * Image resizing method
     * @param srcImg Input Image
     * @param w New width
     * @param h New Height
     * @return  Resized Image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
            BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();
            return resizedImg;
    }
   
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cntrlPanel = new javax.swing.JPanel();
        btnPlay = new javax.swing.JButton();
        btnStepBack = new javax.swing.JButton();
        btnStepForward = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnRewind = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        progress = new javax.swing.JSlider();
        imgArea = new javax.swing.JLabel();
        Menu = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpenImage = new javax.swing.JMenuItem();
        jMenuItemLoadFile = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuEncode = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuFilters = new javax.swing.JMenu();
        jMenuItemNegative = new javax.swing.JMenuItem();
        jMenuItemBinarize = new javax.swing.JMenuItem();
        jMenuItemHSB = new javax.swing.JMenuItem();
        JMenuItemAverage = new javax.swing.JMenuItem();
        jMenuItemLaplacian = new javax.swing.JMenuItem();
        jMenuItemSobelX = new javax.swing.JMenuItem();
        jMenuItemSobelY = new javax.swing.JMenuItem();
        jMenuItemUndo = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(740, 512));
        setPreferredSize(new java.awt.Dimension(740, 512));
        setResizable(false);

        cntrlPanel.setLayout(new java.awt.GridLayout(1, 0));

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play24.gif"))); // NOI18N
        btnPlay.setEnabled(false);
        btnPlay.setFocusable(true);
        btnPlay.requestFocusInWindow();
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });
        btnPlay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                btnPlayKeyTyped(evt);
            }
        });
        cntrlPanel.add(btnPlay);

        btnStepBack.setMnemonic(KeyEvent.VK_LEFT);
        btnStepBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepBack24.gif"))); // NOI18N
        btnStepBack.setFocusable(true);
        btnStepBack.setEnabled(false);
        btnStepBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepBackActionPerformed(evt);
            }
        });
        btnStepBack.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnStepBackKeyPressed(evt);
            }
        });
        cntrlPanel.add(btnStepBack);

        btnStepForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/StepForward24.gif"))); // NOI18N
        btnStepForward.setFocusable(true);
        btnStepForward.setMnemonic(KeyEvent.VK_RIGHT);
        btnStepForward.setEnabled(false);
        btnStepForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepForwardActionPerformed(evt);
            }
        });
        btnStepForward.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnStepForwardKeyPressed(evt);
            }
        });
        cntrlPanel.add(btnStepForward);

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Pause24.gif"))); // NOI18N
        btnPause.setEnabled(false);
        btnPause.setFocusable(true);
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });
        btnPause.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                btnPauseKeyTyped(evt);
            }
        });
        cntrlPanel.add(btnPause);

        btnRewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Rewind24.gif"))); // NOI18N
        btnRewind.setEnabled(false);
        btnRewind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRewindActionPerformed(evt);
            }
        });
        cntrlPanel.add(btnRewind);

        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/FastForward24.gif"))); // NOI18N
        btnForward.setEnabled(false);
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });
        cntrlPanel.add(btnForward);

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Stop24.gif"))); // NOI18N
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        cntrlPanel.add(btnStop);

        progress.setMaximum(1);
        progress.setValue(0);
        progress.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        progress.setEnabled(false);
        progress.setFocusable(false);
        progress.setRequestFocusEnabled(false);
        progress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                progressMouseClicked(evt);
            }
        });

        imgArea.setBackground(new java.awt.Color(0, 0, 0));
        imgArea.setDoubleBuffered(true);
        imgArea.setOpaque(true);

        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItemOpenImage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenImage.setText("Open Image...");
        jMenuItemOpenImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenImageActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenImage);

        jMenuItemLoadFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLoadFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Open16.gif"))); // NOI18N
        jMenuItemLoadFile.setText("Load File...");
        jMenuItemLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLoadFileActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemLoadFile);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Save16.gif"))); // NOI18N
        jMenuItemSave.setText("Save As...");
        jMenuItemSave.setEnabled(false);
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuEncode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuEncode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/Jar16.gif"))); // NOI18N
        jMenuEncode.setText("Encode...");
        jMenuEncode.setEnabled(false);
        jMenuEncode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuEncodeActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuEncode);

        Menu.add(jMenuFile);

        jMenuEdit.setMnemonic('E');
        jMenuEdit.setText("Edit");

        jMenuFilters.setText("Filters...");
        jMenuFilters.setEnabled(false);

        jMenuItemNegative.setText("Negative");
        jMenuItemNegative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNegativeActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemNegative);

        jMenuItemBinarize.setText("Binzarize");
        jMenuItemBinarize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBinarizeActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemBinarize);

        jMenuItemHSB.setText("HSB");
        jMenuItemHSB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHSBActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemHSB);

        JMenuItemAverage.setText("Average");
        JMenuItemAverage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JMenuItemAverageActionPerformed(evt);
            }
        });
        jMenuFilters.add(JMenuItemAverage);

        jMenuItemLaplacian.setText("Laplacian");
        jMenuItemLaplacian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLaplacianActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemLaplacian);

        jMenuItemSobelX.setText("Sobel X");
        jMenuItemSobelX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSobelXActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemSobelX);

        jMenuItemSobelY.setText("Sobel Y");
        jMenuItemSobelY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSobelYActionPerformed(evt);
            }
        });
        jMenuFilters.add(jMenuItemSobelY);

        jMenuEdit.add(jMenuFilters);

        jMenuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Undo16.gif"))); // NOI18N
        jMenuItemUndo.setText("Undo...");
        jMenuItemUndo.setEnabled(false);
        jMenuItemUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUndoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemUndo);

        Menu.add(jMenuEdit);

        jMenuHelp.setMnemonic('H');
        jMenuHelp.setText("Help");

        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/About16.gif"))); // NOI18N
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        Menu.add(jMenuHelp);

        setJMenuBar(Menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cntrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
            .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(imgArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(imgArea, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cntrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * Step Frame button Action listener. 
    * @param evt 
    */
    private void btnStepForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepForwardActionPerformed
        nextFrame();
    }//GEN-LAST:event_btnStepForwardActionPerformed
    
    /**
     * Step Back button Action Listener.
     * @param evt 
     */
    private void btnStepBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepBackActionPerformed
        previousFrame();    
    }//GEN-LAST:event_btnStepBackActionPerformed

    /**
    * File Menu Action Listener
    * @param evt 
    */
    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jMenuFileActionPerformed

    /**
    * Load File Option Action Listener.
    * @param evt 
    */
    private void jMenuItemLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLoadFileActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //fc.setCurrentDirectory(new File("C:\\Users\\simorgh\\Downloads"));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Compressed files","zip", "rar", "7z", "ace", "tar");
        fc.setFileFilter(filter);
        
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            
            if(fc.getSelectedFile().getName().equals(Encoder.COMPRESSED_FNAME)){
                // It IS a custom CODEC file. Let's decode it!
                this.filesFromZip = Decoder.decode(fc.getSelectedFile());
                
            } else {
                zipFile = fc.getSelectedFile();
                try{
                    this.filesFromZip = ZipSaveWorker.readZip(zipFile);
                } catch(Exception e){/* ... */}
            }
            
            initControlPanel();
            this.jMenuItemSave.setEnabled(true);
            this.jMenuFilters.setEnabled(true);
            this.jMenuEncode.setEnabled(true);
            this.btnPlay.requestFocusInWindow();
        }
       
    }//GEN-LAST:event_jMenuItemLoadFileActionPerformed
    
    /**
     * Sets a Video Frame into GUI Video Area.
     * @param imgFile Frame to visualize
     */
    private void adjustImage(BufferedImage img){
        if (img != null) {
            Image dimg = img.getScaledInstance(imgArea.getWidth(), imgArea.getHeight(), Image.SCALE_SMOOTH);
            imgArea.setIcon(new ImageIcon(dimg));
        }
    }
    
    /**
     * Open Image option Action Listener. Allows to Open an image file
     * @param evt 
     */
    private void jMenuItemOpenImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenImageActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("IMAGE FILE", "jpg","png","tif","gif");
        fc.setFileFilter(filter);
        
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            imgFile = fc.getSelectedFile();
        }
        
        BufferedImage img = null;
        try{
            img = ImageIO.read(imgFile);
        } catch(IOException ex){
            Logger.getLogger(MediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        adjustImage(img);
    }//GEN-LAST:event_jMenuItemOpenImageActionPerformed

    /**
     * Play Button Action Listener. If there's a video loaded, the reproduction will start when the button is pressed.
     * 
     * @param evt 
     */
    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        playPause();
    }//GEN-LAST:event_btnPlayActionPerformed

    /**
     * Forward Button Action Listener. If the video is playing forward, pressing the button will increase the FPS
     * If the reproduction is going backwards, pressing this button will dectrease the FPS
     * @param evt 
     */
    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        this.btnPlay.setEnabled(true);
        this.btnStepBack.setEnabled(false);
        this.btnStepForward.setEnabled(false);
        this.btnPause.setEnabled(true);
        this.btnRewind.setEnabled(true);
        this.btnForward.setEnabled(true);
        this.btnStop.setEnabled(true);
        if(reproduction_mode==0){
            if(fps <= 1){ 
                reproduction_mode = 1;
                fps = DEFAULT_SPEED/2;
            }
            else{ 
                fps /= 2;
            
                if(this.th!=null){
                    this.th.cancel();
                }

                this.th = new Timer();
                th.scheduleAtFixedRate(new FrameRateTimer(PlayMode.BACKWARD), 0, (long) 1000.0/fps );
            }
        }
        
        else if (reproduction_mode == 1){
             // prevents from getting over maximum fps
            if( fps > (MAX_FPS/2) ) return;

            fps *= 2;
            if(this.th!=null){
                this.th.cancel();
            }

            this.th = new Timer();
            th.scheduleAtFixedRate(new FrameRateTimer(PlayMode.FORWARD), 0, (long) 1000.0/fps );
        }

    }//GEN-LAST:event_btnForwardActionPerformed

    /**
     * Rewind Button Action Listener. If the video is playing forward, pressing the button will decrease the FPS
     * If the reproduction is going backwards, pressing this button will inctrease the FPS
     * @param evt 
     */
    private void btnRewindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRewindActionPerformed
        this.btnPlay.setEnabled(true);
        this.btnStepBack.setEnabled(false);
        this.btnStepForward.setEnabled(false);
        this.btnPause.setEnabled(true);
        this.btnRewind.setEnabled(true);
        this.btnForward.setEnabled(true);
        this.btnStop.setEnabled(true);
        
        if(reproduction_mode == 1){
            if(fps <= 1){ 
                reproduction_mode = 0;
                fps = DEFAULT_SPEED/2;
            }
            else{
                
                fps /= 2;

                if(this.th!=null){
                    this.th.cancel();
                }

                this.th = new Timer();
                th.scheduleAtFixedRate(new FrameRateTimer(PlayMode.FORWARD), 0, (long) 1000.0/fps );
            }
        }
        
        else if (reproduction_mode ==0){
             // prevents from getting over maximum fps
            if( fps > (MAX_FPS/2) ) return;

            fps *= 2;
            if(this.th!=null){
                this.th.cancel();
            }

            this.th = new Timer();
            th.scheduleAtFixedRate(new FrameRateTimer(PlayMode.BACKWARD), 0, (long) 1000.0/fps );
        }
    }//GEN-LAST:event_btnRewindActionPerformed

    /**
     * Stop Button Action listener. Stops the reproduction of video. 
     * @param evt 
     */
    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        this.btnStop.setEnabled(false);
        this.btnPlay.setEnabled(true);
        this.btnStepBack.setEnabled(true);
        this.btnStepForward.setEnabled(true);
        this.btnPause.setEnabled(false);
        this.btnRewind.setEnabled(false);
        this.btnForward.setEnabled(false);

        if(th!=null){
            this.th.cancel();
            fps = DEFAULT_SPEED;
            this.indexImg = 0;
            this.progress.setValue(indexImg);
            imgArea.setIcon(new ImageIcon(getScaledImage(filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        }
    }//GEN-LAST:event_btnStopActionPerformed

    /**
     * Pause Button Action Listener. Pauses the video reproduction
     * @param evt 
     */
    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        playPause();
    }//GEN-LAST:event_btnPauseActionPerformed

    /**
     * Reproduction progress bar Mouse Listener. Allows to navigate through the video by clicking on the progress bar.
     * @param evt Mouse Event
     */
    private void progressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_progressMouseClicked
        this.indexImg = progress.getValue();
        imgArea.setIcon(new ImageIcon(getScaledImage(filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
    }//GEN-LAST:event_progressMouseClicked

    /**
     * Save option Action Listener. Saves The video file currently reproducing.
     * @param evt 
     */
    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try {
                saveZip(chooser.getSelectedFile());
            } catch (Exception ex) {
            }
        }
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    /**
     * Negative Filter Option Action Listener. Applies negative filter to the current video.
     * @param evt 
     */
    private void jMenuItemNegativeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNegativeActionPerformed
        NegativeFilter nf = new NegativeFilter();
        this.filtered = nf.applyNegativeFilterToVideo(cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemNegativeActionPerformed
    
    /**
     * HSB Option Action Listener. Opens HSB dialog that allows to modify HSB  of the current video.
     * 
     * @param evt 
     */
    private void jMenuItemHSBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHSBActionPerformed
        this.hsbHandling = new DialogHSB(this,true);
        System.out.println("\nOpening dialog");
        this.hsbHandling.setVisible(true);
        System.out.println("\nGot the data");
        System.out.println("\nReported:\n\tHUE "+hsbHandling.getHue()+"\n\tSAT "+hsbHandling.getSaturation()+"\n\tBRIGHT "+hsbHandling.getBrightness());
        HSBFilter hsb = new HSBFilter(hsbHandling.getHue(),hsbHandling.getSaturation(),hsbHandling.getBrightness());
        this.filtered = hsb.applyHSBFilterToVideo(cloneVideo(this.filesFromZip));
        
        imgArea.setIcon(new ImageIcon(getScaledImage(filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemHSBActionPerformed
    
    /**
     * Laplacian Filter Option Action Listener. Applies Laplacian Filter to the current video.
     * 
     * @param evt 
     */
    private void jMenuItemLaplacianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLaplacianActionPerformed
        Convolution conv = new Convolution();
        this.filtered = conv.applyFilterToVideo(Convolution.kernel.LAPLACIAN, cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemLaplacianActionPerformed

    /**
     * Sobel X Filter Option Action Listener. Applies Sobel filter on the X axis to current video.
     * @param evt 
     */
    private void jMenuItemSobelXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSobelXActionPerformed
        Convolution conv = new Convolution();
        this.filtered = conv.applyFilterToVideo(Convolution.kernel.SOBEL_X, cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemSobelXActionPerformed

    /**
     * Average Filter Option Action Listener. Applies Average filter on current video.
     * @param evt 
     */
    private void JMenuItemAverageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JMenuItemAverageActionPerformed
        Convolution conv = new Convolution();
        this.filtered = conv.applyFilterToVideo(Convolution.kernel.AVERAGE, cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_JMenuItemAverageActionPerformed

    /**
     * Sobel Y Option Action Listener. Applies Sobel Filter on Y  axis to current video.
     * @param evt 
     */
    private void jMenuItemSobelYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSobelYActionPerformed
        Convolution conv = new Convolution();
        this.filtered = conv.applyFilterToVideo(Convolution.kernel.SOBEL_Y, cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemSobelYActionPerformed
    
    /**
     * Binarize Option Action Listener. Applies the binarization to current video
     * @param evt 
     */
    private void jMenuItemBinarizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemBinarizeActionPerformed
        Binarization bin = new Binarization(64);
        this.filtered = bin.applyBinarizationToVideo(cloneVideo(this.filesFromZip));
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        jMenuItemUndo.setEnabled(true);
        this.isFiltered = true;
    }//GEN-LAST:event_jMenuItemBinarizeActionPerformed

    /**
     * Encode Option Action Listener. Applies Our own codification to the current video.
     * @param evt 
     */
    private void jMenuEncodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuEncodeActionPerformed

        this.encodeHandling = new DialogEncode(this, true, filesFromZip.get(0).getHeight(), filesFromZip.get(0).getWidth());
        this.encodeHandling.setVisible(true);
        if(this.encodeHandling.isAccepted()){
            Encoder enc = new Encoder( cloneVideo(this.filesFromZip), encodeHandling.getQuality(),
                    (short) encodeHandling.getGOP(), (short) encodeHandling.getPatchSize(),
                    (short) encodeHandling.getOffset());
            enc.encode();
        } else {
            System.out.println("> @encode ABORTED");
        }
    }//GEN-LAST:event_jMenuEncodeActionPerformed
    
    /**
     * About Us Option Action Listener. Opens About Us Dialog.
     * @param evt 
     */
    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
       DialogAbout aboutus = new DialogAbout(this,true);
       aboutus.setVisible(true);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    /**
     * Play Button Key Listener. Plays the video.
     * @param evt 
     */
    private void btnPlayKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPlayKeyTyped
        if (evt.getKeyChar() == '\32'){
            
            playPause();
        }   
    }//GEN-LAST:event_btnPlayKeyTyped

    /**
     * Pause Button Key Listener. Pauses the reproduction.
     * @param evt 
     */
    private void btnPauseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPauseKeyTyped
         if (evt.getKeyChar() == '\32'){
            
            playPause();
        }   
    }//GEN-LAST:event_btnPauseKeyTyped

    /**
     * Step Back Button Key Listener. Shows the previous frame in the current video.
     * @param evt 
     */
    private void btnStepBackKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnStepBackKeyPressed
       //if(evt.getKeyCode() == KeyEvent.VK_LEFT){
       //    previousFrame();
       //}
    }//GEN-LAST:event_btnStepBackKeyPressed

    private void btnStepForwardKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnStepForwardKeyPressed
    //   if(evt.getKeyCode()== KeyEvent.VK_RIGHT) nextFrame();
    }//GEN-LAST:event_btnStepForwardKeyPressed

    private void jMenuItemUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndoActionPerformed
        jMenuItemUndo.setEnabled(false);
        imgArea.setIcon(new ImageIcon(getScaledImage(this.filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        this.filtered.clear();
        this.isFiltered = false;
    }//GEN-LAST:event_jMenuItemUndoActionPerformed
    
    /**
    * Clones video ArrayList and also clone its contents.
    * We will need to iterate on the items, and clone them one by one,
    * putting the clones in a result array as we go.
    *
    * @param original
    * @return full copy
    */  
    public static ArrayList<BufferedImage> cloneVideo(ArrayList<BufferedImage> original) {
        ArrayList<BufferedImage> clone = new ArrayList<>(original.size());
        for(BufferedImage item: original) clone.add(deepCopy(item));
        return clone;
    }
    
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Saves current BufferedImage list filesFromZip into a new Compressed ZIP file.
     * @param file
     */
    public void saveZip(File file){
        ZipSaveWorker zp = new ZipSaveWorker(this.filesFromZip, file);
        zp.run();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MediaPlayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MediaPlayer().setVisible(true);
            }
        });
    }
    
    
    /**
     * Used to convert the BufferedImage type when required.
     * Removes the errors from format data type.
     * @param src
     * @param type
     * @return 
     */
    private BufferedImage convertType(BufferedImage src, int type){
        ColorConvertOp cco = new ColorConvertOp(null);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), type);
        cco.filter(src, dest);
        
        return dest;
    }
    
    /**
     * Initializes  the Control Panel of Media Player
     */
    private void initControlPanel(){
        this.indexImg = 0;
        this.progress.setValue(this.indexImg);
        
        //image set (video)
        if( filesFromZip!=null && filesFromZip.size()>1){
            this.progress.setMaximum(filesFromZip.size()-1);
            this.progress.setEnabled(true); 
            this.btnPlay.setEnabled(true);
            this.btnStepBack.setEnabled(true);
            this.btnStepForward.setEnabled(true);
        }
        
        // common line in video/image loading
        imgArea.setIcon(new ImageIcon(getScaledImage(filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
    }
    
    
    /**
     * Method that switches the actual frame being visualized by Media Player to the previous frame
     */
    private void previousFrame(){
        if(indexImg == 0) indexImg = filesFromZip.size()-1;
        else indexImg--;

        this.progress.setValue(indexImg);
        if(isFiltered) imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        else imgArea.setIcon(new ImageIcon(getScaledImage(filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
    }
    
    /**
     * Method that switches the actual frame being visualized by Media Player to the next frame
     */
    private void nextFrame(){
        if(indexImg == filesFromZip.size()-1) indexImg = 0;
        else indexImg++;

        this.progress.setValue(indexImg);
        if(isFiltered) imgArea.setIcon(new ImageIcon(getScaledImage(this.filtered.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
        else imgArea.setIcon(new ImageIcon(getScaledImage(filesFromZip.get(indexImg), imgArea.getWidth(), imgArea.getHeight())));
    }
    
        /**
     * Method that handles the Play and Pause buttons listeners.
     *Executes the functionallity of the button that is having focus
     * 
     * @param mode 
     */
    private void playPause() {
        if (this.btnPlay.isFocusOwner()){
            this.btnPlay.setEnabled(false);
            this.btnStepBack.setEnabled(false);
            this.btnStepForward.setEnabled(false);
            this.btnPause.setEnabled(true);
            this.btnRewind.setEnabled(true);
            this.btnForward.setEnabled(true);
            this.btnStop.setEnabled(true);
            this.fps = DEFAULT_SPEED;
            reproduction_mode = 1;        
            if(th!=null) this.th.cancel();
            this.th = new  Timer(); 
            th.scheduleAtFixedRate(new FrameRateTimer(PlayMode.FORWARD), 0, (long) 1000.0/fps );
            this.playing = false;
            btnPause.requestFocusInWindow();
        }
        else if (this.btnPause.isFocusOwner()){
            this.btnPause.setEnabled(false);
            this.btnPlay.setEnabled(true);
            this.btnStepBack.setEnabled(true);
            this.btnStepForward.setEnabled(true);

            this.th.cancel();
            this.playing = true;
            btnPlay.requestFocusInWindow();
        }
    }
    
    /**
     * Gets and returns hte current
     * @return 
     */
    public ArrayList<BufferedImage> getVideo(){
        return this.filesFromZip;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem JMenuItemAverage;
    private javax.swing.JMenuBar Menu;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnRewind;
    private javax.swing.JButton btnStepBack;
    private javax.swing.JButton btnStepForward;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel cntrlPanel;
    private javax.swing.JLabel imgArea;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenuItem jMenuEncode;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFilters;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemBinarize;
    private javax.swing.JMenuItem jMenuItemHSB;
    private javax.swing.JMenuItem jMenuItemLaplacian;
    private javax.swing.JMenuItem jMenuItemLoadFile;
    private javax.swing.JMenuItem jMenuItemNegative;
    private javax.swing.JMenuItem jMenuItemOpenImage;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSobelX;
    private javax.swing.JMenuItem jMenuItemSobelY;
    private javax.swing.JMenuItem jMenuItemUndo;
    private javax.swing.JSlider progress;
    // End of variables declaration//GEN-END:variables
    


    private class FrameRateTimer extends TimerTask {
        private final PlayMode mode;
        
        FrameRateTimer(PlayMode mode){
            this.mode = mode;
        }
        
        @Override
        public void run(){
            //change image
            switch(mode){
                case BACKWARD:
                    previousFrame();
                    break;
                case FORWARD:
                    nextFrame();
                    break;
            }
        };   
    }
}