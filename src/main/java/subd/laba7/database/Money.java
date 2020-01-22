package subd.laba7.database;

import java.sql.SQLType;

public class Money implements SQLType {
    @Override
    public String getName() {
        return "money";
    }

    @Override
    public String getVendor() {
        return null;
    }

    @Override
    public Integer getVendorTypeNumber() {
        return null;
    }
}
