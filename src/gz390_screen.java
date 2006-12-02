import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
/*****************************************************

z390 portable mainframe assembler and emulator.

Copyright 2006 Automated Software Tools Corporation
 
z390 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

z390 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with z390; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The gz390_screen class defines public Graphics2D
panel for use by gz390 to define GUAM views:
 1.  tn_screen 
 2.  graphic_screen
The method set_screen(rows,cols,font,
background_color,text_color) is used
to initialize screen size based on the height
and width of the font assuming it has been set
to desired font size and is a monospace font.
gz390 characer and graphics commands draw
directly on gz390_screen buffered image object
which is repainted at fixed intervals whenever
scn_update is set true.
 ****************************************************
 * Maintenance
 * ***************************************************
 * 05/18/06 RPI 227 replace JTextArea screen in
 *          gz390 with this new Graphics2D 
 *          panel in gz390_screen class with
 *          method to set screen size
 * 08/10/06 RPI 408 add 5 pixels to graphic screen size
 ********************************************************
 */
public class gz390_screen extends JPanel implements Runnable {
    /*
     * define public Graphics2D scn_panel
     */
	/*
	 * global data for gz3270_screen
	 */
	    private static final long serialVersionUID = 1L;
        /*
         * input variable from set_screen
         */
	    int       scn_rows = 0;
        int       scn_cols = 0;
        Font      scn_font  = null;
        Color     scn_background_color;
        Color     scn_text_color;
        /*
         * create screen image based on
         * pixel size of character rendering
         * using specified font_size assuming
         * font is monospace
         */
	    BufferedImage     scn_image;
    	Graphics2D        scn_grid;
   	    FontRenderContext scn_context;
   	    TextLayout        scn_layout;
   	    int               scn_font_size   = 0;
        int               scn_char_height = 0;
        int               scn_char_width  = 0;
        Rectangle2D       scn_char_rect;
        int               scn_height = 0;
        int               scn_width  = 0;
	    Dimension         scn_size = null;
        /*
         * screen updated at wait intervals
         * whenever screen_update is set
         */
	    boolean scn_repaint = false;
	    Thread  scn_update_thread;
        long    scn_update_wait = 300; 
	    /*
         * end of global gn3270_screen data
         */  
        public void paint(Graphics g){
        	/*
        	 * override default paint to draw 
        	 * screen image in panel using current
        	 * scn_background.
        	 */
        	g.drawImage(scn_image,0,0,scn_width,scn_height,scn_background_color,this);
        }
        public void start_scn_updates() {
        	/*
        	 * start thread used to repaint image
        	 * at fixed intervals whenever
        	 * scn_update has been set true
        	 */
            scn_update_thread = new Thread(this);
            scn_update_thread.start();
        }    
        public synchronized void stop_scn_updates() {
            scn_update_thread = null;
        }    
        public void run() {
            while (scn_update_thread == Thread.currentThread()) {              
            	try {  // RPI 423 catch repait exception too
                	if (scn_repaint){
                		scn_repaint = false;
                		repaint();
                	}
                    Thread.sleep(scn_update_wait);
                } catch (Exception e){
                	break;
                }
            }
            scn_update_thread = null;
        } 
        public void set_screen(int new_rows, int new_cols, Font new_font, Color new_background_color, Color new_text_color){
        	/*
        	 * initialize screen panel 
        	 * based on rows, columns,
        	 * text font and font size,
        	 * background and text color  
        	 */
        	stop_scn_updates();
        	scn_rows = new_rows;
        	scn_cols = new_cols;
        	scn_font = new_font;
        	scn_background_color = new_background_color;
        	scn_text_color = new_text_color;
        	scn_font_size = scn_font.getSize();
        	scn_image    = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        	scn_grid     = scn_image.createGraphics();
        	scn_context  = scn_grid.getFontRenderContext();
       	    scn_layout   = new TextLayout("X",scn_font,scn_context);
            scn_char_rect = scn_layout.getBlackBoxBounds(0,1).getBounds();
            scn_char_height  = (int) scn_char_rect.getWidth()+3;
        	scn_char_width   = (int) scn_char_rect.getHeight();
        	scn_height = scn_rows * scn_char_height + 5; // RPI 408 + 5
        	scn_width  = scn_cols * scn_char_width + 5;  // RPI 408 + 5
            scn_size     = new Dimension(scn_width,scn_height);
        	scn_image    = new BufferedImage(scn_width,scn_height,BufferedImage.TYPE_INT_ARGB);
        	scn_grid     = scn_image.createGraphics();
        	scn_grid.setFont(new_font);
            scn_grid.setColor(scn_text_color);
       	    scn_context  = scn_grid.getFontRenderContext();
            scn_grid.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            scn_repaint = true;
            start_scn_updates();
        }
}

