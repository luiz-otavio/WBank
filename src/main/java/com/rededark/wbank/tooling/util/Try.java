package com.rededark.wbank.tooling.util;

public class Try {

    public static void of(Service service) {
        try {
            service.onTry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T query(Query<T> query) {
        try {
            return query.query();
        } catch (Exception e) {
            e.printStackTrace();
        } return null;
    }

    public interface Service { void onTry() throws Exception; }

    public interface Query<T> { T query() throws Exception; }

}
