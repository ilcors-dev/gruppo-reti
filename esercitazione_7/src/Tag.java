public enum Tag {
    ECHO("ECHO",1), CONGRESSO("CONGRESSO",2);

    private final String textTag;
    private final int val;

    Tag(String textTag, int val) {
        this.textTag = textTag;
        this.val = val;
    }
    
}
