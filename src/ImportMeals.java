import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
class ImportMeals extends ActionSettings {
    private Sheet sheet = null;
    private String date = null;
    private int shift = -1;
    private int mealNumber = -1;
    private String mealDescription = null;
    private String allergens = null;
    private Database database = null;
    private MealSettings container = null;
    private CustomTableModel tableModel = null;
    
    public ImportMeals(MealSettings container, Database database, CustomTableModel tableModel) throws FileNotFoundException, IOException
    {
        super(database);
        this.database = database;
        this.container = container;
        this.tableModel = tableModel;
        
        InputStream input = null;
        try 
        {
            input = new FileInputStream("src/import/Mealo_import_schem.xlsx");
            
            Workbook wb = WorkbookFactory.create(input);
            sheet = wb.getSheetAt(0);
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
                // start reading file on row
                int row = 4;
                
                ArrayList<Meal> mealList = new ArrayList<>(25);
                
                while(readRow(row++))
                {
                    mealList.add(
                            new Meal(
                                    -1, 
                                    mealNumber,
                                    shift,
                                    String.valueOf(date),
                                    mealDescription,
                                    allergens)
                    );
                }
                
                try 
                {
                    mealList = database.importMeals(mealList);
                    
                    for(Meal meal : mealList)
                    {
                        // add to table only meals matching current shift
                        if(container.getShiftNumber() == meal.getShift())
                            tableModel.addRow(new Object[] {
                                meal.getId(),
                                meal.getDate(), 
                                meal.getMealNumber(),
                                meal.getDescription(),
                                meal.getAllergens()
                            });
                    }
                    
                    JOptionPane.showConfirmDialog(container, "Data has been imported.", "Import Successful", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            } 
            catch (ParseException ex) 
            {
                Logger.getLogger(ImportMeals.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (NullPointerException ex)
            {
                JOptionPane.showConfirmDialog(
                            container,
                            "Re-check the file structure and import again:\n"
                                    + "Row cells MUST NOT be empty, with exception of allergens.", 
                            "Wrong File Structure", 
                            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }

    }

  private boolean readRow(int rowNumber) throws ParseException
  {
      Row row = sheet.getRow(rowNumber);
      if(row == null)
          return false;
      
      date = formatDate(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getDateCellValue().toString());
      shift = (int)row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getNumericCellValue();
      mealNumber = (int)row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getNumericCellValue();
      mealDescription = row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL).getStringCellValue();
      // allergens can be blank (null)
      allergens = row.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
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


