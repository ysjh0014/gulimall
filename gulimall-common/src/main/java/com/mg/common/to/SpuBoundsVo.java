package com.mg.common.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SpuBoundsVo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
