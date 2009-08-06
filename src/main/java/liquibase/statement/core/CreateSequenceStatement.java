package liquibase.statement.core;

import liquibase.statement.SqlStatement;

import java.math.BigInteger;

public class CreateSequenceStatement implements SqlStatement {

    private String schemaName;
    private String sequenceName;
    private BigInteger startValue;
    private BigInteger incrementBy;
    private BigInteger maxValue;
    private BigInteger minValue;
    private Boolean ordered;

    public CreateSequenceStatement(String schemaName, String sequenceName) {
        this.schemaName = schemaName;
        this.sequenceName = sequenceName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public BigInteger getStartValue() {
        return startValue;
    }

    public CreateSequenceStatement setStartValue(BigInteger startValue) {
        this.startValue = startValue;
        return this;
    }

    public BigInteger getIncrementBy() {
        return incrementBy;
    }

    public CreateSequenceStatement setIncrementBy(BigInteger incrementBy) {
        this.incrementBy = incrementBy;
        return this;
    }

    public BigInteger getMaxValue() {
        return maxValue;
    }

    public CreateSequenceStatement setMaxValue(BigInteger maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public BigInteger getMinValue() {
        return minValue;
    }

    public CreateSequenceStatement setMinValue(BigInteger minValue) {
        this.minValue = minValue;
        return this;
    }

    public Boolean getOrdered() {
        return ordered;
    }

    public CreateSequenceStatement setOrdered(Boolean ordered) {
        this.ordered = ordered;
        return this;
    }
}
