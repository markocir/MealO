import javax.swing.JButton;

public class ShiftButton extends JButton{
    int shiftValue = -1;
    
    public ShiftButton(String string)
    {
        super(string);
    }
    
    public ShiftButton(String string, int value)
    {
        super(string);
        shiftValue = value;
    }
    
    public int getValue()
    {
        return shiftValue;
    }
}
