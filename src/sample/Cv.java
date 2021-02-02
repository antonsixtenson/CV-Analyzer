package sample;

public class Cv {

    protected String filepath;
    protected String name;
    protected double hk_matches;
    protected double sk_matches;
    protected double d_key;

    public Cv(String filepath, String name, double hk_matches, double sk_matches, double d_key) {
        this.filepath = filepath;
        this.name = name;
        this.hk_matches = hk_matches;
        this.sk_matches = sk_matches;
        this.d_key = d_key;

    }

    public String getName(){
        return name;
    }

    public String getFilepath(){
        return filepath;
    }

    public double getHkMatches(){
        return hk_matches;
    }

    public double getSkMatches(){
        return sk_matches;
    }

    public double getDKey(){
        return d_key;
    }
}
