package com.example.beauty.common.domain;

import java.util.Date;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cloth {
    /**
     * Id
     */
    private Integer id;

    /**
     * 衣服唯一编号
     */
    private String clothId;

    /**
     * 图片存放地址
     */
    private String picture;

    /**
     * 创建时间
     */
    private Date createTime;
}