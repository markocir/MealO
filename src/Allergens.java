
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
    private ImageIcon[] iconMap;
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
    
    public void generateIcons(int size)
    {
        iconMap = new ImageIcon[Allergens.TOTAL_NUMBER_OF_ALLERGENS];
        String location = String.format("src/allergens/images/size/%d/",size);
        
        iconMap[0] = new ImageIcon(location+"wheat.png");
        iconMap[1] = new ImageIcon(location+"crustances.png");
        iconMap[2] = new ImageIcon(location+"eggs.png");
        iconMap[3] = new ImageIcon(location+"fish.png");
        iconMap[4] = new ImageIcon(location+"peanut.png");
        iconMap[5] = new ImageIcon(location+"soya.png");
        iconMap[6] = new ImageIcon(location+"milk.png");
        iconMap[7] = new ImageIcon(location+"treenut.png");
        iconMap[8] = new ImageIcon(location+"celery.png");
        iconMap[9] = new ImageIcon(location+"mustard.png");
        iconMap[10] = new ImageIcon(location+"sesame.png");
        iconMap[11] = new ImageIcon(location+"sulphurdioxide.png");
        iconMap[12] = new ImageIcon(location+"lupin.png");
        iconMap[13] = new ImageIcon(location+"molluscs.png");
    }
    
    /**
     * Before using this method be sure to generate icons by calling {@link Allergens#generateIcons()}
     * 
     * @param key is 1-based
     * @return 
     */
    public ImageIcon getIcon(int key)
    {
        return iconMap[--key];
    }
    
    /**
     * Before using this method be sure to generate names by calling {@link Allergens#generateIconNames()}
     * 
     * @param key is 1-based
     * @return 
     */
    public String getIconName(int key)
    {
        return names[--key];
    }
    
    public boolean containsKey(int key)
    {
        if(iconMap.length >= key)
            return true;
        else
            return false;
    }
    
}
