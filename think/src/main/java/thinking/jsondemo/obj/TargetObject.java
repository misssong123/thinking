package thinking.jsondemo.obj;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 目标对象
 */
@Data
public class TargetObject {
    private String name;
    private FullName fullName;
    private int age;
    private Date birthday;
    private List<String> hobbies;
    private Map<String, String> clothes;
    private List<TargetObject> friends;
    private String name1;
}
