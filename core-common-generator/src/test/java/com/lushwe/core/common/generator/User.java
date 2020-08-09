package com.lushwe.core.common.generator;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 说明：TODO 写点注释吧
 *
 * @author Jack Liu
 * @date 2019-07-03 16:32
 * @since 1.0
 */
@Data
public class User {

    private Long id;
    private String name;
    private Integer age;
    private Boolean open;
    private Date createTime;
    private List<Long> userIds;
}
