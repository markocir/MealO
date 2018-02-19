
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class EditMealPane extends javax.swing.JPanel {
    /**
     * Creates new form EditMeal
     */
    public EditMealPane() {
        initComponents();
    }
    
    public EditMealPane(Integer id, String date, Integer mealNumber, String description, String allergens)
    {
        this();
        this.textFieldId.setText(id.toString());
        this.textFieldDate.setText(date);
        this.textFieldMealNumber.setText(mealNumber.toString());
        this.textFieldDescription.setText(description);
        
        Allergens allergensIcons = new Allergens(true, Allergens.ICON_SIZE_TWENTYONE);
        CheckBoxLabelCombo[] checkBoxes = new CheckBoxLabelCombo[Allergens.TOTAL_NUMBER_OF_ALLERGENS];
        
        for(int i=0;i<checkBoxes.length;i++)
        {
            checkBoxes[i] = new CheckBoxLabelCombo(allergensIcons.getIcon(i+1), allergensIcons.getIconName(i+1), containsAllergen(allergens, i+1));
            this.allergensCheckboxes.add(checkBoxes[i]);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelId = new javax.swing.JLabel();
        labelDate = new javax.swing.JLabel();
        labelMealNumber = new javax.swing.JLabel();
        labelDescription = new javax.swing.JLabel();
        labelAllergens = new javax.swing.JLabel();
        textFieldMealNumber = new javax.swing.JTextField();
        textFieldDate = new javax.swing.JTextField();
        textFieldId = new javax.swing.JTextField();
        textFieldDescription = new javax.swing.JTextField();
        allergensCheckboxes = new javax.swing.JPanel();

        labelId.setText("ID");

        labelDate.setText("Date");

        labelMealNumber.setText("Meal Number");

        labelDescription.setText("Description");

        labelAllergens.setText("Allergens");

        textFieldMealNumber.setText("null");

        textFieldDate.setText("null");

        textFieldId.setEditable(false);
        textFieldId.setText("null");
        textFieldId.setEnabled(false);

        textFieldDescription.setText("null");

        allergensCheckboxes.setLayout(new java.awt.GridLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelMealNumber)
                            .addComponent(labelDate)
                            .addComponent(labelId))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(textFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldMealNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelDescription)
                            .addComponent(labelAllergens))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(textFieldDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                            .addComponent(allergensCheckboxes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelId)
                    .addComponent(textFieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDate)
                    .addComponent(textFieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMealNumber)
                    .addComponent(textFieldMealNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDescription)
                    .addComponent(textFieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelAllergens)
                        .addGap(0, 114, Short.MAX_VALUE))
                    .addComponent(allergensCheckboxes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    public int getId()
    {
        return Integer.valueOf(textFieldId.getText());
    }
    
    public String getDate()
    {
        return textFieldDate.getText();
    }
    
    public int getMealNumber()
    {
        return Integer.valueOf(textFieldMealNumber.getText());
    }
    
    public String getDescription()
    {
        return textFieldDescription.getText();
    }
    
    public String getAllergens()
    {
        boolean firstSelected = false;
        
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<Allergens.TOTAL_NUMBER_OF_ALLERGENS;i++)
        {
            CheckBoxLabelCombo checkBox = (CheckBoxLabelCombo) allergensCheckboxes.getComponent(i);
            
            if(checkBox.isSelected())
            {
                if(!firstSelected)
                {
                    sb.append(i+1);
                    firstSelected = true;
                }
                else
                    sb.append(" "+(i+1));
            }
        }
        return sb.toString();
    }

    private boolean containsAllergen(String allergens, Integer i)
    {
        String[] allergenArray = allergens.split("\\s");
        
        for(String allergen : allergenArray)
            if(allergen.equals(i.toString()))
                return true;
        
        return false;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel allergensCheckboxes;
    private javax.swing.JLabel labelAllergens;
    private javax.swing.JLabel labelDate;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelId;
    private javax.swing.JLabel labelMealNumber;
    private javax.swing.JTextField textFieldDate;
    private javax.swing.JTextField textFieldDescription;
    private javax.swing.JTextField textFieldId;
    private javax.swing.JTextField textFieldMealNumber;
    // End of variables declaration//GEN-END:variables
}
