package wesicknessdect.example.org.wescanleaf.events;

import java.util.HashMap;
import java.util.Set;

public class QuizCheckedEvent {

    public final  HashMap<Integer, Set<Integer>> choices;
   public final int part_id;

    public QuizCheckedEvent(HashMap<Integer, Set<Integer>> choices, int part_id) {
        this.choices = choices;
        this.part_id = part_id;
    }
}
