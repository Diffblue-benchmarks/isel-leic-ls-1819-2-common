package pt.isel.ls.html2;

public class Prop {

    private final String header;
    private final String value;

    public Prop(String header, String value) {

        this.header = header;
        this.value = value;
    }

    public String getHeader() {
        return header;
    }

    public String getValue() {
        return value;
    }
}
