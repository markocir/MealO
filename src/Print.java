import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
class Print extends ActionSettings implements Printable{
    String date = null;
    int shiftNumber = -1;
    
    public Print(Database database, String date, int shiftNumber) {
        super(database);
        this.date = date;
        this.shiftNumber = shiftNumber;
    }
    
    public void execute() {
            PrinterJob job = PrinterJob.getPrinterJob();
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            PageFormat pf = job.defaultPage();
            Paper paper = pf.getPaper();
            paper.setSize(8.5 * 72, 11 * 72);
            paper.setImageableArea(0.5 * 72, 0.1 * 72, 7.5 * 72, 10.5 * 72);
            pf.setPaper(paper);
            job.setPrintable(this, pf);
            boolean proceed = job.printDialog();
            if (proceed) 
            {
                try 
                {
                    job.print(aset);
                    String query = String.format("SELECT * FROM mo_prints WHERE date = '%s' AND shift = %d", date, shiftNumber);
                    int count = database.executeQueryGetRowCount(query);
                    
                    // if print is performed first time for this date on shift
                    if(count == 0)
                    {
                        query = String.format("INSERT INTO mo_prints (date, shift) VALUES ('%s', %d)", date, shiftNumber);
                        database.executeQuery(query, false);
                        
                        for(String date : database.executeQueryPastPrints(date, shiftNumber))
                        {
                            
                            for(int id : database.executeQueryGetMealId(date, shiftNumber))
                            {
                                query = String.format("DELETE FROM mo_orders WHERE meal_id = %d", id);
                                database.executeQuery(query, false);
                            }
                            query = String.format("DELETE FROM mo_meals WHERE date = '%s' AND shift = %d", date, shiftNumber);
                            database.executeQuery(query, false);
                            query = String.format("DELETE FROM mo_prints WHERE date = '%s' AND shift = %d", date, shiftNumber);
                            database.executeQuery(query, false);
                        }
                    }
                } 
                catch (PrinterException ex) 
                {
                    /* The job did not successfully complete */
                }
            }
    }
        
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
        String font = "Georgia";
        g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize));
        FontMetrics metrics = g2d.getFontMetrics();
        int defaultLineHeight = metrics.getHeight();
        int[] defaultCharacterWidths = metrics.getWidths();
        int coordinateY = 20;
        int coordinateX = 0;
        int mealNumberY = 0;
        boolean firstLoop = true;
        
        SecureRandom sr = new SecureRandom();
        
        ArrayList<MealPrintItem> mealsList = database.getCountOfMeals(date, shiftNumber);
        
        String datePrefix = "Date: ";
        String mealNumber = null;
        String stringToWrite = "In a world where death is the hunter, my friend, there is no time for regrets or doubts. There is only time for decisions.";
        String mealCountString = null;
        
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        int mealNumberStringWidth = -1;
        int mealCountStringWidth = -1;
        int mealCountStringLengthDifference = -1;
        int mealCountStringTrueWidth = -1;
        
        //draw date
        g2d.setFont(new Font(font, Font.BOLD, defaultFontSize + 5));
        metrics = g2d.getFontMetrics(); 
        g2d.drawString(datePrefix.concat(this.date), coordinateX, coordinateY);
        // create bigger space between date line and table
        coordinateY += metrics.getHeight()*2;
        
        for(MealPrintItem meal : mealsList)
        {
            mealNumber = String.valueOf(meal.getMealNumber());
            stringToWrite = meal.getDescription();
            mealCountString = String.valueOf(meal.getOrdersCount());

            // below - for testing purposes only
            if(sr.nextBoolean())
                mealCountString = mealCountString.concat("x");
            else if(sr.nextBoolean())
                mealCountString = mealCountString.concat("xx");
            else if(sr.nextBoolean())
                mealCountString = mealCountString.concat("xxx");
            // above - for testing purposes only

            coordinateX = 0;

            if(firstLoop)
            {
                // calculate string widths for 4-digit number
                g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 12));
                metrics = g2d.getFontMetrics();
                mealCountStringWidth = calculateStringWidth("0000", metrics.getWidths());

                // calculate string lengths for leading meal number
                g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 25));
                metrics = g2d.getFontMetrics();
                mealNumberStringWidth = calculateStringWidth(mealNumber, metrics.getWidths());

                // calculate data for table head
                g2d.setFont(new Font(font, Font.BOLD, defaultFontSize));
                metrics = g2d.getFontMetrics();
                String mealString = "Meal";
                String descriptionString = "Description";
                String ordersString = "Orders";

                /**
                 * Use the bigger width: 
                 * title meal number string width or
                 * meal number string width
                 */
                mealNumberStringWidth = mealNumberStringWidth < calculateStringWidth(mealString, metrics.getWidths()) 
                        ? calculateStringWidth(mealString, metrics.getWidths()) : mealNumberStringWidth;

                /**
                 * Use the bigger width: 
                 * comparing table title orders string with
                 * meal orders sum string
                 */
                mealCountStringWidth = mealCountStringWidth < calculateStringWidth(ordersString, metrics.getWidths()) 
                        ? calculateStringWidth(ordersString, metrics.getWidths()) : mealCountStringWidth;

                // add 5 more width to meal number sum string, to avod text collision
                mealNumberStringWidth += 5;

                g2d.drawString(mealString, 0, coordinateY);
                g2d.drawString(descriptionString, mealNumberStringWidth, coordinateY);
                g2d.drawString(ordersString, (int)pf.getImageableWidth() - mealCountStringWidth, coordinateY);

                coordinateY += 5;
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, coordinateY, (int)pf.getImageableWidth(), coordinateY);
                g2d.setStroke(new BasicStroke(1));
                coordinateY += defaultLineHeight+defaultLineHeight-5;

                firstLoop = false;
            }

            coordinateY += defaultLineHeight*2;


            // MEAL NUMBER AND NAME
            g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 25));
            metrics = g2d.getFontMetrics();
            g2d.drawString(mealNumber, coordinateX, coordinateY);
            coordinateX += mealNumberStringWidth;
            mealNumberY = coordinateY;
            coordinateY -= metrics.getHeight(); // move to the top of the last line
            coordinateY += defaultLineHeight; // add one empty line

            // MEAL DESCRIPTION - SUPPORTS MULTI-LINE

            g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize));

            ArrayList<String> stringList = breakLine(stringToWrite, defaultCharacterWidths, (int)pf.getImageableWidth() - mealCountStringWidth - coordinateX);
            // print each string in seperate line
            for(String string : stringList)
            {
                coordinateY += defaultLineHeight;
                g2d.drawString(string, coordinateX, coordinateY);
            }

            // last Line
            coordinateY += defaultLineHeight;
            if(coordinateY < mealNumberY)
                coordinateY = coordinateY+defaultLineHeight;

            g2d.setFont(new Font(font, Font.PLAIN, defaultFontSize + 12));
            metrics = g2d.getFontMetrics();
            mealCountStringLengthDifference = mealCountStringWidth - calculateStringWidth(mealCountString, metrics.getWidths());
            mealCountStringTrueWidth = 0 < mealCountStringLengthDifference ? mealCountStringWidth-mealCountStringLengthDifference : mealCountStringWidth;
            g2d.drawString(mealCountString, (int)pf.getImageableWidth()-mealCountStringTrueWidth, coordinateY-defaultLineHeight);

            g2d.drawLine(0, coordinateY, (int)pf.getImageableWidth(), coordinateY);
            coordinateY += defaultLineHeight;
        }
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    public int calculateStringWidth(String string, int[] charWidths)
    {
        int result = 0;
        
        // replace slovenian special characters with common characters
        // and get their width
        HashMap<Character, Character> replacements = new HashMap<>(6);
        replacements.put('č', 'c');
        replacements.put('Č', 'C');
        replacements.put('ž', 'z');
        replacements.put('Ž', 'Z');
        replacements.put('š', 's');
        replacements.put('Š', 'S');
        
        for(char c : string.toCharArray())
        {
            try
            {
                if((int)c > 255)
                    c = replacements.get(c);

                result += charWidths[(int)c];
            }
            catch(NullPointerException ex)
            {
                System.err.println("Missing character:'"+c+"'");
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        result += charWidths[(int)' ']; // add space width for estetics
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
                newLine.append(word.concat(delimiter));
            }
            else
            {
                result.add(newLine.toString().concat(delimiter));
                newLine = new StringBuffer();
                
                newLine.append(word.concat(delimiter));
            }            
        }
        
        if(newLine.length() > 0)
                result.add(newLine.toString());
        
        return result;
    }
}
