package bench.abd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Random;

public class Populate {
    private static Connection c;
    private static Statement s;
    private static int n = 2;
    private static int MAX = (int) Math.pow(2,n);
    private static Random rand = new Random();

    public Populate(Connection c){
        try {
            this.c = c;
            s = c.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void populate(){
        try {

            s.executeUpdate("create table client (id int, nome varchar, addr varchar);");
            s.executeUpdate("create table product (id int, descricao varchar, stock int, min int, max int);");
            s.executeUpdate("create table invoice (id serial, id_cliente int);");
            s.executeUpdate("create table InvoiceLine (id serial, InvoiceId int, ProductId int);");
            s.executeUpdate("create table encomenda (id int,productid int, supplier int, items int);");


            int prod_id, cli_id;
            //System.out.println(MAX);
            PreparedStatement ps_cliente = c.prepareStatement("insert into client values (?,?,?)");
            PreparedStatement ps_product = c.prepareStatement("insert into product values (?,?,?,?,?)");
            for (int i = 0;i<MAX;i++) {
                //prod_id = rand.nextInt(MAX) | rand.nextInt(MAX);
                prod_id = i;
                cli_id = i;
                ps_cliente.setInt(1,cli_id); ps_cliente.setString(2,"cli " + cli_id);
                ps_cliente.setString(3,"endereço " + cli_id);

                int stock = rand.nextInt(15) + 1, min = 1, max = rand.nextInt(15) + 17;

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

    public static Connection getC() {
        return c;
    }
    public static void close(){
        try{
            s.close();
            c.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            c = DriverManager.getConnection("jdbc:postgresql://localhost/invoices");
            s = c.createStatement();
            populate();
            close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
