package bench;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


public class Populate {
    private Connection c;
    private Statement s;
    private int n = 10;
    private int MAX = (int) Math.pow(2,n);

    public Populate(){
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost/invoices");
            s = c.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void close(){
        try{
        s.close();
        c.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populate(){
        try {


            Random rand = new Random();
            s.executeUpdate("create table cliente (id int, nome varchar, addr varchar);");
            s.executeUpdate("create table produto (id int, descricao varchar);");
            s.executeUpdate("create table faturas (id int, id_produto int, id_cliente int);");



            int prod_id = 0, cli_id = 0;
            System.out.println(MAX);
            for (int i = 0;i<MAX;i++) {
                prod_id = rand.nextInt(MAX) | rand.nextInt(MAX);
                cli_id = rand.nextInt(MAX) | rand.nextInt(MAX);
                s.executeUpdate("insert into cliente values (" + cli_id + ", 'cli " + cli_id +"', 'endereço " + cli_id+"');");
                s.executeUpdate("insert into produto values (" + prod_id + ", 'produto " + prod_id +"');");
                //System.out.println(i);
            }

            /*sell(0,prod_id,cli_id);

            s.executeUpdate("create materialized view top as select id_produto, count (*) as soma from faturas " +
                    "group by id_produto order by soma DESC limit 10;");

            s.executeUpdate("create or replace function refresh() returns trigger as $$ begin " +
                    "        refresh materialized view top;return null;end;$$ " +
                    "        language plpgsql;");

            s.executeUpdate("create trigger ref_trigger after insert or update on faturas " +
                    "        for each statement execute procedure refresh();");


            s.executeUpdate("create index ind_cliente on faturas (id_cliente);");*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sell(int invoice, int produto, int cliente){
        try {

            s.executeUpdate("insert into faturas values (" + invoice + ","+ produto +"," + cliente +");");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet account(int cliente){
        ResultSet rs = null;
        try {

            rs = s.executeQuery(
                    "select descricao from faturas join produto on (faturas.id_produto = produto.id) where id_cliente ="+ cliente +";");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;

    }

    public ResultSet topTen(){
        ResultSet rs = null;

        try{
            //s.executeQuery("select id_produto, count (*) as soma from faturas group by id_produto order by soma DESC limit 10;");
            s.executeQuery("select * from top");
        }catch(Exception e){
            e.printStackTrace();
        }

        return rs;
    }

}





