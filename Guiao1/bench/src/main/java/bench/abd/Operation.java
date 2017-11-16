package bench.abd;

import java.sql.*;
import java.util.Random;


public class Operation {
    private Connection c;
    private Statement s;
    private int n = 2;
    private int MAX = (int) Math.pow(2,n);
    private Random rand = new Random();

    

    public Operation(Connection c){
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

    

    public boolean checkProductStock(int product, int q) throws Exception{ //checks whether product has a stock higher than q
        boolean valid = false;

        PreparedStatement ps = c.prepareStatement("select stock from product where id = ?");
        ps.setInt(1, product);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (rs.getInt(1) >= q) {
                valid = true;
                break;
            }
        }
        return valid;
    }


    public void sell(int cliente){
        try {
            PreparedStatement ps_invoice = c.prepareStatement("insert into invoice (id_cliente) values (?)",Statement.RETURN_GENERATED_KEYS);
            ps_invoice.setInt(1,cliente);
            ps_invoice.executeUpdate();
            ResultSet rs_invoice  = ps_invoice.getGeneratedKeys();

            int invoice = 0;
            if(rs_invoice.next()){
                invoice = rs_invoice.getInt(1);
            }
            int invoice_lines = rand.nextInt(10) + 1;

            PreparedStatement ps_inline = c.prepareStatement("insert into InvoiceLine (InvoiceId, ProductId) values (?,?)");
            for(int i = 0;i<invoice_lines;i++){
                int product = rand.nextInt(MAX) | rand.nextInt(MAX);
                if(!checkProductStock(product,1)){
                    continue;
                }
                ps_inline.setInt(1,invoice);
                ps_inline.setInt(2,product);
                ps_inline.executeUpdate();
                PreparedStatement ps = c.prepareStatement("update product set stock = stock - 1 where id = ?");
                ps.setInt(1,product);
                ps.executeUpdate();
            }

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

    public void topTen(){
        ResultSet rs = null;

        try{
            //s.executeQuery("select id_produto, count (*) as soma from faturas group by id_produto order by soma DESC limit 10;");
            s.executeQuery("select * from top");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}





