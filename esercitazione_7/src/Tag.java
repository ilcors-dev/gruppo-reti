public enum Tag {
    ECHO("ECHO",1), CONGRESSO("CONGRESSO",2);

    private final String textTag;
    private final int val;

    Tag(String textTag, int val) {
        this.textTag = textTag;
        this.val = val;
    }

    public static boolean check(String textTag){
        for (Tag itemTag: Tag.values()) {
            if(itemTag.textTag.equals(textTag)) return true;
        }
        return false;
    }
}
