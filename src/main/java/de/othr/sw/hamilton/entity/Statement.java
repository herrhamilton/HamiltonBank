package de.othr.sw.hamilton.entity;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

// TODO: transient
public class Statement {

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    @Temporal(TemporalType.DATE)
    private Date fromDay;

    @Temporal(TemporalType.DATE)
    private Date toDay;
}
