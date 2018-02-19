
import java.util.HashMap;
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class Allergens {
    private HashMap<String, ImageIcon> iconMap;
    private String[] names;
    
    /**
     * Defines the total number of allergens
     */
    public static final int TOTAL_NUMBER_OF_ALLERGENS = 14;
    
    public static final int ICON_SIZE_SIXTEEN = 16;
    public static final int ICON_SIZE_TWENTYONE = 21;
    public static final int DEFAULT = 0;
    
    public Allergens()
    {
        // do nothing;
    }
    
    public Allergens(boolean generateIconNames, int iconSize)
    {
        if(generateIconNames)
            generateIconNames();
        
        switch(iconSize)
        {
            case ICON_SIZE_SIXTEEN:
                generateIcons(ICON_SIZE_SIXTEEN);
                break;
            case ICON_SIZE_TWENTYONE:
                generateIcons(ICON_SIZE_TWENTYONE);
                break;
            case DEFAULT:
            default:
                break;
        }
    }
    
    public void generateIconNames()
    {
        names = new String[]{
            "Wheat",
            "Crustances",
            "Eggs",
            "Fish",
            "Peanut",
            "Soya",
            "Milk",
            "Tree Nut",
            "Celery",
            "Mustard",
            "Sesame",
            "Sulphur Dioxide",
            "Lupin",
            "Molluscs"
        };
    }
    
    /**
     * Takes following parameters as argument: {@link Allergens#SIZE_SIXTEEN}, {@link Allergens#SIZE_TWENTYONE}, {@link Allergens#DEFAULT}
     * @param size 
     */
    
    public void generateIcons(Integer size)
    {
        iconMap = new HashMap<>(TOTAL_NUMBER_OF_ALLERGENS);
        String location = String.format("src/allergens/images/size/%d/",size);
        
        iconMap.put("1", new ImageIcon(location+"wheat.png"));
        iconMap.put("2", new ImageIcon(location+"crustances.png"));
        iconMap.put("3", new ImageIcon(location+"eggs.png"));
        iconMap.put("4", new ImageIcon(location+"fish.png"));
        iconMap.put("5", new ImageIcon(location+"peanut.png"));
        iconMap.put("6", new ImageIcon(location+"soya.png"));
        iconMap.put("7", new ImageIcon(location+"milk.png"));
        iconMap.put("8", new ImageIcon(location+"treenut.png"));
        iconMap.put("9", new ImageIcon(location+"celery.png"));
        iconMap.put("10", new ImageIcon(location+"mustard.png"));
        iconMap.put("11", new ImageIcon(location+"sesame.png"));
        iconMap.put("12", new ImageIcon(location+"sulphurdioxide.png"));
        iconMap.put("13", new ImageIcon(location+"lupin.png"));
        iconMap.put("14", new ImageIcon(location+"molluscs.png"));
    }
    
    /**
     * Before using this method be sure to generate icons by calling {@link Allergens#generateIcons()}
     * 
     * @param key is 1-based
     * @return 
     */
    public ImageIcon getIcon(int key)
    {
        return iconMap.get(String.valueOf(key));
    }
    
    /**
     * Before using this method be sure to generate names by calling {@link Allergens#generateIconNames()}
     * 
     * @param key is 1-based
     * @return 
     */
    public String getIconName(int key)
    {
        return names[key-1];
    }
    
    public boolean containsKey(int key)
    {
        return iconMap.containsKey(String.valueOf(key));
    }
    
}
