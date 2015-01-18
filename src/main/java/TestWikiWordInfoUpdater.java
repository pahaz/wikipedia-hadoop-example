import junit.framework.TestCase;

public class TestWikiWordInfoUpdater extends TestCase {
    public void testAdd() {
        WikiWordInfoUpdater u = new WikiWordInfoUpdater();
        u.reset();

        WikiWordInfo info = new WikiWordInfo();
        info.set(new WikiWordInfoEntry("1", 2f, 22));
        assertEquals(info.getEntries()[0].tf, 2f);
        assertEquals(info.getEntries()[0].id, "1");
        assertEquals( (int) info.getEntries()[0].freq, (int) 22);

        u.add(info);
        u.add(info);

        WikiWordInfo i1 = new WikiWordInfo();
        u.update(i1);

        assertEquals(i1.getEntries().length, 2);
    }
}
