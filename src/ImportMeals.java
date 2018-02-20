import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class ImportMeals extends ActionSettings {
    private Sheet sheet = null;
    private Cell date = null;
    private Cell shift = null;
    private Cell mealNumber = null;
    private Cell mealDescription = null;
    private Cell allergens = null;
    private Database database = null;
    private MealSettings container = null;
    private CustomTableModel tableModel;
    
    public ImportMeals(MealSettings container, Database database, CustomTableModel tableModel)
    {
        super(database);
        this.database = database;
        this.container = container;
        this.tableModel = tableModel;
        
        InputStream input = null;
        try 
        {
            input = new FileInputStream("src/import/Mealo_import_scheme.xlsx");
            
            Workbook wb = WorkbookFactory.create(input);
            sheet = wb.getSheetAt(0);
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InvalidFormatException ex) 
        {
            Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (EncryptedDocumentException ex) 
        {
            Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
    @Override
    public void execute() {
        String[] options = {"Yes", "No"};
        int option = JOptionPane.showOptionDialog(container, "Are you sure you want to import file data?", "Double-check import", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
        if(option == JOptionPane.YES_OPTION)
            try 
            {
                int row = 4;

                while(readRow(row++))
                {
                    
                    String query = String.format(""
                            + "INSERT INTO mo_meals (mealNumber, shift, date, description, allergens) "
                            + "VALUES (%d, %d, '%s', '%s', '%s')",
                            (int)mealNumber.getNumericCellValue(), 
                            (int)shift.getNumericCellValue(), 
                            formatDate(String.valueOf(date.getDateCellValue())), 
                            mealDescription.getStringCellValue(), 
                            (allergens != null) ? allergens.getStringCellValue() : "");
                    
                    int key = database.executeQuery(query, true);
                    
                    if(container.getShiftNumber() == (int)shift.getNumericCellValue())
                        tableModel.addRow(new Object[] {
                            key, 
                            formatDate(String.valueOf(date.getDateCellValue())), 
                            (int)mealNumber.getNumericCellValue(),
                            mealDescription.getStringCellValue(),
                            (allergens != null) ? allergens.getStringCellValue() : ""
                        });
                }
                
                JOptionPane.showConfirmDialog(container, "Data has been imported.", "Import Successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                
            } 
            catch (ParseException ex) 
            {
                Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
            }

    }

  private boolean readRow(int rowNumber)
  {
      Row row = sheet.getRow(rowNumber);
      if(row==null)
          return false;
      
      date = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      shift = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      mealNumber = row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      mealDescription = row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      allergens = row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
      
      if(date == null || shift == null || mealNumber == null || mealDescription == null)
      {
           JOptionPane.showConfirmDialog(
                        container,"Re-check the file structure and import again:\nRow cells MUST NOT be empty, with exception of allergens.", "Wrong File Structure", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
          return false;
      }
      
      return true;
  }
  
  private String formatDate(String date) throws ParseException
  {
        Calendar calendar = Calendar.getInstance();
        Date theDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).parse(date);
        calendar.setTime(theDate);
        
        return String.format("%s-%02d-%02d",calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
  }

}


