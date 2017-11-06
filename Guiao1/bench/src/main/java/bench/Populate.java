package bench;

import java.sql.*;
import java.util.Random;


public class Populate {
    private Connection c;
    private Statement s;
    private int n = 10;
    private int MAX = (int) Math.pow(2,n);

    public Populate(Connection c){
        try {
            this.c = c;
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

            s.executeUpdate("create table client (id int, nome varchar, addr varchar);");
            s.executeUpdate("create table product (id int, descricao varchar, stock int, min int, max int);");
            s.executeUpdate("create table invoice (id int, id_cliente int);");
            s.executeUpdate("create table InvoiceLine (id int, InvoiceId int, ProductId int);");
            s.executeUpdate("create table encomenda (id int,productid int, supplier int, items int);");


            int prod_id = 0, cli_id = 0;
            Random r = new Random();
            System.out.println(MAX);
            PreparedStatement ps_cliente = c.prepareStatement("insert into client values (?,?,?)");
            PreparedStatement ps_product = c.prepareStatement("insert into product values (?,?,?,?,?)");
            for (int i = 0;i<MAX;i++) {
                prod_id = rand.nextInt(MAX) | rand.nextInt(MAX);
                cli_id = rand.nextInt(MAX) | rand.nextInt(MAX);
                ps_cliente.setInt(1,cli_id); ps_cliente.setString(2,"cli " + cli_id);
                ps_cliente.setString(3,"endereço " + cli_id);

                int stock = r.nextInt(15) + 1, min = 1, max = r.nextInt(15) + 17;

                ps_product.setInt(1,prod_id); ps_product.setString(2,"produto " + prod_id);
                ps_product.setInt(3,stock); ps_product.setInt(4,min); ps_product.setInt(5,max);

                ps_cliente.executeUpdate(); ps_product.executeUpdate();
                //System.out.println(i);
            }


/*
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





