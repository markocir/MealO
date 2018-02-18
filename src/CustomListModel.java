
import javax.swing.DefaultListModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class CustomListModel<E> extends DefaultListModel<E>{
    
    public void fireContentsChanged(int i)
    {
        super.fireContentsChanged(this, 0, 20);
    }
}
