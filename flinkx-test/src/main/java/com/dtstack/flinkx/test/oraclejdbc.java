package com.dtstack.flinkx.test;

import com.dtstack.flinkx.rdb.util.DbUtil;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class oraclejdbc {
    public static void main(String[] args) {
        Connection dbConn = null;
        ArrayList ls = new ArrayList<>();
        try {
            dbConn = DbUtil.getConnection("jdbc:oracle:thin:@10.158.5.84:1521:dbm", "ogg", "ogg");
            PreparedStatement statement = dbConn.prepareStatement("SELECT ID,DEPARTMENT_CHINESE_NAME FROM HRA00_DEPARTMENT", 1007);
            statement.setFetchSize(1000);
            statement.setQueryTimeout(60);
            ResultSet resultSet = statement.executeQuery();
            boolean hasNext = resultSet.next();

            Object[] lst = new Object[2];


            while (hasNext) {
                String dep_id = resultSet.getString("ID");
                String dep_name = resultSet.getString("DEPARTMENT_CHINESE_NAME");
                lst[0] = dep_id;
                lst[1] = dep_name;
                ls.add(lst);
                System.out.println(dep_id + "-" + dep_name);
                hasNext = resultSet.next();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


//        val senv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
//        val blinkStreamSettings: EnvironmentSettings = EnvironmentSettings
//                .newInstance
//                .useBlinkPlanner
//                .inStreamingMode.build
//
//        val blinkStreamTableEnv = StreamTableEnvironment.create(senv, blinkStreamSettings)


        try {

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            StreamTableEnvironment streamTableEnvironment = StreamTableEnvironment.create(env);

            DataStreamSource dataStreamSource = env.fromCollection(ls);

            streamTableEnvironment.createTemporaryView("t", dataStreamSource);

            streamTableEnvironment.sqlQuery("select * from t");
//            env.execute("x");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
