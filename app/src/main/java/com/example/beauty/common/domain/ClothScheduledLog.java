package com.example.beauty.common.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClothScheduledLog {
    /**
     * id
     */
    private Integer id;

    /**
     * 衣服Id
     */
    private String clothId;

    /**
     * 预定使用时间
     */
    private Date scheduledTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}