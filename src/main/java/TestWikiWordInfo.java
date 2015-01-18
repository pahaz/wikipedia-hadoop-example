import junit.framework.TestCase;

public class TestWikiWordInfo extends TestCase {
    public void testToStrings() {
        WikiWordInfo info = new WikiWordInfo();

        WikiWordInfoEntry[] e = new WikiWordInfoEntry[]{
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
        };
        info.set(e);

        assertEquals(info.toString(), "2#1:2.0\t1:2.0");
    }
}
