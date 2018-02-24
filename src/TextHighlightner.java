
/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class TextHighlightner{
    
    public String setTextToHighlight(final String text, String textToHighlight)
    {
        // if filter text is empty
        if (textToHighlight == null)
            return text;
        
        textToHighlight = textToHighlight.trim().toLowerCase();
        if(textToHighlight.length() == 0)
            return text;
        
        String textToMatch = text.toLowerCase();
        StringBuilder sb = new StringBuilder();
        
        if(textToMatch.contains(textToHighlight))
        {
            sb.append("<html>");
            int i = 0;
            int position = 0;
            
            while(true)
            {
                i = textToMatch.indexOf(textToHighlight, i);
                
                // if no further matches are found
                if(i == -1)
                {
                    if(position < text.length())
                        sb.append(text.substring(position, text.length()));
                    break;
                }
                     
                
                if(position < i)
                    sb.append(text.substring(position, i));
                
                if(i+textToHighlight.length() <= text.length())
                {
                    sb.append("<span style='color:#000000; background-color: #FFFF00;'>");
                    sb.append(text.substring(i, i+textToHighlight.length()));
                    sb.append("</span>");
                }
                i += textToHighlight.length();
                position = i;
            }
            
            sb.append("</html>");
        }
        
        return sb.toString().length() == 0 ? text : sb.toString();
    }
}
