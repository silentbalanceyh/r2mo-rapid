package io.r2mo.base.dbe.relation;

import lombok.Getter;

/**
 * @author lang : 2025-10-18
 */
@Getter
public class JoinOn {

    private String from;
    private String fromBy;
    private String to;
    private String toBy;

    public JoinOn from(final String table, final String field) {
        this.from = table;
        this.fromBy = field;
        return this;
    }

    public JoinOn to(final String table, final String field) {
        this.to = table;
        this.toBy = field;
        return this;
    }

    @Override
    public String toString() {
        return "Linkage{" +
            "from='" + this.from + '\'' +
            ", fromBy='" + this.fromBy + '\'' +
            ", to='" + this.to + '\'' +
            ", toBy='" + this.toBy + '\'' +
            '}';
    }
}
