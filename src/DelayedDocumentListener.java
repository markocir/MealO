
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Uses a timer to delay the action forwarded to this class.
 * @author Marko Čirič <https://github.com/markocir>
 */
public  class DelayedDocumentListener implements DocumentListener {

    private final Timer timer;

    public DelayedDocumentListener(int timeOut, ActionListener listener, boolean repeats) 
    {
        timer = new Timer(timeOut, listener);
        timer.setRepeats(repeats);
    }

    @Override
    public void insertUpdate(DocumentEvent e) 
    {
        if(!timer.isRunning())
            timer.start();
        timer.restart();
    }

    @Override
    public void removeUpdate(DocumentEvent e) 
    {
        // do nothing
    }

    @Override
    public void changedUpdate(DocumentEvent e) 
    {
        // do nothing
    }

}
