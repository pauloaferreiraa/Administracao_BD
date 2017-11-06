package bench.abd;

import bench.Populate;
import com.sun.org.apache.bcel.internal.generic.POP;

import java.sql.*;
import java.util.Random;

public class Run extends Thread {

    // Gerar o numero da fatura
    private static int n = 0;

    private static synchronized int next() {
        return n++;
    }

    private static long ultima = -1;

    private static long iaa = 0, tra = 0, c = 0;
    private static boolean start = false;

    private static synchronized void regista(long antes, long depois) {


        long tr = depois-antes;

        long anterior = ultima;
        ultima = depois;

        if (anterior < 0 || !start)
            return;

        long ia = depois - anterior;

        iaa += ia;
        tra += tr;
        c++;
    }

    public static synchronized void partida() {
        start = true;
    }

    public static synchronized void imprime() {
        double trm = (tra/1e9d)/c;
        double debito = 1/((iaa/1e9d)/c);

        System.out.println("debito = "+debito+" tps, tr = "+trm+" s");

    }

    public void run() {

        Random r = new Random();
        Populate p = new Populate();

        while(true) {

            long antes = System.nanoTime();

            // EXECUTAR OP! (switch, executeQuery, ...)
            ResultSet rs = null;
            switch (r.nextInt(3)){
                case 0:
                    Random prod = new Random();
                    Random cli = new Random();
                    p.sell(next(),prod.nextInt(1024),cli.nextInt(1024));
                    break;
                case 1:
                    Random cliente = new Random();
                    rs = p.account(cliente.nextInt(1024));
                    try{
                        while(rs.next()){
                            //System.out.println(rs.getString(1));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    p.topTen();
                    break;
                default:
                    break;
            }
            /*
            try {
                Thread.sleep(r.nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            // ---

            long depois = System.nanoTime();
            regista(antes, depois);

        }

    }

    public static void main(String[] args) throws Exception {
        Populate p = new Populate();
        p.populate();
        for (int j = 0; j < 1; j++) {
            new Run().start();
        }

        Thread.sleep(5000);

        partida();

        Thread.sleep(10000);

        imprime();

        System.exit(0);

    }
}
