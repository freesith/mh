package com.intelcupid.plugin;

import com.alibaba.fastjson.JSON;

import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.TaskAction;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class MoxyTask extends DefaultTask {

    public String configPath;
    public String assetPath;
    public int hello;

    public MoxyTask() {
        System.out.println("MoxyTask()");
    }

    @TaskAction
    public void parseConfig() {


        configPath="/Users/edz/projects/Moxy/mox/config";
        String mockDir = configPath + "/" + "mock";
        File file = new File(mockDir);
        System.out.println("filePath = " + file.getAbsolutePath()  + "  exist = " + file.exists());
        File[] files = file.listFiles();
        System.out.println("files = " + files.length);
        System.out.println("configPath = " + configPath);
        System.out.println("hello = " + hello);
        System.out.println("assetPath = " + assetPath);


        Connection connection = null;
        try
        {

            long start = System.currentTimeMillis();
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'lzzzzzzo')");
            statement.executeUpdate("insert into person values(2, 'yui')");

            statement.executeUpdate("drop table if exists mock");
            statement.executeUpdate("create table mock (name varchar(20),protocal text, host text, path texxt, response text, status integer)");
            for (File f: files) {
                parseMock(f, statement);
            }

            ResultSet rs = statement.executeQuery("select * from mock");
            while(rs.next())
            {
                System.out.println("name = " + rs.getString("name"));
                System.out.println("protocal = " + rs.getString("protocal"));
                System.out.println("host = " + rs.getString("host"));
                System.out.println("path = " + rs.getString("path"));
                System.out.println("response = " + rs.getString("response"));
                System.out.println("status = " + rs.getInt("status"));
            }

            System.out.println("cost = " + (System.currentTimeMillis() - start));
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

    }


    /**
     * statement.executeUpdate("create table mock (name varchar(20),protocal text, host text, path text, response test, status integer)");
     * @param file
     * @param statement
     */
    private void parseMock(File file, Statement statement ) {
        System.out.println("parse mock file = " + file.getName());
        String name = file.getName();
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(file);
            Mock mock = JSON.parseObject(fis, Mock.class);
            System.out.println(JSON.toJSONString(mock));
            StringBuilder builder = new StringBuilder();

            builder.append("insert into mock values (")
                    .append("'").append(file.getName()).append("'").append(",")
                    .append("'").append(Util.join(mock.request.protocal,",")).append("'").append(",")
                    .append("'").append(Util.join(mock.request.host,",")).append("'").append(",")
                    .append("'").append(mock.request.path).append("'").append(",")
                    .append("'").append(JSON.toJSONString(mock.response)).append("'").append(",")
                    .append(1)
                    .append(")");
            try {
                statement.executeUpdate(builder.toString());

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }




//            int length = fis.available();
//            byte[] buffer = new byte[length];
//            fis.read(buffer);
//            fis.close();
//            res = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
