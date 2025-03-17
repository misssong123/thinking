package book.dsl.param;

import book.dsl.renewal.EsContainsNull;
import book.dsl.renewal.EsField;
import book.dsl.renewal.EsIgnoreField;
import book.dsl.renewal.EsRange;
import lombok.Data;

import java.util.List;

@Data
public class RenewalOppSearchActualParam {
    //产品线
    @EsField(name = "productLineId")
    private Integer productLine;
    //库类型
    @EsField
    private Integer libTypeId;
    //归属销售
    @EsField
    private String ownerBspId;
    //归属部门
    @EsField(name = "owningBUbspId")
    private List<String> owningBUBspIds;
    //基准ID
    @EsField
    private String basicLeadId;
    //电话
    @EsField(name = "allPhones")
    private String phone;
    //商机id
    @EsField(name = "oppId")
    private List<String> oppIds;
    //公司名称
    @EsField
    private String customer;
    //城市
    @EsField(name = "cityBspId")
    private List<Integer> cityBspIds;
    //服务开始时间 todo
    @EsRange(field = "orderBeginTimeArrs")
    private List<Long> orderStartTime;
    //服务结束时间 todo
    @EsRange(field = "orderEndTimeArrs")
    private List<Long> orderEndTime;
    //跟进时间
    @EsRange(field = "lastTraceTime")
    private List<String> lastTraceTime;
    //用户系统标签
    @EsField(name = "userSystemTag")
    private List<String> userSystemTags;
    //意向度
    @EsField
    private Integer intentionDegree;
    //会员类型
    @EsField(name = "membershipType")
    private List<String> membershipTypes;
    //续费周期
    @EsField(name = "renewalCycle")
    private List<String> renewalCycles;
    //招聘新行业
    @EsField
    private List<String> sysTags;
    //未续费原因
    @EsField(name = "userNonRenewalReason")
    private List<String> userNonRenewalReasons;
    //客户标签
    @EsField(name="userCustomerTag")
    private List<String> userCustomerTags;
    //客户类型
    @EsContainsNull(fieldName = "userCustomerType")
    private List<String> userCustomerTypes;
    //页码，大小
    @EsIgnoreField
    private int pageSize;
    @EsIgnoreField
    private int pageIndex;
    //排序字段集合
    @EsIgnoreField
    private List<SortVo> sortVoList;
    //辅助字段 深度分页
    @EsIgnoreField
    private String assistValue;
    //辅助字段 深度分页
    @EsIgnoreField
    private String secondAssistValue;
}
