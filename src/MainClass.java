/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.*;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainClass implements Printable {


    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        int defaultFontSize = 10;
        g2d.setFont(new Font("Sans-Serif", Font.PLAIN, defaultFontSize));
        FontMetrics metrics = g2d.getFontMetrics();
        int defaultLineHeight = metrics.getHeight();
        int[] defaultCharacterWidths = metrics.getWidths();
        int coordinateY = 20;
        int coordinateX = 0;
        String font = "Georgia";
        
        String date = "2018-1-8";
        String stringToWrite = "In a world where death is the hunter, my friend, there is no time for regrets or doubts. There is only time for decisions.";
        String orderedString = "Ordered: ";
        String mealCountString = "14x";
        
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        /* Now we perform our rendering */
        // DATE
        g2d.setFont(new Font(font, Font.BOLD, defaultFontSize + 10));
        metrics = g2d.getFontMetrics(); // get new font metrics
        g2d.drawString(date, coordinateX, coordinateY);
        coordinateY += metrics.getHeight();
        
        //create empty line
        coordinateY += metrics.getHeight();
        
        // MEAL NUMBER AND NAME
        g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 25));
        metrics = g2d.getFontMetrics();
        String mealNumber = "1";
        g2d.drawString(mealNumber, coordinateX, coordinateY);
        coordinateX += calculateStringWidth(mealNumber, metrics.getWidths());
        coordinateY -= metrics.getHeight(); // move to the top of the last line
        coordinateY += defaultLineHeight; // add one empty line
        
        // MEAL DESCRIPTION - SUPPORTS MULTI-LINE

        
        g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 12));
        metrics = g2d.getFontMetrics();
        int mealCountStringWidth = calculateStringWidth(mealCountString, metrics.getWidths());
        
        g2d.setFont(new Font(font, Font.BOLD, defaultFontSize));
        metrics = g2d.getFontMetrics();
        int orderedStringWidth = calculateStringWidth(orderedString, metrics.getWidths());
        
        g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize));
        
        ArrayList<String> stringList = breakLine(stringToWrite, defaultCharacterWidths, (int)pf.getImageableWidth() - mealCountStringWidth - coordinateX - 60);
        for(String string : stringList)
        {
            coordinateY += defaultLineHeight;
            g2d.drawString(string, coordinateX, coordinateY);
        }
        
        g2d.setFont(new Font(font, Font.BOLD, defaultFontSize));
        g2d.drawString(orderedString, (int)pf.getImageableWidth()-mealCountStringWidth-orderedStringWidth, coordinateY);
        
        g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 12));
        g2d.drawString(mealCountString, (int)pf.getImageableWidth()-mealCountStringWidth, coordinateY);
        
        // last Line
        coordinateY += defaultLineHeight;
        g2d.drawLine(0, coordinateY, (int)pf.getImageableWidth(), coordinateY);
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public int calculateStringWidth(String string, int[] charWidths)
    {
        int result = 0;
        
        for(char c : string.toCharArray())
            result += charWidths[(int)c];
        result += charWidths[(int)' '];
        return result;
    }
    
    public ArrayList<String> breakLine(String string, int[] charWidths, int maxWidth)
    {
        ArrayList<String> result = new ArrayList<>(2);
        StringBuffer newLine = new StringBuffer();
        String delimiter = " ";
        String[] words = string.split(delimiter);
        for(String word : words)
        {
            int possibleLineWidth = calculateStringWidth(newLine.toString().concat(word.concat(delimiter)), charWidths);
            
            if(possibleLineWidth < maxWidth-1)
            {
                System.out.println(possibleLineWidth+" "+maxWidth);
                newLine.append(word.concat(delimiter));
            }
            else
            {
                result.add(newLine.toString().concat(delimiter));
                newLine = new StringBuffer();
                
                newLine.append(word.concat(delimiter));
                System.out.println(newLine.toString());
            }            
        }
        
        if(newLine.length() > 0)
                result.add(newLine.toString());
        
        return result;
    }
    
    public static void main(String args[]) {

        
    }
}