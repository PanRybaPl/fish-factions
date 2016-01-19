/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public class StringUtils {
    public static String join(String[] strings) {
        return join(strings, 0);
    }
    
    public static String join(String[] strings, int startAt) {
        String result = "";
        
        for(int i = startAt; i < strings.length; ++i) {
            if(!result.isEmpty()) {
                result += " ";
            }
            
            result += strings[i];
        }
        
        return result;
    }
}
