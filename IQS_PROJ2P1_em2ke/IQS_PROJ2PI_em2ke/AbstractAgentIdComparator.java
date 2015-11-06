/**
 *
 * @author lbarnett
 */
import java.util.Comparator;

public class AbstractAgentIdComparator implements Comparator<AbstractAgent>{

    @Override
    public int compare(AbstractAgent t, AbstractAgent t1) {
        String firstId = t.getId();
        String secondId = t1.getId();
        
        return firstId.compareTo(secondId);
    }
    
}
