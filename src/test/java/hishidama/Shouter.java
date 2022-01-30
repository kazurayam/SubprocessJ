package hishidama;

public class Shouter {

    public static void main(String[] args) {
        for (int i = 0; i <= 256; i++) {
            System.out.print("OUT! ");
            System.err.print("ERR! ");
        }
    }
}