package book.innerclass;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    private List<Event> eventList = new ArrayList<>();
    public void addEvent(Event e) {
        eventList.add(e);
    }
    public void run() {
        while (eventList.size() > 0) {
            //创建一个副本，防止在遍历过程中修改eventList
            for (Event e : new ArrayList<>(eventList)) {
                if (e.ready()) {
                    System.out.println(e);
                    e.action();
                    eventList.remove(e);
                }
            }
        }
    }
}
