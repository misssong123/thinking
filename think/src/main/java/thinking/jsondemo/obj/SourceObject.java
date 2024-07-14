package thinking.jsondemo.obj;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 源对象
 */
@Data
public class SourceObject {
    private String name;
    private FullName fullName;
    private int age;
    private Date birthday;
    private List<String> hobbies;
    private Map<String, String> clothes;
    private List<SourceObject> friends;


}
