import junit.framework.TestCase;

public class TestWikiWordInfo extends TestCase {
    public void testToStrings() {
        WikiWordInfo info = new WikiWordInfo();

        WikiWordInfoEntry[] e = new WikiWordInfoEntry[]{
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
        };

        assertEquals(e.length, 4);

        info.set(e);

        assertEquals(info.getEntries().length, 4);
        assertEquals(info.toString(), "1:2.0\t1:2.0\t1:2.0\t1:2.0");
    }

    public void testToStringsWithMaxResults() {
        WikiWordInfo info = new WikiWordInfo(20, 2);

        WikiWordInfoEntry[] e = new WikiWordInfoEntry[]{
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
                new WikiWordInfoEntry("1", 2f, 22),
        };

        assertEquals(e.length, 4);

        info.set(e);

        assertEquals(info.getEntries().length, 4);
        assertEquals(info.toString(), "1:2.0\t1:2.0");
    }
}
